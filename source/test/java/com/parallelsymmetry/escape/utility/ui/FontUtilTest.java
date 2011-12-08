package com.parallelsymmetry.escape.utility.ui;

import java.awt.Font;

import junit.framework.TestCase;

import org.junit.Test;

public class FontUtilTest extends TestCase {

	@Test
	public void testFindFontForWidth() {
		Font font = FontUtil.findFontForWidth( new Font( Font.SANS_SERIF, Font.PLAIN, 1 ), "M", 14 );
		assertEquals( 20.494053f, font.getSize2D() );
	}

	@Test
	public void testFindFontForHeight() {
		Font font = FontUtil.findFontForHeight( new Font( Font.SANS_SERIF, Font.PLAIN, 1 ), "M", 14 );
		assertEquals( 19.55478f, font.getSize2D() );
	}

}
