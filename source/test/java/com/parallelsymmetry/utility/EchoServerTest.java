package com.parallelsymmetry.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import junit.framework.TestCase;

public class EchoServerTest extends TestCase {

	public void testEcho() throws Exception {
		EchoServer server = new EchoServer();

		int port = server.getLocalPort();
		assertTrue( port > -1 && port < 65536 );

		server.start();

		int timeout = 100;
		Socket socket = null;
		String data = String.valueOf( System.currentTimeMillis() );
		try {
			// Open the socket.
			socket = new Socket();
			socket.setSoTimeout( timeout );
			socket.connect( new InetSocketAddress( InetAddress.getByName( "127.0.0.1" ), port ), timeout );
			//socket.connect( new InetSocketAddress( InetAddress.getLoopbackAddress(), port ), timeout );

			// Write the current time.
			socket.getOutputStream().write( data.getBytes( TextUtil.DEFAULT_CHARSET ) );
			socket.getOutputStream().write( '\n' );
			socket.getOutputStream().flush();

			// Read the response.
			BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream(), TextUtil.DEFAULT_CHARSET ) );
			String echo = reader.readLine();

			assertEquals( data, echo );
		} finally {
			if( socket != null ) socket.close();
		}

		server.close();
	}

}
