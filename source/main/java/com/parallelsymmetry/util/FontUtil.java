package com.parallelsymmetry.util;

import java.awt.Font;

public class FontUtil {

	private static final String SEPARATOR = "-";

	public static final String encodeNameAndStyle( Font font ) {
		StringBuilder builder = new StringBuilder( font.getFamily() );
		builder.append( SEPARATOR );
		builder.append( encodeStyle( font ) );
		return builder.toString();
	}

	public static final String encodeStyle( Font font ) {
		switch( font.getStyle() ) {
			case Font.ITALIC: {
				return "ITALIC";
			}
			case Font.BOLD: {
				return "BOLD";
			}
			case Font.BOLD + Font.ITALIC: {
				return "BOLDITALIC";
			}
		}
		return "PLAIN";
	}

}
