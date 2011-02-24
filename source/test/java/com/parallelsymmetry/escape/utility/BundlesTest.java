package com.parallelsymmetry.escape.utility;

import java.util.Locale;

import junit.framework.TestCase;

public class BundlesTest extends TestCase {

	private static final String SPANISH = "es";

	private static Locale locale;

	public void setUp() {
		locale = Locale.getDefault();
	}

	public void testGetString() throws Exception {
		assertEquals( "Exit", Bundles.getString( Bundles.MESSAGES, "exit", null ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals( "Salir", Bundles.getString( Bundles.MESSAGES, "exit", null ) );
	}

	public void testGetStringShowingKey() throws Exception {
		assertEquals( "String should be the key.", "[messages:]", Bundles.getString( Bundles.MESSAGES, "", null ) );
		assertEquals( "String should be the key.", "[messages:test.property.name]", Bundles.getString( Bundles.MESSAGES, "test.property.name", null ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals( "String should be the key.", "[messages:]", Bundles.getString( Bundles.MESSAGES, "", null ) );
		assertEquals( "String should be the key.", "[messages:test.property.name]", Bundles.getString( Bundles.MESSAGES, "test.property.name", null ) );
	}

	public void testGetStringNotShowingKey() throws Exception {
		assertEquals( null, Bundles.getString( Bundles.MESSAGES, "", null, false ) );
		assertEquals( "Exit", Bundles.getString( Bundles.MESSAGES, "exit", null, false ) );
		Locale.setDefault( new Locale( SPANISH ) );
		assertEquals( null, Bundles.getString( Bundles.MESSAGES, "", null, false ) );
		assertEquals( "Salir", Bundles.getString( Bundles.MESSAGES, "exit", null, false ) );
	}

	public void tearDown() {
		Locale.setDefault( locale );
	}

}
