package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

import junit.framework.TestCase;

public class BundlesTest extends TestCase {

	private static final String TEST_BUNDLE = "test.bundle";

	private static final String LOADER_1_BUNDLE = "loader.1.bundle";

	private static final String LOADER_2_BUNDLE = "loader.2.bundle";

	private static final String SPANISH = "es";

	private static Locale locale;

	public void setUp() {
		locale = Locale.getDefault();
	}

	public void testGetString() throws Exception {
		assertEquals( "Exit_en", Bundles.getString( TEST_BUNDLE, "exit", null ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals( "Exit_es", Bundles.getString( TEST_BUNDLE, "exit", null ) );
	}

	public void testGetStringShowingKey() throws Exception {
		assertEquals( "String should be the key.", "[" + TEST_BUNDLE + ":]", Bundles.getString( TEST_BUNDLE, "", null ) );
		assertEquals( "String should be the key.", "[" + TEST_BUNDLE + ":test.property.name]", Bundles.getString( TEST_BUNDLE, "test.property.name", null ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals( "String should be the key.", "[" + TEST_BUNDLE + ":]", Bundles.getString( TEST_BUNDLE, "", null ) );
		assertEquals( "String should be the key.", "[" + TEST_BUNDLE + ":test.property.name]", Bundles.getString( TEST_BUNDLE, "test.property.name", null ) );
	}

	public void testGetStringNotShowingKey() throws Exception {
		assertEquals( null, Bundles.getString( TEST_BUNDLE, "", null, false ) );
		assertEquals( "Exit_en", Bundles.getString( TEST_BUNDLE, "exit", null, false ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals( null, Bundles.getString( TEST_BUNDLE, "", null, false ) );
		assertEquals( "Exit_es", Bundles.getString( TEST_BUNDLE, "exit", null, false ) );
	}

	public void testClassLoaderHandling() throws Exception {
		ClassLoader loader1 = new URLClassLoader( new URL[] { new File( "source/test/resources/loader1" ).toURI().toURL() } );
		ClassLoader loader2 = new URLClassLoader( new URL[] { new File( "source/test/resources/loader2" ).toURI().toURL() } );
		
		Bundles.register( loader1 );
		Bundles.register( loader2 );
		String text = Bundles.getString( "bundle", "string", null, false );
		assertEquals( "Loader 1 String", text );
	}

	public void tearDown() {
		Locale.setDefault( locale );
	}

}
