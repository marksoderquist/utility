package org.novaworx.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class LineParser {

	private static final int INIT = -3;

	private static final int NULL = -2;

	private static final int DONE = -1;

	private int data = INIT;

	private int next = INIT;

	private Reader reader;

	private StringBuilder builder;

	public LineParser( String string ) {
		if( string == null ) return;
		this.reader = new StringReader( string );
	}

	public LineParser( Reader reader ) {
		if( reader == null ) return;
		this.reader = reader;
	}

	public String next() {
		if( reader == null ) return null;
		if( builder == null ) builder = new StringBuilder();
		builder.delete( 0, builder.length() );

		try {
			init();

			if( data == NULL ) {
				return null;
			} else if( data == DONE ) {
				data = NULL;
				return "";
			}

			while( data > DONE ) {
				if( data == 10 || data == 13 ) {
					if( data == next ) {
						read();
						break;
					} else if( next == 10 || next == 13 ) {
						read();
						read();
						break;
					} else if( next < 0 ) {
						read();
						break;
					}
					read();
					break;
				}
				read();
				if( data == DONE ) data = NULL;
			}
		} catch( IOException exception ) {
			exception.printStackTrace();
			return null;
		}

		return builder.toString();
	}

	private void init() throws IOException {
		if( data == INIT ) data = reader.read();
		if( next == INIT ) next = reader.read();
	}

	private void read() throws IOException {
		builder.append( (char)data );
		data = next;
		next = reader.read();
	}

}
