package com.parallelsymmetry.escape.utility.agent;

import java.io.InputStream;
import java.io.OutputStream;

public class PipeAdapter implements Pipe {

	private InputStream input;

	private OutputStream output;

	public PipeAdapter( InputStream input, OutputStream output ) {
		this.input = input;
		this.output = output;
	}

	public InputStream getInputStream() {
		return input;
	}

	public OutputStream getOutputStream() {
		return output;
	}

}
