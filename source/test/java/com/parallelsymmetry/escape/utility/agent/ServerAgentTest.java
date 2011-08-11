package com.parallelsymmetry.escape.utility.agent;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.log.Log;

public class ServerAgentTest extends TestCase {

	private static final int PORT = 23423;

	private static final long DEFAULT_START_TIME = 100;

	private static final long DEFAULT_STOP_TIME = 100;

	private static final TimeUnit DEFAULT_UNIT = TimeUnit.MILLISECONDS;

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
		server.startAndWait( DEFAULT_START_TIME, DEFAULT_UNIT );
		assertTrue( "Server is not running.", server.isRunning() );
		int localPort = server.getLocalPort();
		assertTrue( "Server port should be greater than zero: " + localPort, localPort > 0 );
		server.stopAndWait( DEFAULT_STOP_TIME, DEFAULT_UNIT );
		assertFalse( "Server is not stopped.", server.isRunning() );
		server.startAndWait( DEFAULT_START_TIME, DEFAULT_UNIT );
		assertTrue( "Server is not running.", server.isRunning() );
		server.stopAndWait( DEFAULT_STOP_TIME, DEFAULT_UNIT );
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testStartStopWithPort() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		server.startAndWait( DEFAULT_START_TIME, DEFAULT_UNIT );
		assertTrue( "Server is not running.", server.isRunning() );
		server.stopAndWait( DEFAULT_STOP_TIME, DEFAULT_UNIT );
		assertFalse( "Server is not stopped.", server.isRunning() );
		server.startAndWait( DEFAULT_START_TIME, DEFAULT_UNIT );
		assertTrue( "Server is not running.", server.isRunning() );
		server.stopAndWait( DEFAULT_STOP_TIME, DEFAULT_UNIT );
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testConnect() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		server.startAndWait( DEFAULT_START_TIME, DEFAULT_UNIT );

		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( "Server should not be running.", agent.isRunning() );
		agent.startAndWait( DEFAULT_START_TIME, DEFAULT_UNIT );
		assertTrue( "Server is not running.", agent.isRunning() );

		agent.stopAndWait( DEFAULT_STOP_TIME, DEFAULT_UNIT );
		assertFalse( "Server is not stopped.", agent.isRunning() );

		server.stopAndWait( DEFAULT_STOP_TIME, DEFAULT_UNIT );
	}

	public void testRestart() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		assertFalse( "Server should not be running.", server.isRunning() );
		server.startAndWait( DEFAULT_START_TIME, DEFAULT_UNIT );
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.stopAndWait( DEFAULT_STOP_TIME, DEFAULT_UNIT );
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testWrite() throws Exception {
		MockServer server = new MockServer( PORT );
		server.startAndWait( DEFAULT_START_TIME, DEFAULT_UNIT );

		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( "Server should not be running.", agent.isRunning() );
		agent.startAndWait( DEFAULT_START_TIME, DEFAULT_UNIT );
		assertTrue( "Server is not running.", agent.isRunning() );

		String message = "Test message.";
		agent.getOutputStream().write( message.getBytes( Charset.forName( "US-ASCII" ) ) );
		assertEquals( "Incorrect message.", message, server.getMessage( message.length() ) );

		agent.stopAndWait( DEFAULT_STOP_TIME, DEFAULT_UNIT );
		assertFalse( "Server is not stopped.", agent.isRunning() );

		server.stopAndWait( DEFAULT_STOP_TIME, DEFAULT_UNIT );
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
