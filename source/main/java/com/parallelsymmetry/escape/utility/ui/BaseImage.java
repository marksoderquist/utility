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
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.parallelsymmetry.escape.utility.TextUtil;
import com.parallelsymmetry.escape.utility.log.Log;

public abstract class BaseImage {

	protected enum ColorType {

		PRIMARY( 0 ), SECONDARYA( 0 ), SECONDARYB( 0 ), COMPLEMENT( 0 );

		ColorType( double factor ) {

		}

		double getFactor() {
			return 0;
		}

	}

	protected enum GradientType {
		DARK( -0.75, -0.25 ), MEDIUM( -0.5, 0.5 ), LIGHT( 0.25, 0.75 );

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

	protected static final float DEFAULT_PEN_WIDTH = 1f / 32f;

	protected int size;

	protected int width;

	protected int height;

	protected ColorScheme scheme;

	protected Graphics2D graphics;

	private List<Instruction> instructions;

	public BaseImage( int width, int height ) {
		this.size = Math.min( width, height );
		this.width = width;
		this.height = height;
		this.scheme = new ColorScheme( Color.BLACK );
		instructions = new CopyOnWriteArrayList<Instruction>();
	}

	public abstract void render();

	public ColorScheme getColorScheme() {
		return scheme;
	}

	public Paint getGradientPaint( GradientType type ) {
		return getGradientPaint( type, new Point( 0, 0 ) );
	}

	public Paint getGradientPaint( GradientType type, Point anchor ) {
		return new GradientPaint( anchor, scheme.getPrimary( type.getBeginFactor() ), new Point2D.Double( anchor.getX() + size, anchor.getY() + size ), scheme.getPrimary( type.getEndFactor() ) );
	}

	protected final void render( Graphics2D graphics, int x, int y ) {
		graphics.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		graphics.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		graphics.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );

		render();
		for( Instruction instruction : instructions ) {
			instruction.paint( graphics, x, y );
		}
	}

	@Deprecated
	private void setBaseColor( Color color ) {
		//		colors = new ConcurrentHashMap<String, Color>();
		//		colors.put( OUTLINE_DARK, Colors.mix( color, Color.WHITE, 0.25 ) );
		//		colors.put( OUTLINE, color );
		//		colors.put( OUTLINE_LIGHT, Colors.mix( color, Color.WHITE, 0.75 ) );
		//
		//		colors.put( GRADIENT_LIGHT_BEGIN, Colors.mix( color, Color.WHITE, 1 ) );
		//		colors.put( GRADIENT_LIGHT_END, Colors.mix( color, Color.WHITE, 0.5 ) );
		//
		//		colors.put( GRADIENT_MEDIUM_BEGIN, Colors.mix( color, Color.WHITE, 0.875 ) );
		//		colors.put( GRADIENT_MEDIUM_END, Colors.mix( color, Color.WHITE, 0.375 ) );
		//
		//		colors.put( GRADIENT_DARK_BEGIN, Colors.mix( color, Color.WHITE, 0.75 ) );
		//		colors.put( GRADIENT_DARK_END, Colors.mix( color, Color.WHITE, 0.25 ) );
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
		draw( shape, null, null );
	}

	protected void draw( Shape shape, Paint paint ) {
		draw( shape, paint, null );
	}

	protected void draw( Shape shape, Stroke stroke ) {
		draw( shape, null, stroke );
	}

	protected void draw( Shape shape, Paint paint, Stroke stroke ) {
		if( paint == null ) paint = scheme.getPrimary( 0 );
		if( stroke == null ) stroke = new BasicStroke( DEFAULT_PEN_WIDTH * size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
		instructions.add( new DrawInstruction( shape, paint, stroke ) );
	}

	protected void draw( Text text ) {
		draw( text, null, null );
	}

	protected void draw( Text text, Paint paint ) {
		draw( text, paint, null );
	}

	protected void draw( Text text, Stroke stroke ) {
		draw( text, null, stroke );
	}

	protected void draw( Text text, Paint paint, Stroke stroke ) {
		if( paint == null ) paint = scheme.getPrimary( 0 );
		if( stroke == null ) stroke = new BasicStroke( DEFAULT_PEN_WIDTH * size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
		instructions.add( new TextInstruction( text, paint, stroke ) );
	}

	protected void fill( Shape shape ) {
		fill( shape, getGradientPaint( GradientType.LIGHT ) );
	}

	protected void fill( Shape shape, GradientType type ) {
		fill( shape, getGradientPaint( type, new Point( shape.getBounds2D().getMinX(), shape.getBounds().getMinY() ) ) );
	}

	protected void fill( Shape shape, Paint paint ) {
		instructions.add( new FillInstruction( shape, paint ) );
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
			graphics.setFont( text.getFont() );
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
			graphics.translate( x, y );
			graphics.drawImage( text.getImage(), (int)text.getX(), (int)text.getY(), null );
			graphics.translate( -x, -y );
		}

	}

}
