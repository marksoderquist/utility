package com.parallelsymmetry.escape.utility.setting;

import java.util.prefs.Preferences;

public class PreferencesSettingProvider implements SettingProvider {

	private Preferences preferences;

	public PreferencesSettingProvider( Preferences preferences ) {
		this.preferences = preferences;
	}

	@Override
	public boolean isWritable() {
		return true;
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
	}

}
