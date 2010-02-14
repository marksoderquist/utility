package com.parallelsymmetry.swing;

import java.awt.Color;

import junit.framework.TestCase;

import com.parallelsymmetry.swing.Colors;

public class ColorsTest extends TestCase {

	public void testToArgbString() {
		assertEquals( null, Colors.encode( null ) );
		assertEquals( "#00000000", Colors.encode( Colors.CLEAR ) );
		assertEquals( "#80808080", Colors.encode( new Color( 128, 128, 128, 128 ) ) );
		assertEquals( "#ffffffff", Colors.encode( new Color( 255, 255, 255, 255 ) ) );
	}

	public void testParseColor() {
		assertEquals( null, Colors.decode( null ) );
		assertEquals( Colors.CLEAR, Colors.decode( "#00000000" ) );
		assertEquals( new Color( 128, 128, 128, 128 ), Colors.decode( "#80808080" ) );
		assertEquals( new Color( 255, 255, 255, 255 ), Colors.decode( "#ffffffff" ) );
	}

	public void testMakeOpaque() {
		assertEquals( new Color( 0, 0, 0, 255 ), Colors.makeOpaque( new Color( 0, 0, 0, 0 ) ) );
		assertEquals( new Color( 10, 20, 30, 255 ), Colors.makeOpaque( new Color( 10, 20, 30, 40 ) ) );
	}

	public void testMakeTransparent() {
		assertEquals( new Color( 0, 0, 0, 0 ), Colors.makeTransparent( new Color( 0, 0, 0, 255 ), 0f ) );
		assertEquals( new Color( 0, 0, 0, 128 ), Colors.makeTransparent( new Color( 0, 0, 0, 255 ), 0.5f ) );
		assertEquals( new Color( 0, 0, 0, 255 ), Colors.makeTransparent( new Color( 0, 0, 0, 255 ), 1f ) );
	}

	public void testMix() {
		assertEquals( null, Colors.mix( null, Colors.CLEAR, 0.5 ) );
		assertEquals( null, Colors.mix( Colors.CLEAR, null, 0.5 ) );
		assertEquals( new Color( 0, 0, 0, 127 ), Colors.mix( Color.BLACK, Colors.CLEAR, 0.5 ) );
		assertEquals( new Color( 127, 127, 127, 255 ), Colors.mix( Color.BLACK, Color.WHITE, 0.5 ) );
		assertEquals( new Color( 63, 63, 63, 255 ), Colors.mix( Color.BLACK, Color.WHITE, 0.25 ) );
	}

}
