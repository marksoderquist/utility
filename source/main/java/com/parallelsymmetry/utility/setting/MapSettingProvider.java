package com.parallelsymmetry.utility.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MapSettingProvider implements WritableSettingProvider {

	protected Map<String, String> store;

	public MapSettingProvider() {
		this( new ConcurrentHashMap<String, String>() );
	}

	public MapSettingProvider( Map<String, String> store ) {
		this.store = store;
	}

	@Override
	public String get( String path ) {
		return store.get( path );
	}

	@Override
	public Set<String> getKeys( String path ) {
		path = nodePath( path );

		Set<String> keys = new HashSet<String>();
		if( !nodeExists( path ) ) return keys;

		for( String key : store.keySet() ) {
			if( key.startsWith( path ) && key.indexOf( "/", path.length() ) < 0 ) {
				keys.add( key.substring( path.length() ) );
			}
		}

		return keys;
	}

	@Override
	public Set<String> getChildNames( String path ) {
		path = nodePath( path );

		Set<String> names = new HashSet<String>();
		if( !nodeExists( path ) ) return names;

		for( String key : store.keySet() ) {
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
		Set<String> keys = store.keySet();

		// If a key starts with the path return true.
		for( String key : keys ) {
			if( key.startsWith( path ) ) return true;
		}

		return false;
	}

	@Override
	public void put( String path, String value ) {
		if( value == null ) {
			store.remove( path );
		} else {
			store.put( path, value );
		}
	}

	@Override
	public void removeNode( String path ) {
		path = nodePath( path );
		Iterator<String> iterator = store.keySet().iterator();

		while( iterator.hasNext() ) {
			String key = iterator.next();
			if( key.startsWith( path ) ) store.remove( key );
		}
	}

	@Override
	public void flush( String path ) throws SettingsStoreException {}

	@Override
	public void sync( String path ) throws SettingsStoreException {}

	@Override
	public String toString() {
		return store.toString();
	}

	public Map<String, String> getStore() {
		return new HashMap<String, String>( store );
	}

	public void show() {
		List<String> keys = new ArrayList<String>( store.keySet() );
		Collections.sort( keys );
		for( String key : keys ) {
			System.out.println( key + "=" + store.get( key ) );
		}
	}

	protected String nodePath( String path ) {
		if( !path.endsWith( "/" ) ) path += "/";
		return path;
	}

}
