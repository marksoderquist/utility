package com.parallelsymmetry.escape.utility.setting;

import java.util.Iterator;

public class MockWritableSettingProvider extends MockSettingProvider implements WritableSettingProvider {

	public MockWritableSettingProvider( String name ) {
		super( name );
	}

	@Override
	public void put( String path, String value ) {
		values.put( path, value );
	}

	public void set( String key, String value ) {
		if( value == null ) {
			values.remove( key );
		} else {
			values.put( key, value );
		}
	}

	@Override
	public void removeNode( String path ) {
		String node = path + "/";
		Iterator<String> iterator = values.keySet().iterator();

		while( iterator.hasNext() ) {
			String key = iterator.next();
			if( key.startsWith( node ) ) values.remove( key );
		}
	}

}
