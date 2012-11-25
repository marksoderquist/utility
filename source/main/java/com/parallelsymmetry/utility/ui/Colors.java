package com.parallelsymmetry.utility.ui;

import java.awt.Color;

import com.parallelsymmetry.utility.TextUtil;

public final class Colors {

	public static final Color CLEAR = new Color( 0, 0, 0, 0 );

	public static final String encode( Color color ) {
		if( color == null ) return null;
		String string = Integer.toHexString( color.getRGB() );
		string = TextUtil.rightJustify( string, 8, '0' );
		return new StringBuilder( "#" ).append( string ).toString();
	}

	public static final Color decode( String data ) {
		if( TextUtil.isEmpty( data ) ) return null;
		int alpha = 255;
		if( data.startsWith( "#" ) ) data = data.substring( data.lastIndexOf( '#' ) + 1 );
		if( data.length() == 8 ) {
			alpha = Integer.parseInt( data.substring( 0, 2 ), 16 );
			data = data.substring( 2 );
		}
		int color = Integer.decode( "#" + data );
		color += alpha << 24;
		return new Color( color, true );
	}

	public static Color mix( Color color, Color mixer, double factor ) {
		if( color == null || mixer == null ) return null;

		int colorR = color.getRed();
		int colorG = color.getGreen();
		int colorB = color.getBlue();
		int colorA = color.getAlpha();

		int mixerR = mixer.getRed();
		int mixerG = mixer.getGreen();
		int mixerB = mixer.getBlue();
		int mixerA = mixer.getAlpha();

		int diffR = mixerR - colorR;
		int diffG = mixerG - colorG;
		int diffB = mixerB - colorB;
		int diffA = mixerA - colorA;

		int r = (int)( colorR + ( diffR * factor ) );
		int g = (int)( colorG + ( diffG * factor ) );
		int b = (int)( colorB + ( diffB * factor ) );
		int a = (int)( colorA + ( diffA * factor ) );

		return new Color( r, g, b, a );
	}

	public static float getIntensity( Color color ) {
		float[] rgb = color.getRGBColorComponents( null );
		return ( ( ( rgb[0] + rgb[1] + rgb[2] ) / 3f ) - 0.5f ) * 2;
	}

	/**
	 * Return a shade of the base color. Factor ranges from -1 to 1 with negative
	 * values trending toward black and positive values trending toward white.
	 * 
	 * @param color The base color.
	 * @param factor The shading factor.
	 * @return
	 */
	public static Color getShade( Color color, double factor ) {
		if( factor == 0 ) return color;
		if( factor < -1 ) factor = -1;
		if( factor > 1 ) factor = 1;
		return factor < 0 ? Colors.mix( color, Color.BLACK, -factor ) : Colors.mix( color, Color.WHITE, factor );
	}

	public static Color makeOpaque( Color color ) {
		return new Color( color.getRGB() | 0xff000000 );
	}

	public static Color makeTransparent( Color color, double transparency ) {
		float[] components = color.getColorComponents( new float[4] );
		return new Color( components[0], components[1], components[2], (float)transparency );
	}

	public static Color getHue( Color color ) {
		float[] components = Color.RGBtoHSB( color.getRed(), color.getGreen(), color.getBlue(), null );
		return new Color( Color.HSBtoRGB( components[0], 1, 1 ) );
	}

	/**
	 * @param color The reference color.
	 * @param angle The color wheel angle in degrees.
	 * @return
	 */
	public static Color getSecondary( Color color, double angle ) {
		float[] components = Color.RGBtoHSB( color.getRed(), color.getGreen(), color.getBlue(), null );

		double factor = ( angle / 360 );
		double h = ( components[0] + factor ) % 1.0;

		return Color.getHSBColor( (float)h, components[1], components[2] );
	}

	public static Color getComplement( Color color ) {
		float[] components = Color.RGBtoHSB( color.getRed(), color.getGreen(), color.getBlue(), null );

		float h = components[0];

		return Color.getHSBColor( h > 0.5 ? h - 0.5f : h + 0.5f, components[1], components[2] );
	}

}
