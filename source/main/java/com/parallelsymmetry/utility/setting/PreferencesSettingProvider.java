package com.parallelsymmetry.utility.setting;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

import com.parallelsymmetry.utility.log.Log;

public class PreferencesSettingProvider implements WritableSettingProvider {

	private String path;

	public PreferencesSettingProvider( Preferences preferences ) {
		this.path = preferences.absolutePath();
	}

	@Override
	public String get( String path ) {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		int index = path.lastIndexOf( "/" );
		String prefKey = path.substring( index + 1 );

		Preferences node = null;
		Preferences preferences = getPreferences();
		try {
			node = index < 0 ? preferences : preferences.node( path.substring( 0, index ) );
		} catch( Exception exception ) {
			Log.write( Log.ERROR, "Path: " + path.substring( 0, index ) );
			Log.write( exception );
		}
		return node.get( prefKey, null );
	}

	@Override
	public void put( String path, String value ) {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		int index = path.lastIndexOf( "/" );
		String prefKey = path.substring( index + 1 );

		Preferences preferences = getPreferences();
		Preferences node = index < 0 ? preferences : preferences.node( path.substring( 0, index ) );
		if( value == null ) {
			node.remove( prefKey );
		} else {
			node.put( prefKey, value );
		}
	}

	@Override
	public Set<String> getChildNames( String path ) {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		Set<String> names = new HashSet<String>();
		Preferences preferences = getPreferences();
		try {
			names.addAll( Arrays.asList( preferences.node( path ).childrenNames() ) );
		} catch( Exception exception ) {
			Log.write( exception );
		}

		return names;
	}

	@Override
	public boolean nodeExists( String path ) {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		Preferences preferences = getPreferences();
		try {
			return preferences.nodeExists( path );
		} catch( Exception exception ) {
			Log.write( exception );
			return false;
		}
	}

	@Override
	public void removeNode( String path ) throws SettingsStoreException {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		Preferences preferences = getPreferences();
		try {
			preferences.node( path ).removeNode();
		} catch( Exception exception ) {
			throw new SettingsStoreException( exception );
		}
	}

	@Override
	public void flush( String path ) throws SettingsStoreException {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		Preferences preferences = getPreferences();
		try {
			if( !preferences.nodeExists( path ) ) return;
			preferences.node( path ).flush();
		} catch( IllegalArgumentException exception ) {
			Log.write( Log.ERROR, "Path: " + path );
			Log.write( exception );
		} catch( Exception exception ) {
			throw new SettingsStoreException( exception );
		}
	}

	@Override
	public void sync( String path ) throws SettingsStoreException {
		// Incoming paths should always be absolute.
		path = path.substring( 1 );

		Preferences preferences = getPreferences();
		try {
			preferences.node( path ).sync();
		} catch( Exception exception ) {
			throw new SettingsStoreException( exception );
		}
	}

	/**
	 * This ensures a fresh instance of the preferences node is used.
	 * 
	 * @return
	 */
	private Preferences getPreferences() {
		return Preferences.userRoot().node( path );
	}

}
