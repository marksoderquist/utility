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
		path = path.substring( 1 );
		int index = path.lastIndexOf( "/" );
		String prefKey = path.substring( index + 1 );

		try {
			preferences.sync();
			preferences.sync();
		} catch( BackingStoreException exception ) {
			Log.write( exception );
		}

		Preferences node = index < 0 ? preferences : preferences.node( path.substring( 0, index ) );
		return node.get( prefKey, null );
	}

	@Override
	public void put( String path, String value ) {
		path = path.substring( 1 );
		int index = path.lastIndexOf( "/" );
		String prefKey = path.substring( index + 1 );

		Preferences node = index < 0 ? preferences : preferences.node( path.substring( 0, index ) );
		if( value == null ) {
			node.remove( prefKey );
		} else {
			node.put( prefKey, value );
		}
		try {
			node.flush();
		} catch( BackingStoreException exception ) {
			Log.write( exception );
		}
	}

	@Override
	public boolean nodeExists( String path ) {
		try {
			return preferences.nodeExists( path.substring( 1 ) );
		} catch( BackingStoreException exception ) {
			Log.write( exception );
			return false;
		}
	}

	@Override
	public void removeNode( String path ) throws SettingsStoreException {
		try {
			preferences.node( path.substring( 1 ) ).removeNode();
		} catch( BackingStoreException exception ) {
			throw new SettingsStoreException( exception );
		}
	}

	@Override
	public void flush( String path ) throws SettingsStoreException {
		try {
			preferences.node( path.substring( 1 ) ).flush();
		} catch( BackingStoreException exception ) {
			throw new SettingsStoreException( exception );
		}
	}

	@Override
	public void sync( String path ) throws SettingsStoreException {
		try {
			preferences.node( path.substring( 1 ) ).sync();
		} catch( BackingStoreException exception ) {
			throw new SettingsStoreException( exception );
		}
	}

}
