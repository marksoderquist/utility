package com.parallelsymmetry.utility.ui;

import java.awt.Color;

public class ColorScheme implements Cloneable {

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

	public ColorScheme( Color primary, Color secondaryA, Color secondaryB, Color complement ) {
		this.primary = primary;
		this.secondaryA = secondaryA;
		this.secondaryB = secondaryB;
		this.complement = complement;
	}

	public Color getPrimary( double factor ) {
		return Colors.getShade( primary, factor );
	}

	public void setPrimary( Color color ) {
		this.primary = color;
	}

	public Color getSecondaryA( double factor ) {
		return Colors.getShade( secondaryA, factor );
	}

	public void setSecondaryA( Color color ) {
		this.secondaryA = color;
	}

	public Color getSecondaryB( double factor ) {
		return Colors.getShade( secondaryB, factor );
	}

	public void setSecondaryB( Color color ) {
		this.secondaryB = color;
	}

	public Color getComplement( double factor ) {
		return Colors.getShade( complement, factor );
	}

	public void setComplement( Color color ) {
		this.complement = color;
	}

	@Override
	public ColorScheme clone() {
		ColorScheme scheme = new ColorScheme();

		scheme.primary = this.primary;
		scheme.secondaryA = this.secondaryA;
		scheme.secondaryB = this.secondaryB;
		scheme.complement = this.complement;

		return scheme;
	}

	@Override
	public String toString() {
		return primary.toString();
	}

}
