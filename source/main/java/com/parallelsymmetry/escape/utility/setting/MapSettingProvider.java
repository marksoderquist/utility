package com.parallelsymmetry.escape.utility.setting;

import java.util.ArrayList;
import java.util.Collections;
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
	public boolean nodeExists( String path ) {
		Set<String> keys = store.keySet();

		if( !path.endsWith( "/" ) ) path += "/";
		for( String key : keys ) {
			if( key.startsWith( path ) ) return true;
		}

		return false;
	}

	@Override
	public String get( String path ) {
		return store.get( path );
	}

	@Override
	public Set<String> getChildNames( String path ) {
		Set<String> names = new HashSet<String>();

		Set<String> keys = store.keySet();
		if( !path.endsWith( "/" ) ) path += "/";
		for( String key : keys ) {
			if( key.startsWith( path ) ) {
				try {
					names.add( key.substring( path.length(), key.indexOf( "/", path.length() ) ) );
				} catch( StringIndexOutOfBoundsException exception ) {
					// Intentionally ignore exception.
				}
			}
		}

		return names;
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
		if( !path.endsWith( "/" ) ) path += "/";
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

	public void show() {
		List<String> keys = new ArrayList<String>( store.keySet() );
		Collections.sort( keys );
		for( String key : keys ) {
			System.out.println( "Key: " + key + "  Value: " + store.get( key ) );
		}
	}

}
