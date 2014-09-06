package com.parallelsymmetry.utility;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class IoUtil {

	private static final int DEFAULT_BUFFER_SIZE = 4 * 1024;

	private IoUtil() {}

	public static final long copy( InputStream source, OutputStream target ) throws IOException {
		int read = 0;
		long count = 0;
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		while( ( read = source.read( buffer ) ) > -1 ) {
			target.write( buffer, 0, read );
			count += read;
		}
		target.flush();
		return count;
	}

	public static final long copy( Reader source, Writer target ) throws IOException {
		int length;
		long count = 0;
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		while( ( length = source.read( buffer ) ) > 0 ) {
			target.write( buffer, 0, length );
			count += length;
		}
		target.flush();
		return count;
	}

	public static final void save( String data, OutputStream target ) throws IOException {
		save( data, target, TextUtil.DEFAULT_ENCODING );
	}

	public static final void save( String data, OutputStream target, String encoding ) throws IOException {
		OutputStreamWriter writer = null;
		writer = new OutputStreamWriter( target, encoding == null ? TextUtil.DEFAULT_ENCODING : encoding );
		writer.write( data );
		writer.flush();
	}

	public static final String load( InputStream input ) throws IOException {
		return load( input, TextUtil.DEFAULT_ENCODING );
	}

	public static final String load( InputStream input, String encoding ) throws IOException {
		InputStreamReader reader = null;
		StringWriter writer = new StringWriter();

		try {
			reader = new InputStreamReader( input, encoding == null ? TextUtil.DEFAULT_ENCODING : encoding );
			copy( reader, writer );
			writer.close();
		} finally {
			if( reader != null ) reader.close();
		}

		return writer.toString();
	}

	public static final List<String> loadAsLines( InputStream input, String encoding ) throws IOException {
		BufferedReader reader = new BufferedReader( new InputStreamReader( input, encoding ) );

		String line = null;
		List<String> list = new ArrayList<String>();
		try {
			while( ( line = reader.readLine() ) != null ) {
				list.add( line );
			}
		} finally {
			if( reader != null ) reader.close();
		}

		return list;
	}

	public static final String[] loadAsLineArray( InputStream input, String encoding ) throws IOException {
		List<String> list = loadAsLines( input, encoding );
		return list.toArray( new String[list.size()] );
	}

	public static final void closeQuietly( Closeable closeable ) {
		if( closeable == null ) return;

		// Flush
		try {
			if( closeable instanceof Flushable ) ( (Flushable)closeable ).flush();
		} catch( IOException exception ) {
			// Intentionally ignore exception.
		}

		// Close
		try {
			closeable.close();
		} catch( IOException exception ) {
			// Intentionally ignore exception.
		}
	}

}
