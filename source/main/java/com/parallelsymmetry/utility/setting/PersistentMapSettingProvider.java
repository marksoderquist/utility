package com.parallelsymmetry.utility.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.parallelsymmetry.utility.IoUtil;
import com.parallelsymmetry.utility.TextUtil;
import com.parallelsymmetry.utility.log.Log;

public class PersistentMapSettingProvider extends MapSettingProvider {

	private static final Map<String, Map<String, String>> stores = new ConcurrentHashMap<String, Map<String, String>>();

	private static final Timer timer = new Timer( "PersistentMapSettingProvider", true );

	private File file;

	private String uri;

	private TimerTask task;

	private ReadWriteLock storeLock;

	public PersistentMapSettingProvider( File file ) {
		this( new ConcurrentHashMap<String, String>(), file );
	}

	public PersistentMapSettingProvider( Map<String, String> store, File file ) {
		this.file = file;
		this.uri = file.toURI().toString();
		this.storeLock = new ReentrantReadWriteLock();
		if( stores.get( uri ) == null ) stores.put( this.uri, new ConcurrentHashMap<String, String>() );
		getInternalStore().putAll( store );
		syncAll();
	}

	@Override
	public void put( String path, String value ) {
		super.put( path, value );
		triggerFlush();
	}

	@Override
	public void removeNode( String path ) {
		super.removeNode( path );
		triggerFlush();
	}

	/**
	 * Update the settings from the file.
	 */
	@Override
	public void sync( String path ) throws SettingsStoreException {
		storeLock.readLock().lock();
		try {
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
					getInternalStore().put( key, map.get( key ) );
				}
			}
		} finally {
			storeLock.readLock().unlock();
		}
	}

	/**
	 * Save the settings to the file.
	 */
	@Override
	public void flush( String path ) throws SettingsStoreException {
		if( !storeLock.writeLock().tryLock() ) return;

		try {
			path = nodePath( path );
			if( !nodeExists( path ) ) new SettingsStoreException( "Path does not exist: " + path );

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
			Map<String, String> internalStore = getInternalStore();
			for( String key : internalStore.keySet() ) {
				if( key.startsWith( path ) ) {
					map.put( key, internalStore.get( key ) );
				}
			}

			// Save the settings back to the file.
			try {
				save( map, file );
				Log.write( Log.DEBUG, "Settings flushed: ", path );
			} catch( IOException exception ) {
				throw new SettingsStoreException( exception );
			}
		} finally {
			storeLock.writeLock().unlock();
		}
	}

	@Override
	protected Map<String, String> getInternalStore() {
		return stores.get( uri );
	}

	private void syncAll() {
		try {
			sync( "/" );
		} catch( Exception exception ) {
			Log.write( exception );
		}
	}

	private void flushAll() {
		try {
			flush( "/" );
		} catch( Exception exception ) {
			Log.write( exception );
		}
	}

	private void triggerFlush() {
		if( task == null || task.cancel() ) timer.schedule( task = new FlushAll(), 100 );
	}

	private void save( Map<String, String> map, File file ) throws IOException {
		FileOutputStream output = new FileOutputStream( file );

		try {
			// Lock the file.
			FileLock lock = output.getChannel().tryLock();
			if( lock != null ) {
				try {
					// Save the file.
					saveData( map, output );
				} finally {
					// Unlock the file.
					lock.release();
				}
			}
		} catch( OverlappingFileLockException exception ) {
			// 
		} finally {
			if( output != null ) output.close();
		}
	}

	private void saveData( Map<String, String> map, FileOutputStream output ) throws IOException {
		PrintStream stream = new PrintStream( output, false, TextUtil.DEFAULT_ENCODING );

		List<String> keys = new ArrayList<String>( map.keySet() );
		Collections.sort( keys );

		for( String key : keys ) {
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
			if( elements.length < 2 ) continue;
			map.put( elements[0], elements[1] );
		}
	}

	private class FlushAll extends TimerTask {

		@Override
		public void run() {
			flushAll();
			task = null;
		}

	}

}
