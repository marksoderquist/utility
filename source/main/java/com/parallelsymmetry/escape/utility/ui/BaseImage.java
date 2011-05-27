package com.parallelsymmetry.escape.utility.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;

import com.parallelsymmetry.escape.utility.TextUtil;
import com.parallelsymmetry.escape.utility.log.Log;

public abstract class BaseImage {

	protected enum ColorMode {
		PRIMARY, SECONDARYA, SECONDARYB, COMPLEMENT
	}

	protected enum GradientType {
		DARK( 0.5, -0.5 ), MEDIUM( 0.75, -0.25 ), LIGHT( 1, 0 );

		private double begin;

		private double end;

		private GradientType( double begin, double end ) {
			this.begin = begin;
			this.end = end;
		}

		public double getBeginFactor() {
			return begin;
		}

		public double getEndFactor() {
			return end;
		}

	}

	protected static final float DEFAULT_OUTLINE_SIZE = 1f / 32f;

	protected static final double RADIANS_PER_DEGREE = Math.PI / 180;

	protected static final double DEGREES_PER_RADIAN = 180 / Math.PI;

	private static final ColorScheme DEFAULT_COLOR_SCHEME = new ColorScheme( Color.GRAY );

	protected static final Font DEFAULT_FONT = new Font( Font.SANS_SERIF, Font.PLAIN, 24 );

	protected static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext( new AffineTransform(), true, false );

	protected int size;

	protected int width;

	protected int height;

	protected ColorScheme scheme = DEFAULT_COLOR_SCHEME;

	private double outlineSize = DEFAULT_OUTLINE_SIZE;

	private ColorMode colorMode = ColorMode.PRIMARY;

	private List<Instruction> instructions;

	private AffineTransform originalTransform;

	private AffineTransform baseTransform;

	public BaseImage( int width, int height ) {
		this.size = Math.min( width, height );
		this.width = width;
		this.height = height;
		instructions = new CopyOnWriteArrayList<Instruction>();
	}

	public abstract void render();

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ColorScheme getColorScheme() {
		return scheme;
	}

	public void setColorScheme( ColorScheme scheme ) {
		this.scheme = scheme != null ? scheme : DEFAULT_COLOR_SCHEME;
	}

	public double getOutlineSize() {
		return outlineSize;
	}

	public void setOutlineSize( double size ) {
		this.outlineSize = size;
	}

	public Paint getGradientPaint( Color color ) {
		return getGradientPaint( color, new Point( 0, 0 ) );
	}

	public Paint getGradientPaint( Color color, Point anchor ) {
		return new GradientPaint( anchor, Colors.getShade( color, 0.75 ), new Point2D.Double( anchor.getX() + 1, anchor.getY() + 1 ), Colors.getShade( color, -0.25 ) );
	}

	public Paint getGradientPaint( GradientType type ) {
		return getGradientPaint( type, new Point( 0, 0 ) );
	}

	public Paint getGradientPaint( GradientType type, Point anchor ) {
		return new GradientPaint( anchor, getColor( type.getBeginFactor() ), new Point2D.Double( anchor.getX() + 1, anchor.getY() + 1 ), getColor( type.getEndFactor() ) );
	}

	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

		Graphics2D graphics = image.createGraphics();
		render( graphics, 0, 0 );
		graphics.dispose();

		return image;
	}

	public void save( File target, String name ) {
		save( target, name, width, height );
	}
	
	public void save( File target, String name, ImageFilter filter ) {
		save( target, name, width, height, filter );
	}

	public void save( File target, String name, int size ) {
		save( target, name, size, size, null );
	}

	public void save( File target, String name, int width, int height ) {
		save( target, name, width, height, null );
	}

	public void save( File target, String name, int width, int height, ImageFilter filter ) {
		if( target.exists() && target.isFile() ) {
			System.err.println( "Target not a folder: " + target );
			return;
		}
		if( !target.exists() && !target.mkdirs() ) {
			System.err.println( "Could not create target: " + target );
			return;
		}

		// Generate the image.
		BufferedImage image = getImage();

		// Scale the icon.
		if( this.width != width || this.height != height ) image = Images.scale( image, width, height );

		// Filter the icon.
		image = Images.filter( image, filter );

		try {
			File file = new File( target, name + ".png" );
			ImageIO.write( image, "png", file );
			System.out.println( "Image created: " + file.toString() );
		} catch( IOException exception ) {
			exception.printStackTrace();
		}
	}

	protected final void render( Graphics2D graphics, int x, int y ) {
		originalTransform = graphics.getTransform();

		graphics.scale( size, size );
		baseTransform = graphics.getTransform();

		graphics.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		graphics.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

		render();

		for( Instruction instruction : instructions ) {
			instruction.paint( graphics, x, y );
		}

		graphics.setTransform( originalTransform );
	}

	protected void move( double x, double y ) {
		instructions.add( new MoveInstruction( x, y ) );
	}

	protected void spin( double angle ) {
		spin( 0, 0, angle );
	}

	protected void spin( double x, double y, double angle ) {
		instructions.add( new SpinInstruction( x, y, -angle * ( RADIANS_PER_DEGREE ) ) );
	}

	protected void reset() {
		instructions.add( new ResetInstruction() );
		clip();
	}

	protected void clip() {
		clip( null );
	}

	protected void clip( Shape clip ) {
		instructions.add( new ClipInstruction( clip ) );
	}

	protected void opacity( double opacity ) {
		instructions.add( new CompositeInstruction( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, (float)opacity ) ) );
	}

	protected void draw( Image image ) {
		instructions.add( new ImageInstruction( image ) );
	}

	protected void draw( Shape shape ) {
		draw( shape, (Paint)null, null );
	}

	protected void draw( Shape shape, Paint paint ) {
		draw( shape, paint, null );
	}

	protected void draw( Shape shape, Stroke stroke ) {
		draw( shape, (Paint)null, stroke );
	}

	protected void draw( Shape shape, Paint paint, Stroke stroke ) {
		if( paint == null ) paint = getColor( -0.75 );
		if( stroke == null ) stroke = new BasicStroke( (float)outlineSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
		instructions.add( new DrawInstruction( shape, paint, stroke ) );
	}

	protected void draw( Text text ) {
		draw( text, (Paint)null, null );
	}

	protected void draw( Text text, Paint paint ) {
		draw( text, paint, null );
	}

	protected void draw( Text text, Stroke stroke ) {
		draw( text, (Paint)null, stroke );
	}

	protected void draw( Text text, Paint paint, Stroke stroke ) {
		if( paint == null ) paint = getColor( -0.75 );
		if( stroke == null ) stroke = new BasicStroke( (float)outlineSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
		instructions.add( new TextInstruction( text, paint, stroke ) );
	}

	protected void fill( Shape shape ) {
		fill( shape, getGradientPaint( GradientType.LIGHT ) );
	}

	protected void fill( Shape shape, GradientType type ) {
		fill( shape, getGradientPaint( type, new Point( shape.getBounds2D().getMinX(), shape.getBounds().getMinY() ) ) );
	}

	protected void fill( Shape shape, Paint paint ) {
		if( paint == null ) paint = getGradientPaint( GradientType.LIGHT );
		instructions.add( new FillInstruction( shape, paint ) );
	}

	protected ColorMode getColorMode() {
		return colorMode;
	}

	protected void setColorMode( ColorMode mode ) {
		this.colorMode = mode;
	}

	protected Path getDot( double x, double y ) {
		double offset = 0.5 * DEFAULT_OUTLINE_SIZE;
		Path path = new Path();
		path.append( new Ellipse( x - offset, y - offset, offset * 2, offset * 2 ), false );
		return path;
	}

	protected Ellipse getCenteredCircle( double cx, double cy, double r ) {
		return getCenteredEllipse( cx, cy, r, r );
	}

	protected Ellipse getCenteredEllipse( double cx, double cy, double r ) {
		return getCenteredEllipse( cx, cy, r, r );
	}

	protected Ellipse getCenteredEllipse( double cx, double cy, double rx, double ry ) {
		double x = cx - rx;
		double y = cy - ry;
		double w = rx * 2;
		double h = ry * 2;

		return new Ellipse( x, y, w, h );
	}

	protected Arc getCenteredArc( double cx, double cy, double radius, double start, double end, int type ) {
		return getCenteredArc( cx, cy, radius, radius, start, end, type );
	}

	protected Arc getCenteredArc( double cx, double cy, double rx, double ry, double start, double end, int type ) {
		double x = cx - rx;
		double y = cy - ry;
		double w = rx * 2;
		double h = ry * 2;

		double extents = end - start;
		if( extents < 0 ) extents += 360;

		return new Arc( x, y, w, h, start, extents, type );
	}

	protected Arc getDirectedArc( double cx, double cy, double radius, double start, double extents, int type ) {
		return getDirectedArc( cx, cy, radius, radius, start, extents, type );
	}

	protected Arc getDirectedArc( double cx, double cy, double rx, double ry, double start, double extents, int type ) {
		double x = cx - rx;
		double y = cy - ry;
		double w = rx * 2;
		double h = ry * 2;

		return new Arc( x, y, w, h, start, extents, type );
	}

	protected Point getArcCenter( Point a, Point b, Point c ) {
		double alpha = 0;
		double beta = 0;
		double gamma = 0;

		alpha = getSquare( b.minus( c ).getMagnitude() ) * a.minus( b ).dot( a.minus( c ) ) / ( 2 * getSquare( a.minus( b ).cross( b.minus( c ) ).getMagnitude() ) );
		beta = getSquare( a.minus( c ).getMagnitude() ) * b.minus( a ).dot( b.minus( c ) ) / ( 2 * getSquare( a.minus( b ).cross( b.minus( c ) ).getMagnitude() ) );
		gamma = getSquare( a.minus( b ).getMagnitude() ) * c.minus( a ).dot( c.minus( b ) ) / ( 2 * getSquare( a.minus( b ).cross( b.minus( c ) ).getMagnitude() ) );

		return a.times( alpha ).plus( b.times( beta ) ).plus( c.times( gamma ) );
	}

	protected double getDistance( Point a, Point b ) {
		double dx = b.x - a.x;
		double dy = b.y - a.y;
		double dz = b.z - a.z;

		return Math.sqrt( dx * dx + dy * dy + dz * dz );
	}

	protected Font getFont( String path, Font defaultFont ) {
		InputStream input = getClass().getResourceAsStream( path );

		if( input == null ) {
			Log.write( Log.WARN, "Font not found: " + path );
		} else {
			try {
				return Font.createFont( Font.TRUETYPE_FONT, input );
			} catch( FontFormatException exception ) {
				Log.write( exception );
			} catch( IOException exception ) {
				Log.write( exception );
			}
		}

		return defaultFont;
	}

	/**
	 * Find the font that fits the specified width.
	 */
	protected Font findFontForWidth( Font font, String text, double width ) {
		Font currentFont = font.deriveFont( 1f );
		Rectangle2D textBounds = currentFont.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();

		double error = ( textBounds.getWidth() - width ) / width;
		double precision = textBounds.getWidth() / width;

		while( Math.abs( error ) > precision ) {
			double rescale = Math.pow( 2, -error );
			currentFont = currentFont.deriveFont( (float)( currentFont.getSize2D() * rescale ) );
			textBounds = currentFont.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();
			error = ( textBounds.getWidth() - width ) / width;
		}

		return currentFont;
	}

	/**
	 * Find the font that fits the specified height.
	 */
	protected Font findFontForHeight( Font font, String text, double height ) {
		Font currentFont = font.deriveFont( 1f );
		Rectangle2D textBounds = currentFont.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();

		double error = ( textBounds.getHeight() - height ) / height;
		double precision = textBounds.getHeight() / height;

		while( Math.abs( error ) > precision ) {
			double rescale = Math.pow( 2, -error );
			currentFont = currentFont.deriveFont( (float)( currentFont.getSize2D() * rescale ) );
			textBounds = currentFont.createGlyphVector( FONT_RENDER_CONTEXT, text ).getVisualBounds();
			error = ( textBounds.getHeight() - height ) / height;
		}

		return currentFont;
	}

	private final Color getColor( double factor ) {
		switch( colorMode ) {
			case SECONDARYA: {
				return scheme.getSecondaryA( factor );
			}
			case SECONDARYB: {
				return scheme.getSecondaryB( factor );
			}
			case COMPLEMENT: {
				return scheme.getComplement( factor );
			}
			default: {
				return scheme.getPrimary( factor );
			}
		}
	}

	private final double getSquare( double x ) {
		return x * x;
	}

	protected static class Path extends Path2D.Double {
		private static final long serialVersionUID = 6613052840894402785L;

		public Path() {
			super();
		}

		public Path( int rule ) {
			super( rule );
		}

		public Path( Shape shape ) {
			super( shape );
		}

		public void close() {
			closePath();
		}

	}

	protected static class Line extends Line2D.Double {
		private static final long serialVersionUID = -5236390976428072819L;

		public Line() {
			super();
		}

		public Line( Point p1, Point p2 ) {
			this( p1.x, p1.y, p2.x, p2.y );
		}

		public Line( double x1, double y1, double x2, double y2 ) {
			super( x1, y1, x2, y2 );
		}
	}

	protected static class Arc extends Arc2D.Double {

		private static final long serialVersionUID = 3888808873160174079L;

		public Arc() {
			super();
		}

		public Arc( int type ) {
			super( type );
		}

		public Arc( double x, double y, double w, double h, double start, double extent, int type ) {
			super( x, y, w, h, start, extent, type );
		}

	}

	protected static class Ellipse extends Ellipse2D.Double {
		private static final long serialVersionUID = 5599524256473627971L;

		public Ellipse() {
			super();
		}

		public Ellipse( double x, double y, double w, double h ) {
			super( x, y, w, h );
		}
	}

	protected static class Rectangle extends Rectangle2D.Double {

		private static final long serialVersionUID = -3800016368466471888L;

		public Rectangle() {
			super();
		}

		public Rectangle( double x, double y, double w, double h ) {
			super( x, y, w, h );
		}

	}

	protected static class RoundRectangle extends RoundRectangle2D.Double {

		private static final long serialVersionUID = 3294150270350204282L;

		public RoundRectangle() {
			super();
		}

		public RoundRectangle( double x, double y, double w, double h, double arcw, double arch ) {
			super( x, y, w, h, arcw, arch );
		}

	}

	protected static class Text {

		private String text;

		private double x;

		private double y;

		private Font font;

		public Text( String text, double x, double y ) {
			this( text, x, y, null );
		}

		public Text( String text, double x, double y, Font font ) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.font = font;
		}

		public String getText() {
			return text;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

		public Font getFont() {
			return font;
		}

	}

	protected static class Image {

		private java.awt.Image image;

		private double x;

		private double y;

		public Image( java.awt.Image text, double x, double y ) {
			this.image = text;
			this.x = x;
			this.y = y;
		}

		public java.awt.Image getImage() {
			return image;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}

	}

	protected static class Point extends Point2D.Double {

		private static final long serialVersionUID = -1520877460686311009L;

		private double x;

		private double y;

		private double z;

		public Point( double x, double y ) {
			this( x, y, 0 );
		}

		private Point( double x, double y, double z ) {
			super( x, y );
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public final double getZ() {
			return z;
		}

		public final double getMagnitude() {
			return Math.sqrt( x * x + y * y + z * z );
		}

		public final double dot( Point vector ) {
			return x * vector.x + y * vector.y + z * vector.z;
		}

		public final Point cross( Point vector ) {
			return new Point( y * vector.z - z * vector.y, z * vector.x - x * vector.z, x * vector.y - y * vector.x );
		}

		public final Point plus( Point vector ) {
			return new Point( x + vector.x, y + vector.y, z + vector.z );
		}

		public final Point minus( Point vector ) {
			return new Point( x - vector.x, y - vector.y, z - vector.z );
		}

		public final Point times( double scale ) {
			return new Point( x * scale, y * scale, z * scale );
		}

	}

	private interface Instruction {
		void paint( Graphics2D graphics, int x, int y );
	}

	private class ResetInstruction implements Instruction {

		@Override
		public void paint( Graphics2D graphics, int x, int y ) {
			graphics.setTransform( baseTransform );
		}

	}

	private class MoveInstruction implements Instruction {

		private double x;

		private double y;

		public MoveInstruction( double x, double y ) {
			this.x = x;
			this.y = y;
		}

		@Override
		public void paint( Graphics2D graphics, int x, int y ) {
			graphics.translate( this.x, this.y );
		}
	}

	private class SpinInstruction implements Instruction {

		private double x;

		private double y;

		private double theta;

		public SpinInstruction( double x, double y, double theta ) {
			this.x = x;
			this.y = y;
			this.theta = theta;
		}

		@Override
		public void paint( Graphics2D graphics, int x, int y ) {
			graphics.rotate( theta, this.x, this.y );
		}

	}

	private class ClipInstruction implements Instruction {

		private Shape clip;

		public ClipInstruction( Shape clip ) {
			this.clip = clip;
		}

		@Override
		public void paint( Graphics2D graphics, int x, int y ) {
			graphics.setClip( clip );
		}

	}

	private class CompositeInstruction implements Instruction {

		private Composite composite;

		public CompositeInstruction( Composite composite ) {
			this.composite = composite;
		}

		@Override
		public void paint( Graphics2D graphics, int x, int y ) {
			graphics.setComposite( composite );
		}

	}

	private class FillInstruction implements Instruction {

		private Shape shape;

		private Paint paint;

		public FillInstruction( Shape shape, Paint paint ) {
			this.shape = shape;
			this.paint = paint;
		}

		public void paint( Graphics2D graphics, int x, int y ) {
			graphics.setPaint( paint );
			graphics.translate( x, y );
			graphics.fill( shape );
			graphics.translate( -x, -y );
		}

	}

	private class DrawInstruction implements Instruction {

		private Shape shape;

		private Paint paint;

		private Stroke stroke;

		public DrawInstruction( Shape shape, Paint paint, Stroke stroke ) {
			this.shape = shape;
			this.paint = paint;
			this.stroke = stroke;
		}

		public void paint( Graphics2D graphics, int x, int y ) {
			graphics.setPaint( paint );
			graphics.setStroke( stroke );
			graphics.translate( x, y );
			graphics.draw( shape );
			graphics.translate( -x, -y );
		}

	}

	private class TextInstruction implements Instruction {

		private Text text;

		private Paint paint;

		private Stroke stroke;

		public TextInstruction( Text text, Paint paint, Stroke stroke ) {
			this.text = text;
			this.paint = paint;
			this.stroke = stroke;
		}

		public void paint( Graphics2D graphics, int x, int y ) {
			if( TextUtil.isEmpty( text.getText() ) ) return;
			graphics.setPaint( paint );
			graphics.setStroke( stroke );
			graphics.setFont( text.getFont().deriveFont( text.getFont().getSize2D() / size ) );
			graphics.translate( x, y );
			graphics.drawString( text.getText(), (float)text.getX(), (float)text.getY() );
			graphics.translate( -x, -y );
		}

	}

	private class ImageInstruction implements Instruction {

		private Image text;

		public ImageInstruction( Image text ) {
			this.text = text;
		}

		public void paint( Graphics2D graphics, int x, int y ) {
			AffineTransform transform = graphics.getTransform();
			graphics.translate( x, y );
			graphics.setTransform( originalTransform );
			graphics.drawImage( text.getImage(), (int)( text.getX() * transform.getScaleX() ), (int)( text.getY() * transform.getScaleY() ), null );
			graphics.setTransform( transform );

			graphics.translate( -x, -y );
		}

	}

}
