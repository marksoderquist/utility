package com.parallelsymmetry.utility.agent;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import com.parallelsymmetry.utility.agent.ServerAgent;
import com.parallelsymmetry.utility.agent.SocketAgent;
import com.parallelsymmetry.utility.log.Log;

/**
 * When using the AnyConnect VPN Client IPv6 is disabled and causes problems
 * with these tests. A workaround it to set -Djava.net.preferIPv4Stack=true.
 * This can be done globally in Eclipse by adding it to the default VM arguments
 * under Preferences>Java/Installed JREs:Select the JRE, Click the Edit button,
 * add to Default VM Arguments.
 * 
 * @author SoderquistMV
 */
public class SocketAgentTest extends TestCase {

	private TestServerAgent server = new TestServerAgent();

	@Override
	public void setUp() throws Exception {
		Log.setLevel( Log.NONE );
		server.startAndWait( 100, TimeUnit.MILLISECONDS );
		assertTrue( server.isRunning() );
		int localPort = server.getLocalPort();
		assertTrue( "Server port should be greater than zero: " + localPort, localPort > 0 );
	}

	public void testStartStop() throws Exception {
		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		agent.startAndWait();
		assertTrue( "Agent is not running.", agent.isRunning() );
		agent.stopAndWait();
		assertFalse( "Agent is not stopped.", agent.isRunning() );
		agent.startAndWait();
		assertTrue( "Agent is not running.", agent.isRunning() );
		agent.stopAndWait();
		assertFalse( "Agent is not stopped.", agent.isRunning() );
	}

	public void testConnect() throws Exception {
		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( "Agent should not be running.", agent.isRunning() );
		agent.startAndWait();
		assertTrue( "Agent is not running.", agent.isRunning() );

		assertTrue( "Agent is not connected.", agent.isConnected() );

		agent.stopAndWait();
		assertFalse( "Agent is not stopped.", agent.isRunning() );
	}

	public void testRestart() throws Exception {
		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( "Agent should not be running.", agent.isRunning() );
		agent.startAndWait();
		assertTrue( "Agent is not running.", agent.isRunning() );

		agent.restart();
		assertTrue( "Agent is not running.", agent.isRunning() );

		agent.stopAndWait();
		assertFalse( "Agent is not stopped.", agent.isRunning() );
	}

	// Potential problem with test not completing.
	//	public void testWrite() throws Exception {
	//		SocketAgent agent = new SocketAgent( server.getLocalPort() );
	//		assertFalse( "Agent should not be running.", agent.isRunning() );
	//		agent.startAndWait();
	//		assertTrue( "Agent is not running.", agent.isRunning() );
	//
	//		String message = "Test message.";
	//		agent.getOutputStream().write( message.getBytes( Charset.forName( "UTF-8" ) ) );
	//		assertEquals( "Incorrect message.", message, server.getMessage( message.length() ) );
	//
	//		agent.stopAndWait();
	//		assertFalse( "Agent is not stopped.", agent.isRunning() );
	//	}

	@Override
	public void tearDown() throws Exception {
		server.stopAndWait();
		Log.setLevel( null );
	}

	private static class TestServerAgent extends ServerAgent {

		private final StringBuilder builder = new StringBuilder();

		@Override
		public void handleSocket( Socket socket ) throws IOException {
			TestClientHandler handler = new TestClientHandler( this, socket );
			handler.start();
			super.handleSocket( socket );
		}

		public void append( int read ) {
			synchronized( builder ) {
				builder.append( (char)read );
				builder.notifyAll();
			}
		}

		@SuppressWarnings( "unused" )
		public String getMessage( int count ) {
			while( builder.length() < count ) {
				synchronized( builder ) {
					try {
						builder.wait();
					} catch( InterruptedException exception ) {
						//
					}
				}
			}

			String message = null;
			synchronized( builder ) {
				message = builder.toString();
				builder.delete( 0, builder.length() );
			}

			return message;
		}

	}

	private static class TestClientHandler implements Runnable {

		private Thread thread;

		private boolean execute;

		private TestServerAgent server;

		private Socket socket;

		public TestClientHandler( TestServerAgent server, Socket socket ) {
			this.server = server;
			this.socket = socket;
		}

		public void start() {
			execute = true;
			thread = new Thread( this, "TestClient" );
			thread.setPriority( Thread.NORM_PRIORITY );
			thread.setDaemon( true );
			thread.start();
		}

		@Override
		public void run() {
			while( execute ) {
				try {
					int read = socket.getInputStream().read();
					if( read < 0 ) return;
					server.append( read );
				} catch( IOException exception ) {
					// Intentinally ignore exception.
				}
			}
		}

	}

}
