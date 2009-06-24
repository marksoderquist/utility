package com.parallelsymmetry.util;

public class PreferencesUtil {

	public static final String PREFERENCES_INDEX_SEPARATOR = "-";

	public static final String key( String key, int index ) {
		return key + PREFERENCES_INDEX_SEPARATOR + index;
	}

	public static final String path( String path, int index ) {
		return key( path, index );
	}

	public static final java.util.prefs.Preferences node( java.util.prefs.Preferences preferences, String path, int index ) {
		return preferences.node( path( path, index ) );
	}
}
