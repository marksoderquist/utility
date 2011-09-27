package com.parallelsymmetry.escape.utility.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.parallelsymmetry.escape.utility.setting.SettingProvider;

public class MockSettingProvider implements SettingProvider {

	public final Map<String, String> values = new ConcurrentHashMap<String, String>();

	private String name;
	
	public MockSettingProvider() {
		this( null );
	}

	public MockSettingProvider( String name ) {
		this.name = name;
	}

	@Override
	public boolean nodeExists( String path ) {
		Set<String> keys = values.keySet();

		if( !path.endsWith( "/" ) ) path += "/";
		for( String key : keys ) {
			if( key.startsWith( path ) ) return true;
		}

		return false;
	}

	@Override
	public String get( String path ) {
		return values.get( path );
	}

	@Override
	public Set<String> getChildNames( String path ) {
		Set<String> names = new HashSet<String>();

		Set<String> keys = values.keySet();
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
		return values.size();
	}

	public void set( String key, String value ) {
		if( value == null ) {
			values.remove( key );
		} else {
			values.put( key, value );
		}
	}

	public void show() {
		System.out.println( (name == null ? "MockSettingProvider" : name ) + " data: " );
		List<String> keys = new ArrayList<String>( values.keySet() );
		Collections.sort( keys );
		for( String key : keys ) {
			System.out.println( "Key: " + key + "  Value: " + values.get( key ) );
		}
	}

}
