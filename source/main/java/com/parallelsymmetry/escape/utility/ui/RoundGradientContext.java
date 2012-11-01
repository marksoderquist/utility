package com.parallelsymmetry.escape.utility.ui;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.geom.Point2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class RoundGradientContext implements PaintContext {

	protected Point2D center;

	protected double start;

	protected double extent;

	protected double[] stops;

	protected Color[] colors;

	private static final double PI2 = Math.PI * 2;

	public RoundGradientContext( Point2D center, double start, double extent, double[] stops, Color[] colors ) {
		this.center = center;
		this.start = start % PI2;
		this.extent = extent;
		this.stops = stops;
		this.colors = colors;
	}

	@Override
	public void dispose() {}

	@Override
	public ColorModel getColorModel() {
		return ColorModel.getRGBdefault();
	}

	@Override
	public Raster getRaster( int x, int y, int w, int h ) {
		WritableRaster raster = getColorModel().createCompatibleWritableRaster( w, h );

		int[] data = new int[w * h * 4];
		for( int j = 0; j < h; j++ ) {
			for( int i = 0; i < w; i++ ) {
				double px = x + i - center.getX();
				double py = y + j - center.getY();

				double rise = -Math.atan2( py, px );
				if( rise < 0 ) rise += PI2;
				double delta = rise - start;
				if( delta < 0 ) delta += PI2;
				delta %= extent;

				double ratio = delta / extent;
				if( ratio < 0.0 ) ratio = 0.0;
				if( ratio > 1.0 ) ratio = 1.0;

				int base = ( j * w + i ) * 4;
				System.arraycopy( getColor( ratio ), 0, data, base, 4 );
			}
		}
		raster.setPixels( 0, 0, w, h, data );

		return raster;
	}

	private int[] getColor( double stop ) {
		int index = 0;
		for( index = 0; index < stops.length; index++ ) {
			if( stops[index] > stop ) break;
		}
		index--;

		Color color1 = colors[index];

		int[] color = new int[4];

		if( stop == stops[index] ) {
			color[0] = color1.getRed();
			color[1] = color1.getGreen();
			color[2] = color1.getBlue();
			color[3] = color1.getAlpha();
		} else {
			Color color2 = colors[index + 1];
			double stop1 = stops[index];
			double stop2 = stops[index + 1];
			double delta = stop - stop1;
			double ratio = delta / ( stop2 - stop1 );
			color[0] = (int)( color1.getRed() + ratio * ( color2.getRed() - color1.getRed() ) );
			color[1] = (int)( color1.getGreen() + ratio * ( color2.getGreen() - color1.getGreen() ) );
			color[2] = (int)( color1.getBlue() + ratio * ( color2.getBlue() - color1.getBlue() ) );
			color[3] = (int)( color1.getAlpha() + ratio * ( color2.getAlpha() - color1.getAlpha() ) );
		}

		return color;
	}
}
