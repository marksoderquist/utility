package com.parallelsymmetry.utility;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

import com.parallelsymmetry.utility.Bundles;

import junit.framework.TestCase;

public class BundlesTest extends TestCase {

	private static final String TEST_BUNDLE = "test.bundle";

	private static final String SPANISH = "es";

	private static Locale locale;

	@Override
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

	public void testMultiStreamHandling() {
		assertEquals( "main", Bundles.getString( "bundles/utility", "source" ) );
		assertEquals( "test", Bundles.getString( "bundles/utility", "target" ) );
	}

	public void testClassLoaderHandling() throws Exception {
		ClassLoader loader1 = new URLClassLoader( new URL[] { new File( "target/test/java/loader1" ).toURI().toURL() }, null );
		ClassLoader loader2 = new URLClassLoader( new URL[] { new File( "target/test/java/loader2" ).toURI().toURL() }, null );

		assertNull( Bundles.getString( "bundle", "string", null, false ) );
		assertEquals( "Loader 1 String", Bundles.getString( loader1, "bundle", "string", null, false ) );
		assertEquals( "Loader 2 String", Bundles.getString( loader2, "bundle", "string", null, false ) );
	}

	@Override
	public void tearDown() {
		Locale.setDefault( locale );
	}

}
