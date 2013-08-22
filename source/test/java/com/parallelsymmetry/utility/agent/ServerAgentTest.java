package com.parallelsymmetry.utility.agent;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import com.parallelsymmetry.utility.BaseTestCase;
import com.parallelsymmetry.utility.log.Log;

public class ServerAgentTest extends BaseTestCase {

	private static final int PORT = 51427;
	
	private static final int TIMEOUT = 1000;

	public void testStartStop() throws Exception {
		ServerAgent server = new ServerAgent();
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( "Server is not running.", server.isRunning() );
		int localPort = server.getLocalPort();
		assertTrue( "Server port should be greater than zero: " + localPort, localPort > 0 );
		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( "Server is not stopped.", server.isRunning() );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( "Server is not running.", server.isRunning() );
		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testStartStopWithPort() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( "Server is not running.", server.isRunning() );
		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( "Server is not stopped.", server.isRunning() );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( "Server is not running.", server.isRunning() );
		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testConnect() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );

		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( "Server should not be running.", agent.isRunning() );
		agent.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( "Server is not running.", agent.isRunning() );

		agent.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( "Server is not stopped.", agent.isRunning() );

		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testRestart() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		assertFalse( "Server should not be running.", server.isRunning() );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.restart();
		assertTrue( "Server is not running.", server.isRunning() );

		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( "Server is not stopped.", server.isRunning() );
	}

	public void testWrite() throws Exception {
		MockServer server = new MockServer( PORT );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );

		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( "Server should not be running.", agent.isRunning() );
		agent.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( "Server is not running.", agent.isRunning() );

		String message = "Test message.";
		agent.getOutputStream().write( message.getBytes( Charset.forName( "US-ASCII" ) ) );
		assertEquals( "Incorrect message.", message, server.getMessage( message.length() ) );

		agent.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( "Server is not stopped.", agent.isRunning() );

		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
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
