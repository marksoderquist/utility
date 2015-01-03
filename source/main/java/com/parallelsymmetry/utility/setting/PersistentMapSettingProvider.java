package com.parallelsymmetry.utility.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.parallelsymmetry.utility.IoUtil;
import com.parallelsymmetry.utility.TextUtil;
import com.parallelsymmetry.utility.log.Log;

public class PersistentMapSettingProvider extends MapSettingProvider {

	private File file;

	public PersistentMapSettingProvider( File file ) {
		this( new ConcurrentHashMap<String, String>(), file );
	}

	public PersistentMapSettingProvider( Map<String, String> store, File file ) {
		super( store );
		this.file = file;
		try {
			sync( "/" );
		} catch( SettingsStoreException exception ) {
			Log.write( exception );
		}
	}

	/**
	 * Save the settings to the file.
	 */
	@Override
	public synchronized void flush( String path ) throws SettingsStoreException {
		path = nodePath( path );
		if( !nodeExists( path ) ) return;

		// Temporary buffer map.
		Map<String, String> map = new HashMap<String, String>();

		// Load the existing settings.
		if( file.exists() ) {
			try {
				load( file, map );
			} catch( IOException exception ) {
				throw new SettingsStoreException( exception );
			}
		}

		// Remove the values for the specified path from the buffer map.
		Set<String> keys = new HashSet<String>( map.keySet() );
		for( String key : keys ) {
			if( key.startsWith( path ) ) {
				map.remove( key );
			}
		}

		// Copy the values for the path from the memory store to the buffer map.
		for( String key : store.keySet() ) {
			if( key.startsWith( path ) ) {
				map.put( key, store.get( key ) );
			}
		}

		// Save the settings back to the file.
		try {
			save( map, file );
		} catch( IOException exception ) {
			throw new SettingsStoreException( exception );
		}
	}

	/**
	 * Update the settings from the file.
	 */
	@Override
	public synchronized void sync( String path ) throws SettingsStoreException {
		path = nodePath( path );

		Map<String, String> map = new HashMap<String, String>();
		try {
			load( file, map );
		} catch( FileNotFoundException exception ) {
			// Intentionally ignore exception.
		} catch( IOException exception ) {
			throw new SettingsStoreException( exception );
		}

		for( String key : map.keySet() ) {
			if( key.startsWith( path ) ) {
				store.put( key, map.get( key ) );
			}
		}
	}

	private void save( Map<String, String> map, File file ) throws IOException {
		FileOutputStream output = new FileOutputStream( file );

		try {
			// Lock the file.
			FileLock lock = output.getChannel().lock();
			try {
				// Save the file.
				saveData( map, output );
			} finally {
				// Unlock the file.
				if( lock != null ) lock.release();
			}
		} finally {
			if( output != null ) output.close();
		}
	}

	private void saveData( Map<String, String> map, FileOutputStream output ) throws IOException {
		PrintStream stream = new PrintStream( output, false, TextUtil.DEFAULT_ENCODING );
		for( String key : map.keySet() ) {
			stream.print( key );
			stream.print( "=" );
			stream.println( map.get( key ) );
		}
		stream.flush();
	}

	private void load( File file, Map<String, String> map ) throws IOException {
		FileInputStream input = new FileInputStream( file );

		try {
			// Load the file.
			loadData( input, map );
		} finally {
			if( input != null ) input.close();
		}
	}

	private void loadData( FileInputStream input, Map<String, String> map ) throws IOException {
		List<String> lines = IoUtil.loadAsLines( input, TextUtil.DEFAULT_ENCODING );
		for( String line : lines ) {
			String[] elements = line.split( "=" );
			map.put( elements[0], elements[1] );
		}
	}

}
