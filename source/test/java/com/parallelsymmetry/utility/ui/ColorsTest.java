package com.parallelsymmetry.utility.ui;

import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorsTest extends BaseTestCase {

	@Test
	public void testToArgbString() {
		assertEquals( null, Colors.encode( null ) );
		assertEquals( "#00000000", Colors.encode( Colors.CLEAR ) );
		assertEquals( "#80808080", Colors.encode( new Color( 128, 128, 128, 128 ) ) );
		assertEquals( "#ffffffff", Colors.encode( new Color( 255, 255, 255, 255 ) ) );
	}

	@Test
	public void testParseColor() {
		assertEquals( null, Colors.decode( null ) );
		assertEquals( Colors.CLEAR, Colors.decode( "#00000000" ) );
		assertEquals( new Color( 128, 128, 128, 128 ), Colors.decode( "#80808080" ) );
		assertEquals( new Color( 255, 255, 255, 255 ), Colors.decode( "#ffffffff" ) );
	}

	@Test
	public void testMakeOpaque() {
		assertEquals( new Color( 0, 0, 0, 255 ), Colors.makeOpaque( new Color( 0, 0, 0, 0 ) ) );
		assertEquals( new Color( 10, 20, 30, 255 ), Colors.makeOpaque( new Color( 10, 20, 30, 40 ) ) );
	}

	@Test
	public void testMakeTransparent() {
		assertEquals( new Color( 0, 0, 0, 0 ), Colors.makeTransparent( new Color( 0, 0, 0, 255 ), 0f ) );
		assertEquals( new Color( 0, 0, 0, 128 ), Colors.makeTransparent( new Color( 0, 0, 0, 255 ), 0.5f ) );
		assertEquals( new Color( 0, 0, 0, 255 ), Colors.makeTransparent( new Color( 0, 0, 0, 255 ), 1f ) );
	}

	@Test
	public void testMix() {
		assertEquals( null, Colors.mix( null, Colors.CLEAR, 0.5 ) );
		assertEquals( null, Colors.mix( Colors.CLEAR, null, 0.5 ) );
		assertEquals( new Color( 0, 0, 0, 127 ), Colors.mix( Color.BLACK, Colors.CLEAR, 0.5 ) );
		assertEquals( new Color( 127, 127, 127, 255 ), Colors.mix( Color.BLACK, Color.WHITE, 0.5 ) );
		assertEquals( new Color( 63, 63, 63, 255 ), Colors.mix( Color.BLACK, Color.WHITE, 0.25 ) );
	}

	@Test
	public void testMixSrcOverDst() {
		assertEquals( null, Colors.mixSrcOverDst( null, Colors.CLEAR, 0.5 ) );
		assertEquals( null, Colors.mixSrcOverDst( Colors.CLEAR, null, 0.5 ) );
		assertEquals( new Color( 0, 0, 0, 255 ), Colors.mixSrcOverDst( Color.BLACK, Colors.CLEAR, 0.5 ) );
		assertEquals( new Color( 128, 128, 128, 255 ), Colors.mixSrcOverDst( Color.BLACK, Color.WHITE, 0.5 ) );
		assertEquals( new Color( 64, 64, 64, 255 ), Colors.mixSrcOverDst( Color.BLACK, Color.WHITE, 0.25 ) );

		Color a = new Color( 0, 0, 0, 128 );
		Color b = new Color( 255, 255, 255, 128 );
		assertEquals( new Color( 102, 102, 102, 160 ), Colors.mixSrcOverDst( a, b, 0.5 ) );
		assertEquals( new Color( 153, 153, 153, 160 ), Colors.mixSrcOverDst( b, a, 0.5 ) );
	}

	@Test
	public void testDeriveWithColor() {
		assertEquals( new Color( 116, 165, 209 ), Colors.derive( new Color( 51, 98, 140 ), 0f, -0.19f, 0.27f, 0 ) );
	}

	@Test
	public void testGetIntensity() {
		assertEquals( -1f, Colors.getIntensity( Color.BLACK ) );
		assertEquals( -0.0039215684f, Colors.getIntensity( new Color( 127, 127, 127 ) ) );
		assertEquals( 0.003921628f, Colors.getIntensity( Color.GRAY ) );
		assertEquals( 1f, Colors.getIntensity( Color.WHITE ) );
	}

	@Test
	public void testGetShade() {
		Color color = Color.decode( "#ff0000" );

		assertEquals( Color.decode( "#000000" ), Colors.getShade( color, -1 ) );
		assertEquals( Color.decode( "#7f0000" ), Colors.getShade( color, -0.5 ) );
		assertEquals( Color.decode( "#ff0000" ), Colors.getShade( color, 0 ) );
		assertEquals( Color.decode( "#ff7f7f" ), Colors.getShade( color, 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), Colors.getShade( color, 1 ) );
	}

	@Test
	public void testGetShadeWithBlack() {
		Color color = Color.BLACK;

		assertEquals( Color.decode( "#000000" ), Colors.getShade( color, -1 ) );
		assertEquals( Color.decode( "#000000" ), Colors.getShade( color, -0.5 ) );
		assertEquals( Color.decode( "#000000" ), Colors.getShade( color, 0 ) );
		assertEquals( Color.decode( "#7f7f7f" ), Colors.getShade( color, 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), Colors.getShade( color, 1 ) );
	}

	@Test
	public void testGetShadeWithWhite() {
		Color color = Color.WHITE;

		assertEquals( Color.decode( "#000000" ), Colors.getShade( color, -1 ) );
		assertEquals( Color.decode( "#7f7f7f" ), Colors.getShade( color, -0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), Colors.getShade( color, 0 ) );
		assertEquals( Color.decode( "#ffffff" ), Colors.getShade( color, 0.5 ) );
		assertEquals( Color.decode( "#ffffff" ), Colors.getShade( color, 1 ) );
	}

	@Test
	public void testGetHue() {
		assertEquals( Color.decode( "#ff0000" ), Colors.getHue( Color.decode( "#000000" ) ) );
		assertEquals( Color.decode( "#ff0000" ), Colors.getHue( Color.decode( "#ffffff" ) ) );

		assertEquals( Color.decode( "#ff0000" ), Colors.getHue( Color.decode( "#ff0000" ) ) );
		assertEquals( Color.decode( "#00ff00" ), Colors.getHue( Color.decode( "#00ff00" ) ) );
		assertEquals( Color.decode( "#0000ff" ), Colors.getHue( Color.decode( "#0000ff" ) ) );
	}

	@Test
	public void testGetSecondary() {
		// Black
		assertEquals( Color.decode( "#000000" ), Colors.getSecondary( Color.decode( "#000000" ), -30 ) );
		assertEquals( Color.decode( "#000000" ), Colors.getSecondary( Color.decode( "#000000" ), 30 ) );

		// White
		assertEquals( Color.decode( "#ffffff" ), Colors.getSecondary( Color.decode( "#ffffff" ), -30 ) );
		assertEquals( Color.decode( "#ffffff" ), Colors.getSecondary( Color.decode( "#ffffff" ), 30 ) );

		// Colors
		assertEquals( Color.decode( "#ff0080" ), Colors.getSecondary( Color.decode( "#ff0000" ), -30 ) );
		assertEquals( Color.decode( "#ff8000" ), Colors.getSecondary( Color.decode( "#ff0000" ), 30 ) );
		assertEquals( Color.decode( "#80ff00" ), Colors.getSecondary( Color.decode( "#00ff00" ), -30 ) );
		assertEquals( Color.decode( "#00ff80" ), Colors.getSecondary( Color.decode( "#00ff00" ), 30 ) );
		assertEquals( Color.decode( "#007fff" ), Colors.getSecondary( Color.decode( "#0000ff" ), -30 ) );
		assertEquals( Color.decode( "#8000ff" ), Colors.getSecondary( Color.decode( "#0000ff" ), 30 ) );
	}

	@Test
	public void testGetComplement() {
		// Black
		assertEquals( Color.decode( "#000000" ), Colors.getComplement( Color.decode( "#000000" ) ) );

		// White
		assertEquals( Color.decode( "#ffffff" ), Colors.getComplement( Color.decode( "#ffffff" ) ) );

		// Colors
		assertEquals( Color.decode( "#00ffff" ), Colors.getComplement( Color.decode( "#ff0000" ) ) );
		assertEquals( Color.decode( "#ff00ff" ), Colors.getComplement( Color.decode( "#00ff00" ) ) );
		assertEquals( Color.decode( "#ffff00" ), Colors.getComplement( Color.decode( "#0000ff" ) ) );
	}

	@Test
	public void testGetHsvOffsets() {
		float[] offsets = Colors.getHsvOffsets( new Color( 51, 98, 140 ), new Color( 116, 165, 209 ) );
		assertEquals( 2.014041E-4f, offsets[ 0 ] );
		assertEquals( -0.1907382f, offsets[ 1 ] );
		assertEquals( 0.27058822f, offsets[ 2 ] );
	}

}
