package com.parallelsymmetry.escape.utility.agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.parallelsymmetry.escape.utility.ThreadUtil;
import com.parallelsymmetry.escape.utility.log.Log;

public abstract class PipeAgent extends Agent implements Pipe {

	private static final int DEFAULT_RECONNECT_DELAY = 5000;

	private static final boolean DEFAULT_STOP_ON_EXCEPTION = false;

	private static final boolean DEFAULT_STOP_ON_CONNECT_EXCEPTION = false;

	private static final boolean DEFAULT_CONNECT_ONCE = false;

	private long reconnectDelay = DEFAULT_RECONNECT_DELAY;

	private boolean stopOnException = DEFAULT_STOP_ON_EXCEPTION;

	private boolean stopOnConnectException = DEFAULT_STOP_ON_CONNECT_EXCEPTION;

	private boolean connectOnce = DEFAULT_CONNECT_ONCE;

	private InputStream input = new AgentInputStream();

	private OutputStream output = new AgentOutputStream();

	private volatile InputStream realInput;

	private volatile OutputStream realOutput;

	private boolean connected;

	public PipeAgent() {
		this( null, false );
	}

	public PipeAgent( boolean stopOnException ) {
		this( null, stopOnException );
	}

	public PipeAgent( String name ) {
		this( name, false );
	}

	public PipeAgent( String name, boolean stopOnException ) {
		super( name );
		this.stopOnException = stopOnException;
	}

	public final boolean isConnected() {
		return connected;
	}

	public long getReconnectDelay() {
		return reconnectDelay;
	}

	public void setReconnectDelay( long reconnectDelay ) {
		this.reconnectDelay = reconnectDelay;
	}

	/**
	 * Should the agent connect only once.
	 * 
	 * @return
	 */
	public boolean isConnectOnce() {
		return connectOnce;
	}

	/**
	 * Set if the agent should connect only once.
	 * 
	 * @param connectOnce
	 */
	public void setConnectOnce( boolean connectOnce ) {
		this.connectOnce = connectOnce;
	}

	public boolean isStopOnException() {
		return stopOnException;
	}

	public void setStopOnException( boolean stopOnException ) {
		this.stopOnException = stopOnException;
	}

	public boolean isStopOnConnectException() {
		return stopOnConnectException;
	}

	public void setStopOnConnectException( boolean stopOnConnectException ) {
		this.stopOnConnectException = stopOnConnectException;
	}

	public InputStream getInputStream() {
		return input;
	}

	public OutputStream getOutputStream() {
		return output;
	}

	protected InputStream getRealInputStream() {
		return realInput;
	}

	protected void setRealInputStream( InputStream input ) {
		synchronized( this.input ) {
			realInput = input;
			this.input.notifyAll();
		}
	}

	protected OutputStream getRealOutputStream() {
		return realOutput;
	}

	protected void setRealOutputStream( OutputStream output ) {
		synchronized( this.output ) {
			realOutput = output;
			this.output.notifyAll();
		}
	}

	@Override
	protected void startAgent() throws Exception {
		reconnect( true );
	}

	@Override
	protected void stopAgent() throws Exception {
		internalDisconnect();
	}

	protected abstract void connect() throws Exception;

	protected abstract void disconnect() throws Exception;

	protected void reconnect() {
		reconnect( false );
	}

	protected void reconnect( boolean start ) {
		reconnect( start, 0 );
	}

	protected void reconnect( boolean start, int attempts ) {
		Log.write( Log.DEBUG, getName(), " reconnecting..." );
		int attempt = 0;
		while( shouldExecute() && ( attempts == 0 || ( attempt < attempts ) ) ) {
			if( attempts > 0 ) attempt++;
			try {
				if( connected ) internalDisconnect();
				internalConnect();
				break;
			} catch( Exception exception ) {
				if( start && ( connectOnce || stopOnConnectException ) ) {
					Log.write( getName() + " failed to connect!" );
					Log.write( Log.ERROR, exception );
				} else {
					Log.write( getName() + " failed to connect! Waiting " + (int)( reconnectDelay / 1000.0 ) + " seconds..." );
					Log.write( Log.ERROR, exception );
					ThreadUtil.pause( reconnectDelay );
				}
			}
		}
		Log.write( Log.TRACE, getName(), " reconnected." );
	}

	private final void internalConnect() throws Exception {
		fireEvent( State.CONNECTING );
		connect();
		fireEvent( State.CONNECTED );
		connected = true;
	}

	private final void internalDisconnect() throws Exception {
		fireEvent( State.DISCONNECTING );
		disconnect();
		fireEvent( State.DISCONNECTED );
		connected = false;
	}

	private class AgentInputStream extends InputStream {

		private void checkForReadablility() {
			synchronized( input ) {
				while( realInput == null ) {
					try {
						input.wait();
					} catch( InterruptedException exception ) {
						// Intentionally ignore exception.
					}
				}
			}
		}

		@Override
		public int read() throws IOException {
			checkForReadablility();
			try {
				int bite = realInput.read();
				if( bite < 0 ) {
					if( !isRunning() || isConnectOnce() ) return -1;
					reconnect();
					return read();
				}
				return bite;
			} catch( IOException exception ) {
				if( !isRunning() ) return -1;
				if( stopOnException || isConnectOnce() ) {
					stop();
					throw exception;
				}
				Log.write( Log.DEBUG, exception );
				reconnect();
				return read();
			}
		}

		@Override
		public int read( byte[] data ) throws IOException {
			checkForReadablility();
			try {
				int read = realInput.read( data );
				if( read < 0 ) {
					if( !isRunning() || isConnectOnce() ) return -1;
					reconnect();
					return read( data );
				}
				return read;
			} catch( IOException exception ) {
				if( !isRunning() ) return -1;
				if( stopOnException || isConnectOnce() ) {
					stop();
					throw exception;
				}
				Log.write( Log.DEBUG, exception );
				reconnect();
				return read( data );
			}
		}

		@Override
		public int read( byte[] data, int offset, int length ) throws IOException {
			checkForReadablility();
			try {
				int read = realInput.read( data, offset, length );
				if( read < 0 ) {
					if( !isRunning() || isConnectOnce() ) return -1;
					reconnect();
					return read( data, offset, length );
				}
				return read;
			} catch( IOException exception ) {
				if( !isRunning() || isConnectOnce() ) return -1;
				if( stopOnException ) {
					stop();
					throw exception;
				}
				Log.write( Log.DEBUG, exception );
				reconnect();
				return read( data, offset, length );
			}
		}

		@Override
		public void close() throws IOException {
			realInput.close();
		}

	}

	private class AgentOutputStream extends OutputStream {

		private void checkForWritablility() {
			synchronized( output ) {
				while( realOutput == null ) {
					try {
						output.wait();
					} catch( InterruptedException exception ) {
						// Intentionally ignore exception.
					}
				}
			}
		}

		@Override
		public void write( int bite ) throws IOException {
			checkForWritablility();
			try {
				realOutput.write( bite );
			} catch( IOException exception ) {
				if( !isRunning() || isConnectOnce() ) return;
				if( stopOnException ) {
					stop();
					throw exception;
				}
				reconnect();
				write( bite );
			}
		}

		@Override
		public void write( byte[] data ) throws IOException {
			checkForWritablility();
			try {
				realOutput.write( data );
			} catch( IOException exception ) {
				if( !isRunning() || isConnectOnce() ) return;
				if( stopOnException ) {
					stop();
					throw exception;
				}
				reconnect();
				write( data );
			}
		}

		@Override
		public void write( byte[] data, int offset, int length ) throws IOException {
			checkForWritablility();
			try {
				realOutput.write( data, offset, length );
			} catch( IOException exception ) {
				if( !isRunning() || isConnectOnce() ) return;
				if( stopOnException ) {
					stop();
					throw exception;
				}
				reconnect();
				write( data, offset, length );
			}
		}

		@Override
		public void flush() throws IOException {
			checkForWritablility();
			try {
				realOutput.flush();
			} catch( IOException exception ) {
				if( !isRunning() || isConnectOnce() ) return;
				if( stopOnException ) {
					stop();
					throw exception;
				}
				reconnect();
				flush();
			}
		}

		@Override
		public void close() throws IOException {
			realOutput.close();
		}

	}
}
