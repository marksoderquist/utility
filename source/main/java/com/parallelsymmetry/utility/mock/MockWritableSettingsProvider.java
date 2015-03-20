package com.parallelsymmetry.utility.mock;

import java.util.Iterator;

import com.parallelsymmetry.utility.setting.SettingsStoreException;
import com.parallelsymmetry.utility.setting.WritableSettingsProvider;

public class MockWritableSettingsProvider extends MockSettingsProvider implements WritableSettingsProvider {

	public MockWritableSettingsProvider() {
		super();
	}

	public MockWritableSettingsProvider( String name ) {
		super( name );
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

}
