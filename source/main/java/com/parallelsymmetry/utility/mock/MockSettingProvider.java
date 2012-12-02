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
	public Set<String> getKeys( String path ) {
		Set<String> keySet = new HashSet<String>();
		if( !path.endsWith( "/" ) ) path += "/";

		Set<String> keys = store.keySet();
		for( String key : keys ) {
			if( key.startsWith( path ) && key.indexOf( "/", path.length() ) < 0 ) {
				keySet.add( key.substring( path.length() ) );
			}
		}

		return keySet;
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

	public void print() {
		System.out.println( ( name == null ? "MockSettingProvider" : name ) + " data: " );
		List<String> keys = new ArrayList<String>( store.keySet() );
		Collections.sort( keys );
		for( String key : keys ) {
			System.out.println( "Key: " + key + "  Value: " + store.get( key ) );
		}
	}

}
