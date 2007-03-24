package org.novaworx.util;

import java.io.File;

public class FileUtil {

	public static final String getExtension( File file ) {
		if( file == null ) return null;
		return getExtension( file.getName() );
	}

	public static final String getExtension( String name ) {
		if( name == null ) return null;
		int index = name.lastIndexOf( '.' );
		if( index < 0 ) return "";
		return name.substring( index + 1 );
	}

}
