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
	public boolean nodeExists( String path ) {
		try {
			return preferences.nodeExists( path );
		} catch( BackingStoreException exception ) {
			Log.write( exception );
		}
		return false;
	}

	@Override
	public String get( String path ) {
		int index = path.lastIndexOf( "/" );
		String prefPath = path.substring( 0, index );
		String prefKey = path.substring( index + 1 );
		return preferences.node( prefPath ).get( prefKey, null );
	}

	@Override
	public void put( String path, String value ) {
		int index = path.lastIndexOf( "/" );
		String prefPath = path.substring( 0, index );
		String prefKey = path.substring( index + 1 );
		if( value == null ) {
			preferences.node( prefPath ).remove( prefKey );
		} else {
			preferences.node( prefPath ).put( prefKey, value );
		}
		try {
			preferences.flush();
		} catch( BackingStoreException exception ) {
			Log.write( exception );
		}
	}

	@Override
	public void removeNode( String path ) {
		try {
			preferences.node( path ).removeNode();
		} catch( BackingStoreException exception ) {
			Log.write( exception );
		}
	}

	@Override
	public void renameNode( String oldPath, String newPath ) {
		preferences.node( oldPath );
	}

}
