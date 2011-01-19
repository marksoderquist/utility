package com.parallelsymmetry.escape.utility.setting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockSettingProvider implements SettingProvider {

	private Map<String, String> values = new ConcurrentHashMap<String, String>();

	private boolean writable;
	
	public MockSettingProvider( boolean writable ) {
		this.writable = writable;
	}

	@Override
	public String get( String path ) {
		return values.get( path );
	}

	@Override
	public void put( String path, String value ) {
		if( writable ) values.put( path, value );
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	public void set( String key, String value ) {
		if( value == null ) {
			values.remove( key );
		} else {
			values.put( key, value );
		}
	}

}
