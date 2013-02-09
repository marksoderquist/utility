package com.parallelsymmetry.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class FileUtil {

	public static final long KB = 1000;

	public static final long MB = KB * 1000;

	public static final long GB = MB * 1000;

	public static final long TB = GB * 1000;

	public static final long PB = TB * 1000;

	public static final long EB = PB * 1000;

	public static final long KiB = 1L << 10;

	public static final long MiB = 1L << 20;

	public static final long GiB = 1L << 30;

	public static final long TiB = 1L << 40;

	public static final long PiB = 1L << 50;

	public static final long EiB = 1L << 60;

	public static final File TEMP_FOLDER = new File( System.getProperty( "java.io.tmpdir" ) );

	public static final FileFilter FOLDER_FILTER = new FolderFilter();

	public static final FileFilter JAR_FILE_FILTER = new JarFileFilter();

	/**
	 * Converts a Unix-style glob to a regular expression. This does the following
	 * substitutions: ? becomes ., * becomes .*, {aa,bb} becomes (aa|bb).
	 * 
	 * @param glob The glob pattern.
	 */
	public static String globToRE( String glob ) {
		if( glob == null ) return null;

		boolean inside = false;
		boolean escape = false;
		StringBuffer buffer = new StringBuffer();

		for( int index = 0; index < glob.length(); index++ ) {
			char c = glob.charAt( index );

			if( escape ) {
				buffer.append( '\\' );
				buffer.append( c );
				escape = false;
				continue;
			}

			switch( c ) {
				case '\\': {
					escape = true;
					break;
				}
				case '?': {
					buffer.append( '.' );
					break;
				}
				case '.': {
					buffer.append( "\\." );
					break;
				}
				case '*': {
					buffer.append( ".*" );
					break;
				}
				case '{': {
					buffer.append( '(' );
					inside = true;
					break;
				}
				case ',': {
					if( inside ) {
						buffer.append( '|' );
					} else {
						buffer.append( ',' );
					}
					break;
				}
				case '}': {
					buffer.append( ')' );
					inside = false;
					break;
				}
				default: {
					buffer.append( c );
				}
			}
		}

		return buffer.toString();
	}

	public static String getExtension( File file ) {
		if( file == null ) return null;
		return getExtension( file.getName() );
	}

	public static String getExtension( String name ) {
		if( name == null ) return null;
		int index = name.lastIndexOf( '.' );
		if( index < 0 ) return "";
		return name.substring( index + 1 );
	}

	public static String getHumanSize( long size ) {
		int exponent = 0;
		long coefficient = size;
		while( coefficient >= KB ) {
			coefficient /= KB;
			exponent++;
		}

		String unit = "B";
		switch( (int)exponent ) {
			case 1: {
				unit = "KB";
				break;
			}
			case 2: {
				unit = "MB";
				break;
			}
			case 3: {
				unit = "GB";
				break;
			}
			case 4: {
				unit = "TB";
				break;
			}
			case 5: {
				unit = "PB";
				break;
			}
			case 6: {
				unit = "EB";
				break;
			}
		}

		// Should be, at most, five characters long; three numbers, two units.
		if( exponent > 0 && coefficient < 10 ) {
			long precise = size;
			while( precise >= MB ) {
				precise /= KB;
			}
			return String.format( "%3.1f", (float)precise / KB ) + unit;
		}

		return String.valueOf( coefficient ) + unit;
	}

	public static String getHumanBinSize( long size ) {
		int exponent = 0;
		long coefficient = size;
		while( coefficient >= KiB ) {
			coefficient /= KiB;
			exponent++;
		}

		String unit = "B";
		switch( (int)exponent ) {
			case 1: {
				unit = "KiB";
				break;
			}
			case 2: {
				unit = "MiB";
				break;
			}
			case 3: {
				unit = "GiB";
				break;
			}
			case 4: {
				unit = "TiB";
				break;
			}
			case 5: {
				unit = "PiB";
				break;
			}
			case 6: {
				unit = "EiB";
				break;
			}
		}

		// Should be, at most, seven characters long; four numbers, three units.
		if( exponent > 0 && coefficient < 10 ) {
			long precise = size;
			while( precise >= MiB ) {
				precise /= KiB;
			}
			return String.format( "%3.1f", (float)precise / KiB ) + unit;
		}

		return String.valueOf( coefficient ) + unit;
	}

	public static File removeExtension( File file ) {
		if( file == null ) return null;
		return new File( removeExtension( file.toString() ) );
	}

	public static String removeExtension( String name ) {
		if( name == null ) return null;
		int index = name.lastIndexOf( '.' );
		if( index < 0 ) return name;
		return name.substring( 0, index );
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
	public static File createTempFolder( String prefix, String suffix ) throws IOException {
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
	public static File createTempFolder( String prefix, String suffix, File parent ) throws IOException {
		File file = File.createTempFile( prefix, suffix, parent );
		if( !file.delete() ) return null;
		if( !file.mkdir() ) return null;
		return file;
	}

	public static void save( String data, File target ) throws IOException {
		save( data, target, "UTF-8" );
	}

	public static void save( String data, File target, String encoding ) throws IOException {
		IoUtil.save( data, new FileOutputStream( target ), encoding );
	}

	public static String load( File source ) throws IOException {
		return load( source, "UTF-8" );
	}

	public static String load( File source, String encoding ) throws IOException {
		return IoUtil.load( new FileInputStream( source ), encoding );
	}

	public static List<String> loadAsLines( File source ) throws IOException {
		return loadAsLines( source, "UTF-8" );
	}

	public static List<String> loadAsLines( File source, String encoding ) throws IOException {
		BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( source ), encoding ) );

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

	public static void zip( File source, File target ) throws IOException {
		URI base = source.toURI();
		List<File> files = listFiles( source );

		ZipOutputStream zip = null;
		try {
			zip = new ZipOutputStream( new FileOutputStream( target ) );

			for( File file : files ) {
				zip.putNextEntry( new ZipEntry( base.relativize( file.toURI() ).toString() ) );
				if( !file.isDirectory() ) {
					FileInputStream input = null;
					try {
						input = new FileInputStream( file );
						IoUtil.copy( new FileInputStream( file ), zip );
					} finally {
						if( input != null ) input.close();
					}
				}
				zip.closeEntry();
			}
		} finally {
			if( zip != null ) zip.close();
		}
	}

	public static void unzip( File source, File target ) throws IOException {
		target.mkdirs();

		ZipEntry entry = null;
		ZipInputStream zip = null;
		try {
			zip = new ZipInputStream( new FileInputStream( source ) );
			while( ( entry = zip.getNextEntry() ) != null ) {
				String path = entry.getName();
				File file = new File( target, path );
				if( path.endsWith( "/" ) ) {
					file.mkdirs();
				} else {
					FileOutputStream output = null;
					try {
						file.getParentFile().mkdirs();
						output = new FileOutputStream( file );
						IoUtil.copy( zip, output );
					} finally {
						if( output != null ) output.close();
					}
				}
			}
		} finally {
			if( zip != null ) zip.close();
		}
	}

	public static List<File> listFiles( File file ) {
		List<File> files = new ArrayList<File>();

		File[] fileArray = file.listFiles();
		for( File temp : fileArray ) {
			if( temp.isDirectory() ) {
				files.add( temp );
				files.addAll( listFiles( temp ) );
			} else {
				files.add( temp );
			}
		}

		return files;
	}

	public static boolean copy( File source, File target ) throws IOException {
		return copy( source, target, false );
	}

	public static boolean copy( File source, File target, boolean keepSourceFolder ) throws IOException {
		// Cannot copy a folder to a file.
		if( source.isDirectory() && target.isFile() ) return false;

		// Copy file sources to file targets.
		if( source.isFile() && target.isFile() ) {
			FileInputStream input = new FileInputStream( source );
			FileOutputStream output = new FileOutputStream( target );
			try {
				IoUtil.copy( input, output );
			} finally {
				if( output != null ) output.close();
				if( input != null ) input.close();
			}
			return true;
		}

		// Copy file sources to folder targets.
		if( source.isFile() && target.isDirectory() ) {
			File newTarget = new File( target, source.getName() );
			newTarget.createNewFile();
			return copy( source, newTarget, false );
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

		// Copy file source to new file target.
		if( source.isFile() ) {
			File parent = target.getParentFile();
			if( !parent.exists() ) target.getParentFile().mkdirs();
			target.createNewFile();
			return copy( source, target, false );
		}

		return false;
	}

	public static long copy( File file, OutputStream target ) throws IOException {
		final FileInputStream source = new FileInputStream( file );
		try {
			return IoUtil.copy( source, target );
		} finally {
			source.close();
		}
	}

	public static boolean move( File source, File target ) throws IOException {
		if( source.renameTo( target ) ) {
			return true;
		} else {
			if( copy( source, target ) && delete( source ) ) {
				return true;
			}
		}
		return false;
	}

	public static boolean delete( File file ) {
		if( !file.exists() ) return true;
		if( file.isDirectory() ) {
			for( File child : file.listFiles() ) {
				delete( child );
			}
		}
		return file.delete();
	}

	public static void deleteOnExit( File file ) {
		if( !file.exists() ) return;
		file.deleteOnExit();
		if( file.isDirectory() ) {
			for( File child : file.listFiles() ) {
				deleteOnExit( child );
			}
		}
	}

	public static boolean isWritable( File file ) {
		if( !file.isDirectory() ) return file.canWrite();

		try {
			File privilegeCheckFile = new File( file, "privilege.check.txt" );
			return privilegeCheckFile.createNewFile() && privilegeCheckFile.delete();
		} catch( IOException exception ) {
			return false;
		}
	}

}
