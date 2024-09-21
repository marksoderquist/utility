package com.parallelsymmetry.utility.agent;

import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * When using the AnyConnect VPN Client IPv6 is disabled and causes problems
 * with these tests. A workaround it to set -Djava.net.preferIPv4Stack=true.
 * This can be done globally in Eclipse by adding it to the default VM arguments
 * under Preferences>Java/Installed JREs:Select the JRE, Click the Edit button,
 * add to Default VM Arguments.
 *
 * @author SoderquistMV
 */
public class SocketAgentTest extends BaseTestCase {

	private final TestServerAgent server = new TestServerAgent();

	@BeforeEach
	@Override
	public void setup() throws Exception {
		super.setup();
		server.startAndWait( 100, TimeUnit.MILLISECONDS );
		assertTrue( server.isRunning() );
		int localPort = server.getLocalPort();
		assertTrue( localPort > 0, "Server port should be greater than zero: " + localPort );
	}

	@Test
	public void testStartStop() throws Exception {
		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		agent.startAndWait();
		assertTrue( agent.isRunning(), "Agent is not running." );
		agent.stopAndWait();
		assertFalse( agent.isRunning(), "Agent is not stopped." );
		agent.startAndWait();
		assertTrue( agent.isRunning(), "Agent is not running." );
		agent.stopAndWait();
		assertFalse( agent.isRunning(), "Agent is not stopped." );
	}

	@Test
	public void testConnect() throws Exception {
		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( agent.isRunning(), "Agent should not be running." );

		agent.startAndWait();
		assertTrue( agent.isRunning(), "Agent is not running." );

		assertTrue( agent.isConnected(), "Agent is not connected." );

		agent.stopAndWait();
		assertFalse( agent.isRunning(), "Agent is not stopped." );
	}

	@Test
	public void testRestart() throws Exception {
		SocketAgent agent = new SocketAgent( server.getLocalPort() );
		assertFalse( agent.isRunning(), "Agent should not be running." );

		agent.startAndWait();
		assertTrue( agent.isRunning(), "Agent is not running." );

		agent.restart();
		assertTrue( agent.isRunning(), "Agent is not running." );

		agent.stopAndWait();
		assertFalse( agent.isRunning(), "Agent is not stopped." );
	}

	// Potential problem with test not completing.
	//	@Test
	//  public void testWrite() throws Exception {
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

	@AfterEach
	@Override
	public void teardown() throws Exception {
		server.stopAndWait();
		super.teardown();
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

			String message;
			synchronized( builder ) {
				message = builder.toString();
				builder.delete( 0, builder.length() );
			}

			return message;
		}

	}

	private static class TestClientHandler implements Runnable {

		private final TestServerAgent server;

		private final Socket socket;

		private boolean execute;

		public TestClientHandler( TestServerAgent server, Socket socket ) {
			this.server = server;
			this.socket = socket;
		}

		public void start() {
			execute = true;
			Thread thread = new Thread( this, "TestClient" );
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
					// Intentionally ignore exception.
				}
			}
		}

	}

}
