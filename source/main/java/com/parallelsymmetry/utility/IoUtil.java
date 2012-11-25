package com.parallelsymmetry.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class IoUtil {

	private static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

	public static final long copy( InputStream source, OutputStream target ) throws IOException {
		int read = 0;
		long count = 0;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		while( ( read = source.read( buffer ) ) > -1 ) {
			target.write( buffer, 0, read );
			count += read;
		}
		return count;
	}

	public static final void copy( Reader source, Writer target ) throws IOException {
		int length;
		char[] buffer = new char[1024];
		while( ( length = source.read( buffer ) ) > 0 ) {
			target.write( buffer, 0, length );
		}
	}

	public static final void save( String data, OutputStream target ) throws IOException {
		save( data, target, "UTF-8" );
	}

	public static final void save( String data, OutputStream target, String encoding ) throws IOException {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter( target, encoding );
			writer.write( data );
		} finally {
			if( writer != null ) writer.close();
		}
	}

	public static final String load( InputStream input ) throws IOException {
		return load( input, "UTF-8" );
	}

	public static final String load( InputStream input, String encoding ) throws IOException {
		char[] buffer = new char[4096];
		InputStreamReader reader = null;
		StringWriter writer = new StringWriter();

		try {
			reader = new InputStreamReader( input, encoding );
			int read = reader.read( buffer );
			while( read > -1 ) {
				writer.write( buffer, 0, read );
				read = reader.read( buffer );
			}
			writer.close();
		} finally {
			if( reader != null ) reader.close();
		}

		return writer.toString();
	}

}
