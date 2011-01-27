package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtil {

	public static final long KILOBYTE = 1L << 10;

	public static final long MEGABYTE = 1L << 20;

	public static final long GIGABYTE = 1L << 30;

	public static final long TERABYTE = 1L << 40;

	public static final long PETABYTE = 1L << 50;

	public static final File TEMP_FOLDER = new File( System.getProperty( "java.io.tmpdir" ) );

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

	public static final File removeExtension( File file ) {
		if( file == null ) return null;
		return new File( removeExtension( file.toString() ) );
	}

	public static final String removeExtension( String name ) {
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
		save( data, target, "UTF-8" );
	}

	public static final void save( String data, File target, String encoding ) throws IOException {
		IoUtil.save( data, new FileOutputStream( target ), encoding );
	}

	public static final String load( File source ) throws IOException {
		return load( source, "UTF-8" );
	}

	public static final String load( File source, String encoding ) throws IOException {
		return IoUtil.load( new FileInputStream( source ), encoding );
	}

	public static final void zip( File source, File target ) throws IOException {
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

	public static final void unzip( File source, File target ) throws IOException {
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

	public static final List<File> listFiles( File file ) {
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
			IoUtil.copy( input, output );
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

	public static final boolean move( File source, File target ) throws IOException {
		if( source.renameTo( target ) ) {
			return true;
		} else {
			if( copy( source, target ) && delete( source ) ) {
				return true;
			}
		}
		return false;
	}

	public static final boolean delete( File file ) {
		if( !file.exists() ) return true;
		if( file.isDirectory() ) {
			for( File child : file.listFiles() ) {
				delete( child );
			}
		}
		return file.delete();
	}

	public static final void deleteOnExit( File file ) {
		if( !file.exists() ) return;
		file.deleteOnExit();
		if( file.isDirectory() ) {
			for( File child : file.listFiles() ) {
				deleteOnExit( child );
			}
		}
	}

}
