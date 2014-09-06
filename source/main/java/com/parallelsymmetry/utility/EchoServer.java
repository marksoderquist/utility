package com.parallelsymmetry.utility;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
		while( server != null ) {
			Socket socket = null;
			try {
				socket = server.accept();
				String echo = IoUtil.load( socket.getInputStream() );
				IoUtil.save( echo, socket.getOutputStream() );
			} catch( IOException exception ) {
				Log.write( exception );
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
