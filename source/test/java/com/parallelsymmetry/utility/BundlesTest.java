package com.parallelsymmetry.utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BundlesTest extends BaseTestCase {

	private static final String TEST_BUNDLE = "test.bundle";

	private static final String SPANISH = "es";

	private static Locale locale;

	@BeforeEach
	@Override
	public void setup() throws Exception {
		super.setup();
		locale = Locale.getDefault();
	}

	@AfterEach
	@Override
	public void teardown() throws Exception {
		Locale.setDefault( locale );
		super.teardown();
	}

	@Test
	public void testGetString() {
		assertEquals( "Exit_en", Bundles.getString( TEST_BUNDLE, "exit", null ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals( "Exit_es", Bundles.getString( TEST_BUNDLE, "exit", null ) );
	}

	@Test
	public void testGetStringShowingKey() {
		assertEquals(  "[" + TEST_BUNDLE + ":]", Bundles.getString( TEST_BUNDLE, "", null ) );
		assertEquals(  "[" + TEST_BUNDLE + ":test.property.name]", Bundles.getString( TEST_BUNDLE, "test.property.name", null ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals(  "[" + TEST_BUNDLE + ":]", Bundles.getString( TEST_BUNDLE, "", null ) );
		assertEquals(  "[" + TEST_BUNDLE + ":test.property.name]", Bundles.getString( TEST_BUNDLE, "test.property.name", null ) );
	}

	@Test
	public void testGetStringNotShowingKey() {
		assertEquals( null, Bundles.getString( TEST_BUNDLE, "", null, false ) );
		assertEquals( "Exit_en", Bundles.getString( TEST_BUNDLE, "exit", null, false ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals( null, Bundles.getString( TEST_BUNDLE, "", null, false ) );
		assertEquals( "Exit_es", Bundles.getString( TEST_BUNDLE, "exit", null, false ) );
	}

	@Test
	public void testMultiStreamHandling() {
		assertEquals( "[bundles/utility:source]", Bundles.getString( "bundles/utility", "source" ) );
		assertEquals( "test", Bundles.getString( "bundles/utility", "target" ) );
	}

	@Test
	public void testClassLoaderHandling() throws Exception {
		ClassLoader loader1 = new URLClassLoader( new URL[]{ new File( "source/test/resources/loader1" ).toURI().toURL() }, null );
		ClassLoader loader2 = new URLClassLoader( new URL[]{ new File( "source/test/resources/loader2" ).toURI().toURL() }, null );

		assertNull( Bundles.getString( "bundle", "string", null, false ) );
		assertEquals( "Loader 1 String", Bundles.getString( loader1, "bundle", "string", null, false ) );
		assertEquals( "Loader 2 String", Bundles.getString( loader2, "bundle", "string", null, false ) );
	}

}
