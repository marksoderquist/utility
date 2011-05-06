package com.parallelsymmetry.escape.utility.ui;

import java.io.InputStream;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.Descriptor;

public class ColorSchemeTest extends TestCase {

	private ColorScheme colorScheme;

	@Override
	public void setUp() throws Exception {
		InputStream input = getClass().getResourceAsStream( "test.color.scheme.xml" );
		Descriptor descriptor = new Descriptor( input );
		colorScheme = new ColorScheme( descriptor );
	}

	public void testPrimaryColors() {
		assertEquals( "406180", colorScheme.getPrimaryCode( 1 ) );
		assertEquals( "3C4E60", colorScheme.getPrimaryCode( 2 ) );
		assertEquals( "153553", colorScheme.getPrimaryCode( 3 ) );
		assertEquals( "789CC0", colorScheme.getPrimaryCode( 4 ) );
		assertEquals( "8BA6C0", colorScheme.getPrimaryCode( 5 ) );
	}

	public void testSecondaryAColors() {
		assertEquals( "475187", colorScheme.getSecondaryACode( 1 ) );
		assertEquals( "414765", colorScheme.getSecondaryACode( 2 ) );
		assertEquals( "172158", colorScheme.getSecondaryACode( 3 ) );
		assertEquals( "7E89C3", colorScheme.getSecondaryACode( 4 ) );
		assertEquals( "9098C3", colorScheme.getSecondaryACode( 5 ) );
	}

	public void testSecondaryBColors() {
		assertEquals( "387974", colorScheme.getSecondaryBCode( 1 ) );
		assertEquals( "365B58", colorScheme.getSecondaryBCode( 2 ) );
		assertEquals( "124F4A", colorScheme.getSecondaryBCode( 3 ) );
		assertEquals( "70BCB7", colorScheme.getSecondaryBCode( 4 ) );
		assertEquals( "84BCB8", colorScheme.getSecondaryBCode( 5 ) );
	}

	public void testComplementColors() {
		assertEquals( "C59A5B", colorScheme.getComplementCode( 1 ) );
		assertEquals( "947B58", colorScheme.getComplementCode( 2 ) );
		assertEquals( "80581D", colorScheme.getComplementCode( 3 ) );
		assertEquals( "E2BD87", colorScheme.getComplementCode( 4 ) );
		assertEquals( "E2C79F", colorScheme.getComplementCode( 5 ) );
	}

}
