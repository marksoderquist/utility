package com.parallelsymmetry.escape.utility.ui;

import java.awt.image.RGBImageFilter;

public class GrayscaleFilter extends RGBImageFilter {

	private float brightness;

	private float attenuation;

	public GrayscaleFilter() {
		this( 0 );
	}

	public GrayscaleFilter( double brightness ) {
		this( brightness, 0 );
	}

	public GrayscaleFilter( double brightness, double attenuation ) {
		this.brightness = (float)brightness;
		this.attenuation = (float)attenuation;
		canFilterIndexColorModel = true;
	}

	@Override
	public int filterRGB( int x, int y, int rgb ) {
		int oa = ( rgb & 0xff000000 ) >>> 24;
		int or = ( rgb & 0x00ff0000 ) >>> 16;
		int og = ( rgb & 0x0000ff00 ) >>> 8;
		int ob = ( rgb & 0x000000ff ) >>> 0;

		int v = ( or + og + ob ) / 3;
		v = calc( v, brightness );
		v = calc( v, attenuation );

		int a = oa;
		int r = v;
		int g = v;
		int b = v;

		return ( a << 24 ) + ( r << 16 ) + ( g << 8 ) + b;
	}

	private int calc( int value, float scale ) {
		int delta = value;
		if( scale > 0 ) delta = 255 - value;
		return (int)( value + ( delta * scale ) );
	}
}
