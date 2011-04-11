package com.parallelsymmetry.escape.utility.ui;

import java.awt.image.RGBImageFilter;

public class NoAlphaFilter extends RGBImageFilter {

	@Override
	public int filterRGB( int x, int y, int rgb ) {
		int oa = ( rgb & 0xff000000 ) >>> 24;
		int a = oa < 128 ? 0 : 255;
		return ( a << 24 ) + ( rgb & 0x00ffffff );
	}

}
