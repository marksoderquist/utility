package com.parallelsymmetry.utility.agent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.parallelsymmetry.utility.log.Log;

public class SocketAgent extends PipeAgent {

	public static final int DEFAULT_CONNECT_TIMEOUT = 5000;

	private String host;

	private int port;

	private Socket socket;

	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

	public SocketAgent( int port ) {
		this( null, null, port );
	}

	public SocketAgent( String name, int port ) {
		this( name, null, port );
	}

	public SocketAgent( String name, String host, int port ) {
		super( name );
		this.host = host;
		this.port = port;
	}

	public int getConnectTimout() {
		return connectTimeout;
	}

	public void setConnectTimeout( int timeout ) {
		connectTimeout = timeout;
	}

	@Override
	protected void connect() throws IOException {
		String server = host == null ? "localhost" : host;
		Log.write( Log.DEBUG, getName() + ": Connecting to " + host + ":" + port + "..." );
		socket = new Socket();
		socket.connect( new InetSocketAddress( server, port ), connectTimeout );
		setRealInputStream( socket.getInputStream() );
		setRealOutputStream( socket.getOutputStream() );
		Log.write( getName() + ": Connected to: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() );
	}

	@Override
	protected void disconnect() throws IOException {
		Log.write( Log.DEBUG, getName() + ": Disconnecting..." );
		if( socket != null ) {
			Log.write( Log.DEBUG, getName() + ": Closing socket..." );
			socket.close();
			Log.write( Log.DEBUG, getName() + ": Socket closed." );
		}
		setRealInputStream( null );
		setRealOutputStream( null );
		Log.write( getName() + ": Disconnected." );
	}

}
