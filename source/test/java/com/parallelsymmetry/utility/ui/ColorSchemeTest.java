package com.parallelsymmetry.utility.ui;

import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorSchemeTest extends BaseTestCase {

	@Test
	public void testGetPrimary() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ff0000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getPrimary( -1 ) );
		assertEquals( Color.decode( "#7f0000" ), scheme.getPrimary( -0.5 ) );
		assertEquals( Color.decode( "#ff0000" ), scheme.getPrimary( 0 ) );
		assertEquals( Color.decode( "#ff7f7f" ), scheme.getPrimary( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getPrimary( 1 ) );
	}

	@Test
	public void testGetPrimaryWithBlack() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#000000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getPrimary( -1 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getPrimary( -0.5 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getPrimary( 0 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getPrimary( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getPrimary( 1 ) );
	}

	@Test
	public void testGetPrimaryWithWhite() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ffffff" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getPrimary( -1 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getPrimary( -0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getPrimary( 0 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getPrimary( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getPrimary( 1 ) );
	}

	@Test
	public void testGetSecondaryA() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ff0000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryA( -1 ) );
		assertEquals( Color.decode( "#7f0040" ), scheme.getSecondaryA( -0.5 ) );
		assertEquals( Color.decode( "#ff0080" ), scheme.getSecondaryA( 0 ) );
		assertEquals( Color.decode( "#ff7fbf" ), scheme.getSecondaryA( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryA( 1 ) );
	}

	@Test
	public void testGetSecondaryAWithBlack() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#000000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryA( -1 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryA( -0.5 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryA( 0 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getSecondaryA( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryA( 1 ) );
	}

	@Test
	public void testGetSecondaryAWithWhite() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ffffff" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryA( -1 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getSecondaryA( -0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryA( 0 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryA( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryA( 1 ) );
	}

	@Test
	public void testGetSecondaryB() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ff0000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryB( -1 ) );
		assertEquals( Color.decode( "#7f4000" ), scheme.getSecondaryB( -0.5 ) );
		assertEquals( Color.decode( "#ff8000" ), scheme.getSecondaryB( 0 ) );
		assertEquals( Color.decode( "#ffbf7f" ), scheme.getSecondaryB( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryB( 1 ) );
	}

	@Test
	public void testGetSecondaryBWithBlack() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#000000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryB( -1 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryB( -0.5 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryB( 0 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getSecondaryB( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryB( 1 ) );
	}

	@Test
	public void testGetSecondaryBWithWhite() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ffffff" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getSecondaryB( -1 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getSecondaryB( -0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryB( 0 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryB( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getSecondaryB( 1 ) );
	}

	@Test
	public void testGetComplement() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ff0000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getComplement( -1 ) );
		assertEquals( Color.decode( "#007f7f" ), scheme.getComplement( -0.5 ) );
		assertEquals( Color.decode( "#00ffff" ), scheme.getComplement( 0 ) );
		assertEquals( Color.decode( "#7fffff" ), scheme.getComplement( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getComplement( 1 ) );
	}

	@Test
	public void testGetComplementWithBlack() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#000000" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getComplement( -1 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getComplement( -0.5 ) );
		assertEquals( Color.decode( "#000000" ), scheme.getComplement( 0 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getComplement( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getComplement( 1 ) );
	}

	@Test
	public void testGetComplementWithWhite() {
		ColorScheme scheme = new ColorScheme( Color.decode( "#ffffff" ) );

		assertEquals( Color.decode( "#000000" ), scheme.getComplement( -1 ) );
		assertEquals( Color.decode( "#7f7f7f" ), scheme.getComplement( -0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getComplement( 0 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getComplement( 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), scheme.getComplement( 1 ) );
	}

}
