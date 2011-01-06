package com.parallelsymmetry.escape.utility;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class IoUtil {

	public static final void copy( Reader source, Writer target ) throws IOException {
		int length;
		char[] buffer = new char[1024];
		while( ( length = source.read( buffer ) ) > 0 ) {
			target.write( buffer, 0, length );
		}
	}

}
