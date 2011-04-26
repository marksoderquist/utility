package com.parallelsymmetry.escape.utility.setting;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MockSettingProvider implements SettingProvider {

	protected Map<String, String> values = new ConcurrentHashMap<String, String>();

	private String name;

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
	public Set<String> getNames( String path ) {
		Set<String> names = new HashSet<String>();

		Set<String> keys = values.keySet();
		if( !path.endsWith( "/" ) ) path += "/";
		for( String key : keys ) {
			if( key.startsWith( path ) ) names.add( key.substring( path.length(), key.indexOf( "/", path.length() ) ) );
		}

		return names;
	}

	public void set( String key, String value ) {
		if( value == null ) {
			values.remove( key );
		} else {
			values.put( key, value );
		}
	}

	public void show() {
		System.out.println( name + " data: " );
		for( String key : values.keySet() ) {
			System.out.println( "Key: " + key + "  Value: " + values.get( key ) );
		}
	}

}
