package com.parallelsymmetry.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class FileUtil {

	public static final FileFilter FOLDER_FILTER = new FolderFilter();

	public static final FileFilter JAR_FILE_FILTER = new JarFileFilter();

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

	/**
	 * Create a temporary folder. If there is a problem creating the folder this
	 * method will return null.
	 * 
	 * @param prefix
	 * @param suffix
	 * @return
	 * @throws IOException
	 */
	public static final File createTempFolder( String prefix, String suffix ) throws IOException {
		File file = File.createTempFile( prefix, suffix );
		if( !file.delete() ) return null;
		if( !file.mkdir() ) return null;
		return file;
	}

	/**
	 * Create a temporary folder. If there is a problem creating the folder this
	 * method will return null.
	 * 
	 * @param prefix
	 * @param suffix
	 * @param parent
	 * @return
	 * @throws IOException
	 */
	public static final File createTempFolder( String prefix, String suffix, File parent ) throws IOException {
		File file = File.createTempFile( prefix, suffix, parent );
		if( !file.delete() ) return null;
		if( !file.mkdir() ) return null;
		return file;
	}

	public static final void save( String data, File target ) throws IOException {
		FileWriter writer = new FileWriter( target );
		writer.write( data );
		writer.close();
	}

	public static final String load( File source ) throws IOException {
		char[] buffer = new char[4096];
		FileReader reader = new FileReader( source );
		StringWriter writer = new StringWriter();

		int read = reader.read( buffer );
		while( read > -1 ) {
			writer.write( buffer, 0, read );
			read = reader.read( buffer );
		}
		writer.close();

		return writer.toString();
	}

	public static final boolean copy( File source, File target ) throws IOException {
		return copy( source, target, false );
	}

	public static final boolean copy( File source, File target, boolean keepSourceFolder ) throws IOException {
		// Cannot copy a folder to a file.
		if( source.isDirectory() && target.isFile() ) return false;

		// Copy file sources to file targets.
		if( source.isFile() && target.isFile() ) {
			FileInputStream input = new FileInputStream( source );
			FileOutputStream output = new FileOutputStream( target );
			IOPump pump = new IOPump( input, output );
			pump.startAndWait( 0 );
			return true;
		}

		// Copy file sources to folder targets.
		if( source.isFile() && target.isDirectory() ) {
			File newTarget = new File( target, source.getName() );
			newTarget.createNewFile();
			return copy( source, newTarget );
		}

		// Copy folder sources to folder targets.
		if( source.isDirectory() && target.isDirectory() ) {
			File newTarget = target;
			if( keepSourceFolder ) {
				newTarget = new File( target, source.getName() );
				newTarget.mkdir();
			}
			boolean result = true;
			for( File file : source.listFiles() ) {
				result = result & copy( file, newTarget, true );
			}
			return result;
		}

		return false;
	}

	public static final boolean deleteTree( File file ) {
		if( !file.exists() ) return true;
		if( file.isDirectory() ) {
			for( File child : file.listFiles() ) {
				deleteTree( child );
			}
		}
		return file.delete();
	}

	public static final void deleteTreeOnExit( File file ) {
		if( !file.exists() ) return;
		file.deleteOnExit();
		if( file.isDirectory() ) {
			for( File child : file.listFiles() ) {
				deleteTreeOnExit( child );
			}
		}
	}

}
