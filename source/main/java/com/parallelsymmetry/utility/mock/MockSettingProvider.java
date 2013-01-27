package com.parallelsymmetry.utility.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.parallelsymmetry.utility.setting.SettingProvider;

public class MockSettingProvider implements SettingProvider {

	protected final Map<String, String> store = new ConcurrentHashMap<String, String>();

	private String name;

	public MockSettingProvider() {
		this( null );
	}

	public MockSettingProvider( String name ) {
		this.name = name;
	}

	public MockSettingProvider( String name, Map<String, String> values ) {
		this.name = name;
		store.putAll( values );
	}

	@Override
	public String get( String path ) {
		return store.get( path );
	}

	@Override
	public Set<String> getKeys( String path ) {
		Set<String> keys = new HashSet<String>();
		if( !nodeExists( path ) ) return keys;

		if( !path.endsWith( "/" ) ) path += "/";

		for( String key : store.keySet() ) {
			if( key.startsWith( path ) && key.indexOf( "/", path.length() ) < 0 ) {
				keys.add( key.substring( path.length() ) );
			}
		}

		return keys;
	}

	@Override
	public Set<String> getChildNames( String path ) {
		Set<String> names = new HashSet<String>();
		if( !nodeExists( path ) ) return names;
		
		if( !path.endsWith( "/" ) ) path += "/";

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
		Set<String> keys = store.keySet();
		if( !path.endsWith( "/" ) ) path += "/";

		// If a key starts with the path return true.
		for( String key : keys ) {
			if( key.startsWith( path ) ) return true;
		}

		return false;
	}

	public int getValueCount() {
		return store.size();
	}

	public void set( String key, String value ) {
		if( value == null ) {
			store.remove( key );
		} else {
			store.put( key, value );
		}
	}

	public Set<String> keySet() {
		return store.keySet();
	}

	public void show() {
		System.out.println( ( name == null ? "MockSettingProvider" : name ) + " data: " );
		List<String> keys = new ArrayList<String>( store.keySet() );
		Collections.sort( keys );
		for( String key : keys ) {
			System.out.println( "Key: " + key + "  Value: " + store.get( key ) );
		}
	}

}
