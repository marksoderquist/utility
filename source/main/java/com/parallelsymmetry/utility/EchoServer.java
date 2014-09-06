package com.parallelsymmetry.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.parallelsymmetry.utility.log.Log;

public class EchoServer extends Thread {

	private ServerSocket server;

	public EchoServer() {
		setDaemon( true );
		try {
			server = new ServerSocket( 0 );
		} catch( IOException exception ) {
			Log.write( exception );
		}
	}

	public int getLocalPort() {
		return server == null ? -1 : server.getLocalPort();
	}

	public void close() {
		if( server == null ) return;

		try {
			server.close();
		} catch( IOException exception ) {
			Log.write( exception );
		}
	}

	@Override
	public void run() {
		while( server != null && !server.isClosed() ) {
			Socket socket = null;
			try {
				try {
					socket = server.accept();
				} catch( SocketException exception ) {
					return;
				}
				BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream(), TextUtil.DEFAULT_CHARSET ) );
				String line = reader.readLine();
				socket.getOutputStream().write( line.getBytes( TextUtil.DEFAULT_CHARSET ) );
				socket.getOutputStream().write( '\n' );
				socket.getOutputStream().flush();
			} catch( IOException exception ) {
				Log.write( exception );
				return;
			} finally {
				try {
					if( socket != null ) socket.close();
				} catch( IOException exception ) {
					Log.write( exception );
				}
			}
		}
	}

}
