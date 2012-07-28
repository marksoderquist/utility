package com.parallelsymmetry.escape.utility.ui;

import java.awt.Color;

import junit.framework.TestCase;

public class ColorSchemeTest extends TestCase {

	public void testGetPrimary() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ff0000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getPrimary( -1 ) );
		assertEquals( Color.decode( "#7f0000" ), scheme.getPrimary( -0.5 ) );
		assertEquals( Color.decode( "#ff0000" ), scheme.getPrimary( 0 ) );
		assertEquals( Color.decode( "#ff7f7f" ), scheme.getPrimary( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getPrimary( 1 ) );
	}

	public void testGetPrimaryWithBlack() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#000000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getPrimary( -1 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getPrimary( -0.5 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getPrimary( 0 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getPrimary( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getPrimary( 1 ) );
	}

	public void testGetPrimaryWithWhite() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ffffff" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getPrimary( -1 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getPrimary( -0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getPrimary( 0 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getPrimary( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getPrimary( 1 ) );
	}

	public void testGetSecondaryA() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ff0000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryA( -1 ) );
		assertEquals( Color.decode( "#7f0040" ), scheme.getSecondaryA( -0.5 ) );
		assertEquals( Color.decode( "#ff0080" ), scheme.getSecondaryA( 0 ) );
		assertEquals( Color.decode( "#ff7fbf" ), scheme.getSecondaryA( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryA( 1 ) );
	}

	public void testGetSecondaryAWithBlack() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#000000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryA( -1 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryA( -0.5 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryA( 0 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getSecondaryA( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryA( 1 ) );
	}

	public void testGetSecondaryAWithWhite() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ffffff" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryA( -1 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getSecondaryA( -0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryA( 0 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryA( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryA( 1 ) );
	}

	public void testGetSecondaryB() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ff0000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryB( -1 ) );
		assertEquals( Color.decode( "#7f4000" ), scheme.getSecondaryB( -0.5 ) );
		assertEquals( Color.decode( "#ff8000" ), scheme.getSecondaryB( 0 ) );
		assertEquals( Color.decode( "#ffbf7f" ), scheme.getSecondaryB( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryB( 1 ) );
	}

	public void testGetSecondaryBWithBlack() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#000000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryB( -1 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryB( -0.5 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryB( 0 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getSecondaryB( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryB( 1 ) );
	}

	public void testGetSecondaryBWithWhite() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ffffff" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryB( -1 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getSecondaryB( -0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryB( 0 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryB( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryB( 1 ) );
	}

	public void testGetComplement() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ff0000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getComplement( -1 ) );
		assertEquals( Color.decode( "#007f7f" ), scheme.getComplement( -0.5 ) );
		assertEquals( Color.decode( "#00ffff" ), scheme.getComplement( 0 ) );
		assertEquals( Color.decode( "#7fffff" ), scheme.getComplement( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getComplement( 1 ) );
	}

	public void testGetComplementWithBlack() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#000000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getComplement( -1 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getComplement( -0.5 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getComplement( 0 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getComplement( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getComplement( 1 ) );
	}

	public void testGetComplementWithWhite() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ffffff" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getComplement( -1 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getComplement( -0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getComplement( 0 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getComplement( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getComplement( 1 ) );
	}

}
