package com.parallelsymmetry.escape.utility.ui;

import java.awt.Color;

public class ColorScheme {

	public static final double DEFAULT_SECONDARY_ANGLE = 30;

	private Color primary;

	private Color secondaryA;

	private Color secondaryB;

	private Color complement;

	public ColorScheme() {
		this( Color.BLACK );
	}

	public ColorScheme( Color color ) {
		this( color, DEFAULT_SECONDARY_ANGLE, false );
	}

	public ColorScheme( Color color, boolean switchSecondary ) {
		this( color, DEFAULT_SECONDARY_ANGLE, switchSecondary );
	}

	public ColorScheme( Color color, double secondaryAngle ) {
		this( color, secondaryAngle, false );
	}

	public ColorScheme( Color color, double secondaryAngle, boolean switchSecondary ) {
		secondaryAngle = secondaryAngle % 360;
		this.primary = color;
		this.secondaryA = Colors.getSecondary( color, switchSecondary ? secondaryAngle : -secondaryAngle );
		this.secondaryB = Colors.getSecondary( color, switchSecondary ? -secondaryAngle : secondaryAngle );
		this.complement = Colors.getComplement( color );
	}

	public Color getPrimary( double factor ) {
		return getColor( primary, factor );
	}

	public Color getSecondaryA( double factor ) {
		return getColor( secondaryA, factor );
	}

	public Color getSecondaryB( double factor ) {
		return getColor( secondaryB, factor );
	}

	public Color getComplement( double factor ) {
		return getColor( complement, factor );
	}

	private Color getColor( Color color, double factor ) {
		if( factor == 0 ) return color;
		if( factor < -1 ) factor = -1;
		if( factor > 1 ) factor = 1;
		return factor < 0 ? Colors.mix( color, Color.BLACK, -factor ) : Colors.mix( color, Color.WHITE, factor );
	}

	public String toString() {
		return primary.toString();
	}

}
