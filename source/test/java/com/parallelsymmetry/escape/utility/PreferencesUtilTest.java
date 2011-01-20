package com.parallelsymmetry.escape.utility;

import java.util.prefs.Preferences;

import junit.framework.TestCase;

import org.junit.Test;

public class PreferencesUtilTest extends TestCase {

	private static final String PATH = "/com/parallelsymmetry/escape/utility/test";

	@Test
	public void testGetIndexedNode() throws Exception {
		int index = 0;
		Preferences preferences = PreferencesUtil.getIndexedNode( Preferences.userRoot(), PATH, index );
		assertEquals( PATH + PreferencesUtil.PREFERENCES_INDEX_SEPARATOR + index, preferences.absolutePath() );
	}

}
