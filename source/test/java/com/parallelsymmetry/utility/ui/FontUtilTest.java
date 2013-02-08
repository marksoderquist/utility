package com.parallelsymmetry.utility.ui;

import java.awt.Font;
import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Test;

import com.parallelsymmetry.utility.ui.FontUtil;

public class FontUtilTest extends TestCase {

	private Font baseFont;

	public FontUtilTest() throws Exception {
		InputStream input = getClass().getResourceAsStream( "/fonts/ConsolaMono.ttf" );
		baseFont = Font.createFont( Font.TRUETYPE_FONT, input );
	}

	@Test
	public void testEncodeNameAndStyle() {
		assertEquals( "Dialog-PLAIN", FontUtil.encodeNameAndStyle( new Font( Font.DIALOG, Font.PLAIN, 8 ) ) );
		assertEquals( "Serif-BOLD", FontUtil.encodeNameAndStyle( new Font( Font.SERIF, Font.BOLD, 8 ) ) );
		assertEquals( "Consola Mono-ITALIC", FontUtil.encodeNameAndStyle( baseFont.deriveFont( Font.ITALIC ) ) );
	}

	@Test
	public void testEncodeStyle() {
		assertEquals( "PLAIN", FontUtil.encodeStyle( new Font( Font.DIALOG, Font.PLAIN, 8 ) ) );
		assertEquals( "BOLD", FontUtil.encodeStyle( new Font( Font.DIALOG, Font.BOLD, 8 ) ) );
		assertEquals( "ITALIC", FontUtil.encodeStyle( new Font( Font.DIALOG, Font.ITALIC, 8 ) ) );
		assertEquals( "BOLDITALIC", FontUtil.encodeStyle( new Font( Font.DIALOG, Font.BOLD | Font.ITALIC, 8 ) ) );
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
