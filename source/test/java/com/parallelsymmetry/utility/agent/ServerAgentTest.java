package com.parallelsymmetry.utility.agent;

import com.parallelsymmetry.utility.BaseTestCase;
import com.parallelsymmetry.utility.log.Log;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ServerAgentTest extends BaseTestCase {

	private static final int PORT = 51427;

	private static final int TIMEOUT = 1000;

	@Test
	public void testStartStop() throws Exception {
		ServerAgent server = new ServerAgent();
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( server.isRunning(), "Server is not running." );
		int localPort = server.getLocalPort();
		assertTrue( localPort > 0, "Server port should be greater than zero: " + localPort );
		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( server.isRunning(), "Server is not stopped." );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( server.isRunning(), "Server is not running." );
		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( server.isRunning(), "Server is not stopped." );
	}

	@Test
	public void testStartStopWithPort() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( server.isRunning(), "Server is not running." );
		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( server.isRunning(), "Server is not stopped." );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( server.isRunning(), "Server is not running." );
		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( server.isRunning(), "Server is not stopped." );
	}

	@Disabled
	@Test
	public void testConnect() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );

		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( agent.isRunning(), "Server should not be running." );

		agent.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( agent.isRunning(), "Server is not running." );

		agent.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( agent.isRunning(), "Server is not stopped." );

		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( server.isRunning(), "Server is not stopped." );
	}

	@Test
	public void testRestart() throws Exception {
		ServerAgent server = new ServerAgent( PORT );
		assertFalse( server.isRunning(), "Server should not be running." );

		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( server.isRunning(), "Server is not running." );

		server.restart();
		assertTrue( server.isRunning(), "Server is not running." );

		server.restart();
		assertTrue( server.isRunning(), "Server is not running." );

		server.restart();
		assertTrue( server.isRunning(), "Server is not running." );

		server.restart();
		assertTrue( server.isRunning(), "Server is not running." );

		server.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( server.isRunning(), "Server is not stopped." );
	}

	@Test
	public void testWrite() throws Exception {
		MockServer server = new MockServer( PORT );
		server.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );

		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( agent.isRunning(), "Server should not be running." );
		agent.startAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertTrue( agent.isRunning(), "Server is not running." );

		String message = "Test message.";
		agent.getOutputStream().write( message.getBytes( StandardCharsets.US_ASCII ) );
		assertEquals( message, server.getMessage( message.length() ) );

		agent.stopAndWait( TIMEOUT, TimeUnit.MILLISECONDS );
		assertFalse( agent.isRunning(), "Server is not stopped." );

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
