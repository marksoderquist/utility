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
		path = path.substring( 1 );

		try {
			preferences.sync();
			return preferences.nodeExists( path );
		} catch( BackingStoreException exception ) {
			Log.write( exception );
		}
		
		return false;
	}

	@Override
	public String get( String path ) {
		path = path.substring( 1 );
		int index = path.lastIndexOf( "/" );
		String prefPath = index < 0 ? "." : path.substring( 0, index );
		String prefKey = path.substring( index + 1 );
		
		Preferences node = preferences.node( prefPath );
		try {
			node.sync();
		} catch( BackingStoreException exception ) {
			Log.write( exception );
		}
		
		return node.get( prefKey, null );
	}

	@Override
	public void put( String path, String value ) {
		path = path.substring( 1 );
		int index = path.lastIndexOf( "/" );
		String prefPath = index < 0 ? "." : path.substring( 0, index );
		String prefKey = path.substring( index + 1 );

		Preferences node = preferences.node( prefPath );
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
	public void removeNode( String path ) {
		Preferences node = this.preferences.node( path.substring( 1 ) );

		try {
			node.removeNode();
			node.flush();
		} catch( BackingStoreException exception ) {
			Log.write( exception );
		}
	}

}
