package com.parallelsymmetry.utility.setting;

import com.parallelsymmetry.utility.IoUtil;
import com.parallelsymmetry.utility.log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapSettingsProvider implements WritableSettingsProvider {

	protected Map<String, String> store;

	public MapSettingsProvider() {
		this( new ConcurrentHashMap<>() );
	}

	public MapSettingsProvider( InputStream input ) {
		try {
			this.store = new ConcurrentHashMap<>( IoUtil.loadAsMap( input ) );
		} catch( IOException exception ) {
			Log.write( exception );
		} finally {
			try {
				if( input != null ) input.close();
			} catch( IOException exception ) {
				Log.write( exception );
			}
		}
	}

	public MapSettingsProvider( Map<String, String> store ) {
		this.store = new ConcurrentHashMap<>();
		this.store.putAll( store );
	}

	@Override
	public String get( String path ) {
		return getInternalStore().get( path );
	}

	@Override
	public Set<String> getKeys( String path ) {
		path = nodePath( path );

		Set<String> keys = new HashSet<>();
		if( !nodeExists( path ) ) return keys;

		for( String key : getInternalStore().keySet() ) {
			if( key.startsWith( path ) && key.indexOf( "/", path.length() ) < 0 ) {
				keys.add( key.substring( path.length() ) );
			}
		}

		return keys;
	}

	@Override
	public Set<String> getChildNames( String path ) {
		path = nodePath( path );

		Set<String> names = new HashSet<>();
		if( !nodeExists( path ) ) return names;

		for( String key : getInternalStore().keySet() ) {
			if( key.startsWith( path ) ) {
				int index = key.indexOf( "/", path.length() );
				if( index > 0 ) names.add( key.substring( path.length(), index ) );
			}
		}

		return names;
	}

	@Override
	public boolean nodeExists( String path ) {
		path = nodePath( path );
		Set<String> keys = getInternalStore().keySet();

		// If a key starts with the path return true.
		for( String key : keys ) {
			if( key.startsWith( path ) ) return true;
		}

		return false;
	}

	@Override
	public void put( String path, String value ) {
		if( value == null ) {
			getInternalStore().remove( path );
		} else {
			getInternalStore().put( path, value );
		}
	}

	@Override
	public void removeNode( String path ) {
		path = nodePath( path );

		for( String key : getInternalStore().keySet() ) {
			if( key.startsWith( path ) ) getInternalStore().remove( key );
		}
	}

	@Override
	public void flush( String path ) throws SettingsStoreException {}

	@Override
	public void sync( String path ) throws SettingsStoreException {}

	@Override
	public String toString() {
		return getInternalStore().toString();
	}

	public Map<String, String> getStore() {
		return new HashMap<String, String>( getInternalStore() );
	}

	public void show() {
		List<String> keys = new ArrayList<String>( getInternalStore().keySet() );
		Collections.sort( keys );
		for( String key : keys ) {
			System.out.println( key + "=" + getInternalStore().get( key ) );
		}
	}

	protected Map<String, String> getInternalStore() {
		return store;
	}

	protected String nodePath( String path ) {
		if( !path.endsWith( "/" ) ) path += "/";
		return path;
	}

}
