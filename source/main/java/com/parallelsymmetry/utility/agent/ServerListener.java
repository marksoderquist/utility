package com.parallelsymmetry.utility.agent;

import java.io.IOException;
import java.net.Socket;

public interface ServerListener {

	public void handleSocket( Socket socket ) throws IOException;

}
