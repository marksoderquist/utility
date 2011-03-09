package com.parallelsymmetry.escape.utility;

import java.util.Locale;

import junit.framework.TestCase;

public class BundlesTest extends TestCase {

	private static final String TEST_BUNDLE = "test.bundle";

	private static final String SPANISH = "es";

	private static Locale locale;

	public void setUp() {
		locale = Locale.getDefault();
	}

	public void testGetString() throws Exception {
		assertEquals( "Exit", Bundles.getString( TEST_BUNDLE, "exit", null ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals( "Salir", Bundles.getString( TEST_BUNDLE, "exit", null ) );
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
		assertEquals( "Exit", Bundles.getString( TEST_BUNDLE, "exit", null, false ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals( null, Bundles.getString( TEST_BUNDLE, "", null, false ) );
		assertEquals( "Salir", Bundles.getString( TEST_BUNDLE, "exit", null, false ) );
	}

	public void tearDown() {
		Locale.setDefault( locale );
	}

}
