package com.parallelsymmetry.escape.utility.ui;

import java.awt.Font;
import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Test;

public class FontUtilTest extends TestCase {

	private Font baseFont;

	public FontUtilTest() throws Exception {
		InputStream input = getClass().getResourceAsStream( "/fonts/ConsolaMono.ttf" );
		baseFont = Font.createFont( Font.TRUETYPE_FONT, input );
	}

	@Test
	public void testFindFontForWidth() {
		Font font = FontUtil.findFontForWidth( baseFont, "M", 14 );
		assertEquals( 31.852116f, font.getSize2D() );
	}

	@Test
	public void testFindFontForHeight() {
		Font font = FontUtil.findFontForHeight( baseFont, "M", 14 );
		assertEquals( 19.116705f, font.getSize2D() );
	}

}
