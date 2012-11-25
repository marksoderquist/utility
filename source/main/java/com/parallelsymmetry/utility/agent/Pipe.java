package com.parallelsymmetry.utility.agent;

import java.io.InputStream;
import java.io.OutputStream;

public interface Pipe {

	public InputStream getInputStream();

	public OutputStream getOutputStream();

}
