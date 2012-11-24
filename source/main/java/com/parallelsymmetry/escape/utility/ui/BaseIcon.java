package com.parallelsymmetry.escape.utility.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.ImageFilter;
import java.io.File;

import javax.swing.Icon;

/**
 * Base icon implementation intended to provided convenience for generating high
 * quality icons.
 * <p>
 * The constants are based on a 16x16 grid so that it is easy to generate icons
 * that can be accurately scaled down to 16x16 pixels. There are two commonly
 * uses sets of constants, the grid constants and the cell constants. The grid
 * constants fall on the grid lines while the cell constants fall in the center
 * of a 16x16 cell.
 * <p>
 * The following graph shows the locations of each constant. While the grid
 * constants are only shown on the Y-axis and the cell constants only on the
 * X-axis they are valid for both axes.
 * 
 * <pre>
 *      Z   Z   Z   Z   Z   Z   Z   Z   Z   Z   Z   Z   Z   Z   Z   Z
 *    A A J B F C K D D E L F G G M H C I N J H K O L E M P N I O Q P B
 *  A +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZA |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  J +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZB |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  F +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZC |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  K +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZD |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  D +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZE |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  L +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZF |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  G +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZG |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  M +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZH |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  C +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZI |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  N +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZJ |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  H +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZK |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  O +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZL |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  E +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZM |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  P +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZN |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  I +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZO |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  Q +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * ZP |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
 *  B +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
 * </pre>
 * 
 * @author SoderquistMV
 */
public abstract class BaseIcon extends BaseImage implements Icon {

	public static final int DEFAULT_ICON_SIZE = 256;

	public static final double A = 0;

	public static final double B = 1;

	public static final double C = 0.5;

	public static final double D = 0.25;

	public static final double E = 0.75;

	public static final double F = 0.125;

	public static final double G = 0.375;

	public static final double H = 0.625;

	public static final double I = 0.875;

	public static final double J = 0.0625;

	public static final double K = 0.1875;

	public static final double L = 0.3125;

	public static final double M = 0.4375;

	public static final double N = 0.5625;

	public static final double O = 0.6875;

	public static final double P = 0.8125;

	public static final double Q = 0.9375;

	public static final double ZA = 0.03125;

	public static final double ZB = 0.09375;

	public static final double ZC = 0.15625;

	public static final double ZD = 0.21875;

	public static final double ZE = 0.28125;

	public static final double ZF = 0.34375;

	public static final double ZG = 0.40625;

	public static final double ZH = 0.46875;

	public static final double ZI = 0.53125;

	public static final double ZJ = 0.59375;

	public static final double ZK = 0.65625;

	public static final double ZL = 0.71875;

	public static final double ZM = 0.78125;

	public static final double ZN = 0.84375;

	public static final double ZO = 0.90625;

	public static final double ZP = 0.96875;

	public BaseIcon() {
		this( DEFAULT_ICON_SIZE );
	}

	public BaseIcon( int size ) {
		super( size, size );
	}

	@Override
	public void paintIcon( Component c, Graphics g, int x, int y ) {
		render( (Graphics2D)g, x, y );
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}

	@Override
	public void save( File target, String name ) {
		save( target, name, width, height );
	}

	@Override
	public void save( File target, String name, ImageFilter filter ) {
		save( target, name, width, height, filter );
	}

	@Override
	public void save( File target, String name, int size ) {
		save( target, name, size, size, null );
	}

	@Override
	public void save( File target, String name, int width, int height ) {
		save( target, name, width, height, null );
	}

	@Override
	public void save( File target, String name, int width, int height, ImageFilter filter ) {
		Icons.save( this, target, name, width, height, filter );
	}

}
