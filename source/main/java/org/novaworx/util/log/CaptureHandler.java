package org.novaworx.util.log;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class CaptureHandler extends StreamHandler {

	private OutputStream output;

	public CaptureHandler() {
		super();
		output = new ByteArrayOutputStream();
		setOutputStream( output );
		setFormatter( new CaptureFormatter() );
	}

	@Override
	public void publish( LogRecord record ) {
		super.publish( record );
		flush();
	}

	public String toString() {
		return output.toString();
	}

}
