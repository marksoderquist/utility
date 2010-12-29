package com.parallelsymmetry.escape.utility;

public class PreferencesUtil {

	public static final String PREFERENCES_INDEX_SEPARATOR = "-";

	public static final String getIndexedKey( String key, int index ) {
		return key + PREFERENCES_INDEX_SEPARATOR + index;
	}

	public static final String getIndexedPath( String path, int index ) {
		return getIndexedKey( path, index );
	}

	public static final java.util.prefs.Preferences getIndexedNode( java.util.prefs.Preferences preferences, String path, int index ) {
		return preferences.node( getIndexedPath( path, index ) );
	}
}
