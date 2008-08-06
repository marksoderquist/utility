package com.parallelsymmetry.util;

public class PreferencesUtil {

	public static final String PREFERENCES_INDEX_SEPARATOR = "-";

	public static final java.util.prefs.Preferences node( java.util.prefs.Preferences preferences, String path, int index ) {
		return preferences.node( path + PREFERENCES_INDEX_SEPARATOR + index );
	}
}
