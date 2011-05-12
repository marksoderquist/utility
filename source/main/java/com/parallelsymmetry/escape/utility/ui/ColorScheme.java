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
		return Colors.getShade( primary, factor );
	}

	public Color getSecondaryA( double factor ) {
		return Colors.getShade( secondaryA, factor );
	}

	public Color getSecondaryB( double factor ) {
		return Colors.getShade( secondaryB, factor );
	}

	public Color getComplement( double factor ) {
		return Colors.getShade( complement, factor );
	}

	public String toString() {
		return primary.toString();
	}

}
