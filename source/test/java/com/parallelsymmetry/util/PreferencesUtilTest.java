package com.parallelsymmetry.util;

import junit.framework.TestCase;

import org.junit.Test;

public class PreferencesUtilTest extends TestCase {

	private static final String PATH = "/com/parallelsymmetry/utility/testing";

	@Test
	public void testNode() throws Exception {
		int index = 0;
		java.util.prefs.Preferences preferences = PreferencesUtil.node( Preferences.userRoot(), PATH, index );
		assertEquals( "/com/parallelsymmetry/utility/testing" + PreferencesUtil.PREFERENCES_INDEX_SEPARATOR + index, preferences.absolutePath() );
	}

}
