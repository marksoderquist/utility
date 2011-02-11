package com.parallelsymmetry.escape.utility.setting;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.parallelsymmetry.escape.utility.log.Log;

public class PreferencesSettingProvider implements WritableSettingProvider {

	private Preferences preferences;

	public PreferencesSettingProvider( Preferences preferences ) {
		this.preferences = preferences;
	}

	@Override
	public String get( String path ) {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );
		
		int index = path.lastIndexOf( "/" );
		String prefKey = path.substring( index + 1 );

		Preferences node = index < 0 ? preferences : preferences.node( path.substring( 0, index ) );
		return node.get( prefKey, null );
	}

	@Override
	public void put( String path, String value ) {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		int index = path.lastIndexOf( "/" );
		String prefKey = path.substring( index + 1 );

		Preferences node = index < 0 ? preferences : preferences.node( path.substring( 0, index ) );
		if( value == null ) {
			node.remove( prefKey );
		} else {
			node.put( prefKey, value );
		}
	}

	@Override
	public boolean nodeExists( String path ) {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		try {
			return preferences.nodeExists( path );
		} catch( BackingStoreException exception ) {
			Log.write( exception );
			return false;
		}
	}

	@Override
	public void removeNode( String path ) throws SettingsStoreException {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		try {
			preferences.node( path ).removeNode();
		} catch( BackingStoreException exception ) {
			throw new SettingsStoreException( exception );
		}
	}

	@Override
	public void flush( String path ) throws SettingsStoreException {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		try {
			preferences.node( path ).flush();
		} catch( BackingStoreException exception ) {
			throw new SettingsStoreException( exception );
		} catch( IllegalArgumentException exception ) {
			Log.write( Log.ERROR, "Path: " + path );
			Log.write( exception );
		}
	}

	@Override
	public void sync( String path ) throws SettingsStoreException {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		try {
			preferences.node( path ).sync();
		} catch( BackingStoreException exception ) {
			throw new SettingsStoreException( exception );
		}
	}

}
