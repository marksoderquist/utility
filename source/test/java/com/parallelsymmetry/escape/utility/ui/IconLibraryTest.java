package com.parallelsymmetry.escape.utility.ui;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.Accessor;
import com.parallelsymmetry.escape.utility.JavaUtil;

public class IconLibraryTest extends TestCase {

	public void testConstructor() {
		assertNotNull( new IconLibrary() );
	}

	public void testGetIconUrl() throws Exception {
		IconLibrary library = new IconLibrary();
		library.addPath( JavaUtil.getPackagePath( getClass() ) );

		assertNull( "Null icon should not be found and was.", Accessor.callMethod( library, "getIconUrl", "null" ) );
		assertNotNull( "Test icon should be found and was not.", Accessor.callMethod( library, "getIconUrl", "test" ) );
	}

}
