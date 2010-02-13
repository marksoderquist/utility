package com.parallelsymmetry.swing;

import java.awt.Color;

import com.parallelsymmetry.util.TextUtil;

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

	public static Color makeOpaque( Color color ) {
		return new Color( color.getRGB() | 0xff000000 );
	}

	public static Color makeTransparent( Color color, float transparency ) {
		float[] components = color.getColorComponents( new float[4] );
		return new Color( components[0], components[1], components[2], transparency );
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
}
