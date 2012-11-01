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

	@Override
	public InputStream getInputStream() {
		return input;
	}

	@Override
	public OutputStream getOutputStream() {
		return output;
	}

}
