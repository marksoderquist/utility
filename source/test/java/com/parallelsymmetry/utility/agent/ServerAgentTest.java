package com.parallelsymmetry.utility.agent;

import java.io.IOException;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import com.parallelsymmetry.utility.agent.ServerAgent;
import com.parallelsymmetry.utility.agent.SocketAgent;
import com.parallelsymmetry.utility.log.Log;

public class ServerAgentTest extends TestCase {

	private static final int PORT = 23423;

	@Override
	public void setUp() {
		Log.setLevel( Log.NONE );
	}

	@Override
	public void tearDown() {
		Log.setLevel( null );
	}

	public void testStartStop() throws Exception {
		ServerAgent server = new ServerAgent();
		server.startAndWait();
		assertTrue( "Server is not running.", server.isRunning() );
		int localPort = server.getLocalPort();
		assertTrue( "Server port should be greater than zero: " + localPort, localPort > 0 );
		server.stopAndWait();
		assertFalse( "Server is not stopped.", server.isRunning() );
		server.startAndWait();
		assertTrue( "Server is not running.", server.isRunning() );
		server.stopAndWait();
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testStartStopWithPort() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		server.startAndWait();
		assertTrue( "Server is not running.", server.isRunning() );
		server.stopAndWait();
		assertFalse( "Server is not stopped.", server.isRunning() );
		server.startAndWait();
		assertTrue( "Server is not running.", server.isRunning() );
		server.stopAndWait();
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testConnect() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		server.startAndWait();

		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( "Server should not be running.", agent.isRunning() );
		agent.startAndWait();
		assertTrue( "Server is not running.", agent.isRunning() );

		agent.stopAndWait();
		assertFalse( "Server is not stopped.", agent.isRunning() );

		server.stopAndWait();
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testRestart() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		assertFalse( "Server should not be running.", server.isRunning() );
		server.startAndWait();
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.stopAndWait();
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testWrite() throws Exception {
		MockServer server = new MockServer( PORT );
		server.startAndWait();

		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( "Server should not be running.", agent.isRunning() );
		agent.startAndWait();
		assertTrue( "Server is not running.", agent.isRunning() );

		String message = "Test message.";
		agent.getOutputStream().write( message.getBytes( Charset.forName( "US-ASCII" ) ) );
		assertEquals( "Incorrect message.", message, server.getMessage( message.length() ) );

		agent.stopAndWait();
		assertFalse( "Server is not stopped.", agent.isRunning() );

		server.stopAndWait();
	}

	private static final class MockServer extends ServerAgent {

		public MockServer( int port ) {
			super( port );
		}

		public String getMessage( int length ) {
			StringBuilder builder = new StringBuilder();

			for( int index = 0; index < length; index++ ) {
				try {
					int data = getInputStream().read();
					if( data < 0 ) return builder.toString();
					builder.append( (char)data );
				} catch( IOException exception ) {
					Log.write( exception );
				}
			}

			return builder.toString();
		}

	}

}
