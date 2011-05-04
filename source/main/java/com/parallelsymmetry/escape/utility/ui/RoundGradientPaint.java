package com.parallelsymmetry.escape.utility.ui;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.util.Arrays;

public class RoundGradientPaint implements Paint {

	protected Point2D center;

	protected double angle;

	protected double extent;

	protected double[] stops;

	protected Color[] colors;

	public RoundGradientPaint( double x, double y, double angle, Color color1, Color color2 ) {
		this( new Point2D.Double( x, y ), angle, color1, color2 );
	}

	public RoundGradientPaint( Point2D center, double angle, Color color1, Color color2 ) {
		this( center, angle, new double[] { 0, 1 }, new Color[] { color1, color2 } );
	}

	public RoundGradientPaint( double x, double y, double angle, double[] stops, Color[] colors ) {
		this( new Point2D.Double( x, y ), angle, stops, colors );
	}

	public RoundGradientPaint( Point2D center, double angle, double[] stops, Color[] colors ) {
		this( center, angle, Math.PI * 2, stops, colors );
	}

	public RoundGradientPaint( double x, double y, double angle, double extent, double[] stops, Color[] colors ) {
		this( new Point2D.Double( x, y ), angle, extent, stops, colors );
	}

	public RoundGradientPaint( Point2D center, double angle, double extent, double[] stops, Color[] colors ) {
		this.center = center;
		this.angle = angle;
		this.extent = extent;
		this.stops = Arrays.copyOf( stops, stops.length );
		this.colors = Arrays.copyOf( colors, colors.length );
	}

	public PaintContext createContext( ColorModel colorModel, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform transform, RenderingHints hints ) {
		Point2D center = transform.transform( this.center, null );
		return new RoundGradientContext( center, angle, extent, stops, colors );
	}

	public int getTransparency() {
		for( Color color : colors ) {
			if( color.getAlpha() == 0xff ) continue;
			return TRANSLUCENT;
		}
		return OPAQUE;
	}

}
