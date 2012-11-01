package com.parallelsymmetry.escape.utility.mock;

import java.util.Iterator;

import com.parallelsymmetry.escape.utility.setting.SettingsStoreException;
import com.parallelsymmetry.escape.utility.setting.WritableSettingProvider;

public class MockWritableSettingProvider extends MockSettingProvider implements WritableSettingProvider {

	public MockWritableSettingProvider() {
		super();
	}

	public MockWritableSettingProvider( String name ) {
		super( name );
	}

	@Override
	public void put( String path, String value ) {
		set( path, value );
	}

	@Override
	public void set( String key, String value ) {
		if( value == null ) {
			values.remove( key );
		} else {
			values.put( key, value );
		}
	}

	@Override
	public void removeNode( String path ) {
		if( !path.endsWith( "/" ) ) path += "/";
		Iterator<String> iterator = values.keySet().iterator();

		while( iterator.hasNext() ) {
			String key = iterator.next();
			if( key.startsWith( path ) ) values.remove( key );
		}
	}

	@Override
	public void flush( String path ) throws SettingsStoreException {}

	@Override
	public void sync( String path ) throws SettingsStoreException {}

}
