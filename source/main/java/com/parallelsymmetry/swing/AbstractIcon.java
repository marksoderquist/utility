package com.parallelsymmetry.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.parallelsymmetry.util.TextUtil;

public abstract class AbstractIcon implements Icon {

	public static final int DEFAULT_PEN_WIDTH = 8;

	public static final int DARK_GRADIENT_PAINT = 1;

	public static final int MEDIUM_GRADIENT_PAINT = 2;

	public static final int LIGHT_GRADIENT_PAINT = 3;

	public static final double DEGREES_PER_RADIAN = 180 / Math.PI;

	public static final double RADIANS_PER_DEGREE = Math.PI / 180;

	protected static final String OUTLINE_DARK = "outline.dark";

	protected static final String OUTLINE = "outline";

	protected static final String OUTLINE_LIGHT = "outline.light";

	protected static final String GRADIENT_DARK_END = "gradient.dark.end";

	protected static final String GRADIENT_DARK_BEGIN = "gradient.dark.begin";

	protected static final String GRADIENT_MEDIUM_END = "gradient.medium.end";

	protected static final String GRADIENT_MEDIUM_BEGIN = "gradient.medium.begin";

	protected static final String GRADIENT_LIGHT_END = "gradient.light.end";

	protected static final String GRADIENT_LIGHT_BEGIN = "gradient.light.begin";

	protected static final Stroke HALF_STROKE = new BasicStroke( DEFAULT_PEN_WIDTH / 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

	protected static final Stroke DEFAULT_STROKE = new BasicStroke( DEFAULT_PEN_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

	protected static final Stroke DOUBLE_STROKE = new BasicStroke( DEFAULT_PEN_WIDTH * 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

	protected static final Map<String, Color> colors;

	protected final int grid = 256;

	protected int width;

	protected int height;

	private double movex;

	private double movey;

	private double angle;

	private List<Instruction> instructions;

	static {
		Color basic = Color.BLACK;

		colors = new ConcurrentHashMap<String, Color>();
		colors.put( OUTLINE_DARK, basic.darker() );
		colors.put( OUTLINE, basic );
		colors.put( OUTLINE_LIGHT, basic.brighter() );

		colors.put( GRADIENT_LIGHT_BEGIN, Colors.mix( basic, Color.WHITE, 1 ) );
		colors.put( GRADIENT_LIGHT_END, Colors.mix( basic, Color.WHITE, 0.5 ) );

		colors.put( GRADIENT_MEDIUM_BEGIN, Colors.mix( basic, Color.WHITE, 0.875 ) );
		colors.put( GRADIENT_MEDIUM_END, Colors.mix( basic, Color.WHITE, 0.375 ) );

		colors.put( GRADIENT_DARK_BEGIN, Colors.mix( basic, Color.WHITE, 0.75 ) );
		colors.put( GRADIENT_DARK_END, Colors.mix( basic, Color.WHITE, 0.25 ) );
	}

	public AbstractIcon() {
		instructions = new CopyOnWriteArrayList<Instruction>();
		width = grid;
		height = grid;
	}

	public int getIconHeight() {
		return width;
	}

	public int getIconWidth() {
		return height;
	}

	public void paintIcon( Component c, Graphics g, int x, int y ) {
		Graphics2D graphics = (Graphics2D)g;
		graphics.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		graphics.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		graphics.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
		render( graphics );
		reset();
		for( Instruction instruction : instructions ) {
			instruction.paint( graphics, x, y );
		}
	}

	public abstract void render( Graphics2D graphics );

	public static void showSample( Icon icon ) {
		showSample( icon, null );
	}

	public static void showSample( Icon icon, ImageFilter filter ) {
		JFrame frame = new JFrame( icon.getClass().getName() );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.add( new SamplePanel( icon, filter ) );
		frame.pack();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int)( ( screen.width - frame.getWidth() ) * 0.5 );
		int y = (int)( ( screen.height - frame.getHeight() ) * 0.25 );

		frame.setLocation( x, y );
		frame.setResizable( false );
		frame.setVisible( true );
	}

	protected void add( Graphics2D graphics, AbstractIcon icon ) {
		icon.render( graphics );
		instructions.addAll( icon.instructions );
	}

	protected void addAxes() {
		addAxes( new Color( 255, 0, 0, 64 ) );
	}

	protected void addAxes( Color color ) {
		double size = 256;
		draw( new Line( -size, 0, size, 0 ), color );
		draw( new Line( 0, -size, 0, size ), color );

		double arrowSize = size * 0.1;
		Path arrow = new Path();
		arrow.moveTo( 0, -size );
		arrow.lineTo( arrowSize / 2, -size + arrowSize );
		arrow.lineTo( -arrowSize / 2, -size + arrowSize );
		arrow.close();
		draw( arrow, color );
	}

	protected void debugScale( Graphics2D graphics, double scale ) {
		graphics.scale( scale, scale );
		graphics.translate( 128 / scale - 128, 128 / scale - 128 );
	}

	protected void reset() {
		spin();
		move();
		clip();
	}

	protected void home() {
		spin();
		move();
	}

	protected void move() {
		move( -movex, -movey );
	}

	protected void move( double x, double y ) {
		movex += x;
		movey += y;
		instructions.add( new MoveInstruction( x, y ) );
	}

	protected void spin() {
		instructions.add( new SpinInstruction( 0, 0, angle * ( RADIANS_PER_DEGREE ) ) );
		this.angle = 0;
	}

	protected void spin( double angle ) {
		spin( 0, 0, angle );
	}

	protected void spin( double x, double y, double angle ) {
		double phi = -angle * ( RADIANS_PER_DEGREE );
		double ax = x;
		double ay = y;

		double radius = Math.sqrt( ax * ax + ay * ay );
		double theta = Math.atan2( y, x ) + phi;

		movex += x - ( Math.cos( theta ) * radius );
		movey += y - ( Math.sin( theta ) * radius );

		this.angle += angle;
		instructions.add( new SpinInstruction( x, y, phi ) );
	}

	protected void clip() {
		clip( null );
	}

	protected void clip( Shape clip ) {
		instructions.add( new ClipInstruction( clip ) );
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
		instructions.add( new DrawInstruction( shape, paint, stroke ) );
	}

	protected void draw( Text text ) {
		instructions.add( new TextInstruction( text ) );
	}

	protected void draw( Text text, Color color ) {
		instructions.add( new TextInstruction( text, color ) );
	}

	protected void draw( Text text, Stroke stroke ) {
		instructions.add( new TextInstruction( text, stroke ) );
	}

	protected void draw( Text text, Color color, Stroke stroke ) {
		instructions.add( new TextInstruction( text, color, stroke ) );
	}

	protected void fill( Shape shape ) {
		fill( shape, LIGHT_GRADIENT_PAINT );
	}

	protected void fill( Shape shape, int type ) {
		Paint paint = null;
		Point2D point = new Point2D.Double( shape.getBounds2D().getMinX(), shape.getBounds().getMinY() );
		switch( type ) {
			case DARK_GRADIENT_PAINT:
				paint = darkPaint( point );
				break;
			case MEDIUM_GRADIENT_PAINT:
				paint = mediumPaint( point );
				break;
			case LIGHT_GRADIENT_PAINT:
				paint = lightPaint( point );
				break;
		}
		fill( shape, paint );
	}

	protected void fill( Shape shape, Paint paint ) {
		instructions.add( new FillInstruction( shape, paint ) );
	}

	protected Paint darkPaint( Point2D anchor ) {
		return new GradientPaint( anchor, colors.get( GRADIENT_DARK_BEGIN ), new Point2D.Double( anchor.getX() + grid, anchor.getY() + grid ), colors.get( GRADIENT_DARK_END ) );
	}

	protected Paint mediumPaint( Point2D anchor ) {
		return new GradientPaint( anchor, colors.get( GRADIENT_MEDIUM_BEGIN ), new Point2D.Double( anchor.getX() + grid, anchor.getY() + grid ), colors.get( GRADIENT_MEDIUM_END ) );
	}

	protected Paint lightPaint( Point2D anchor ) {
		return new GradientPaint( anchor, colors.get( GRADIENT_LIGHT_BEGIN ), new Point2D.Double( anchor.getX() + grid, anchor.getY() + grid ), colors.get( GRADIENT_LIGHT_END ) );
	}

	protected Paint coloredPaint( Shape shape, Color color ) {
		return coloredPaint( shape, colors.get( GRADIENT_LIGHT_BEGIN ), color );
	}

	protected Paint coloredPaint( Shape shape, Color color1, Color color2 ) {
		Point2D anchor = new Point2D.Double( shape.getBounds2D().getMinX(), shape.getBounds().getMinY() );
		Point2D anchor2 = new Point2D.Double( shape.getBounds2D().getMaxX(), shape.getBounds().getMaxY() );
		//return coloredPaint( anchor, color1, color2 );
		return new GradientPaint( anchor, color1, anchor2, color2 );
	}

	protected Paint coloredPaint( Point2D anchor, Color color ) {
		return coloredPaint( anchor, colors.get( GRADIENT_LIGHT_BEGIN ), color );
	}

	protected Paint coloredPaint( Point2D anchor, Color color1, Color color2 ) {
		return new GradientPaint( anchor, color1, new Point2D.Double( anchor.getX() + grid, anchor.getY() + grid ), color2 );
	}

	protected Path getDot( double x, double y ) {
		float offset = DEFAULT_PEN_WIDTH;
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

	/**
	 * Find the font that fits the specified width.
	 */
	protected Font findFontForWidth( Graphics g, Font font, String text, float width ) {
		Graphics2D graphics = (Graphics2D)g;

		Font currentFont = font.deriveFont( 1.0f );
		FontMetrics fontMetrics = graphics.getFontMetrics( currentFont );
		Rectangle2D textBounds = fontMetrics.getStringBounds( text, graphics );

		while( textBounds.getWidth() < width ) {
			currentFont = currentFont.deriveFont( currentFont.getSize2D() + 1 );
			fontMetrics = graphics.getFontMetrics( currentFont );
			textBounds = fontMetrics.getStringBounds( text, graphics );
		}

		return currentFont;
	}

	/**
	 * Find the font that fits the specified height.
	 */
	protected Font findFontForHeight( Graphics g, Font font, float height ) {
		Graphics2D graphics = (Graphics2D)g;

		Font currentFont = font.deriveFont( 1.0f );
		FontMetrics fontMetrics = graphics.getFontMetrics( currentFont );
		int fontHeight = fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent();

		while( fontHeight < height ) {
			currentFont = currentFont.deriveFont( currentFont.getSize2D() + 1 );
			fontMetrics = graphics.getFontMetrics( currentFont );
			fontHeight = fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent();
		}

		return currentFont;
	}

	protected void save( File target, String name ) {
		save( target, name, getIconWidth(), getIconHeight() );
	}

	protected void save( File target, String name, int size ) {
		save( target, name, size, size, null );
	}

	protected void save( File target, String name, int width, int height ) {
		save( target, name, width, height, null );
	}

	protected void save( File target, String name, int width, int height, RGBImageFilter filter ) {
		if( !target.isDirectory() ) target = target.getParentFile();
		if( !target.exists() && target.mkdirs() ) {
			System.err.println( "Could not create target: " + target );
			return;
		}

		BufferedImage image = new BufferedImage( getIconWidth(), getIconHeight(), BufferedImage.TYPE_INT_ARGB );
		paintIcon( null, image.getGraphics(), 0, 0 );

		// Scale the icon.
		if( getIconWidth() != width || getIconHeight() != height ) image = Images.scale( image, width, height );

		// Filter the icon.
		filter( image, filter );

		try {
			File file = new File( target, name + ".png" );
			ImageIO.write( image, "png", file );
			System.out.println( "Image created: " + file.toString() );
		} catch( IOException exception ) {
			exception.printStackTrace();
		}
	}

	private void filter( BufferedImage image, RGBImageFilter filter ) {
		if( filter == null ) return;

		int w = image.getWidth();
		int h = image.getHeight();
		for( int x = 0; x < w; x++ ) {
			for( int y = 0; y < h; y++ ) {
				image.setRGB( x, y, filter.filterRGB( x, y, image.getRGB( x, y ) ) );
			}
		}
	}

	private final double getSquare( double x ) {
		return x * x;
	}

	public static class Path extends Path2D.Double {
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

	public static class Line extends Line2D.Double {
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

	public static class Arc extends Arc2D.Double {

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

	public static class Ellipse extends Ellipse2D.Double {
		private static final long serialVersionUID = 5599524256473627971L;

		public Ellipse() {
			super();
		}

		public Ellipse( double x, double y, double w, double h ) {
			super( x, y, w, h );
		}
	}

	public static class Rectangle extends Rectangle2D.Double {

		private static final long serialVersionUID = -3800016368466471888L;

		public Rectangle() {
			super();
		}

		public Rectangle( double x, double y, double w, double h ) {
			super( x, y, w, h );
		}

	}

	public static class RoundRectangle extends RoundRectangle2D.Double {

		private static final long serialVersionUID = 3294150270350204282L;

		public RoundRectangle() {
			super();
		}

		public RoundRectangle( double x, double y, double w, double h, double arcw, double arch ) {
			super( x, y, w, h, arcw, arch );
		}

	}

	public static class Text {

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

	public static class Point {

		public final double x;

		public final double y;

		public final double z;

		public Point( double x, double y ) {
			this.x = x;
			this.y = y;
			this.z = 0;
		}

		private Point( double x, double y, double z ) {
			this.x = x;
			this.y = y;
			this.z = z;
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

	private static interface Instruction {
		void paint( Graphics2D graphics, int x, int y );
	}

	private static class MoveInstruction implements Instruction {

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

	private static class SpinInstruction implements Instruction {

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

	private static class ClipInstruction implements Instruction {

		private Shape clip;

		public ClipInstruction( Shape clip ) {
			this.clip = clip;
		}

		@Override
		public void paint( Graphics2D graphics, int x, int y ) {
			graphics.setClip( clip );
		}

	}

	private static class DrawInstruction implements Instruction {

		private Shape shape;

		private Paint paint;

		private Stroke stroke;

		public DrawInstruction( Shape shape, Paint paint, Stroke stroke ) {
			this.shape = shape;
			this.paint = paint;
			this.stroke = stroke;
		}

		public void paint( Graphics2D graphics, int x, int y ) {
			graphics.setPaint( paint == null ? colors.get( OUTLINE ) : paint );
			graphics.setStroke( stroke == null ? AbstractIcon.DEFAULT_STROKE : stroke );
			graphics.translate( x, y );
			graphics.draw( shape );
			graphics.translate( -x, -y );
		}

	}

	private static class FillInstruction implements Instruction {

		private Shape shape;

		private Paint paint;

		public FillInstruction( Shape shape, Paint paint ) {
			this.shape = shape;
			this.paint = paint;
		}

		public void paint( Graphics2D graphics, int x, int y ) {
			if( paint != null ) graphics.setPaint( paint );
			graphics.translate( x, y );
			graphics.fill( shape );
			graphics.translate( -x, -y );
		}

	}

	private static class TextInstruction implements Instruction {

		private Text text;

		private Color color;

		private Stroke stroke;

		public TextInstruction( Text text ) {
			this.text = text;
		}

		public TextInstruction( Text text, Color color ) {
			this.text = text;
			this.color = color;
		}

		public TextInstruction( Text text, Stroke stroke ) {
			this.text = text;
			this.stroke = stroke;
		}

		public TextInstruction( Text text, Color color, Stroke stroke ) {
			this.text = text;
			this.color = color;
			this.stroke = stroke;
		}

		public void paint( Graphics2D graphics, int x, int y ) {
			if( TextUtil.isEmpty( text.getText() ) ) return;
			graphics.setColor( color == null ? colors.get( OUTLINE ) : color );
			graphics.setStroke( stroke == null ? AbstractIcon.DEFAULT_STROKE : stroke );
			graphics.setFont( text.getFont() == null ? graphics.getFont() : text.getFont() );
			graphics.translate( x, y );
			graphics.drawString( text.getText(), (float)text.getX(), (float)text.getY() );
			graphics.translate( -x, -y );
		}

	}

	private static class SamplePanel extends JComponent {

		private static final long serialVersionUID = 7020998970315590613L;

		private Image iconImage;

		private Image gridImage;

		private Color border = new Color( 255, 0, 0, 64 );

		public SamplePanel( Icon icon, ImageFilter filter ) {
			iconImage = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
			icon.paintIcon( null, iconImage.getGraphics(), 0, 0 );
			if( filter != null ) iconImage = Toolkit.getDefaultToolkit().createImage( new FilteredImageSource( iconImage.getSource(), filter ) );
			gridImage = createBackgroundImage();
			setBackground( new Color( 220, 220, 220 ) );
			setBackground( Color.WHITE );
		}

		private Image createBackgroundImage() {
			Image image = new BufferedImage( 16, 16, BufferedImage.TYPE_INT_RGB );

			Graphics graphics = image.getGraphics();
			for( int x = 0; x < 16; x++ ) {
				for( int y = 0; y < 16; y++ ) {
					graphics.setColor( ( x + y ) % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY );
					graphics.fillRect( x, y, 1, 1 );
				}
			}

			return image;
		}

		@Override
		public void paint( Graphics graphics ) {
			graphics.setColor( getBackground() );
			graphics.fillRect( 0, 0, getWidth(), getHeight() );

			paintIcon( graphics, 0, 0, 256, true );
			paintZoomedIcon( graphics, 256, 0, 16, false );

			paintIcon( graphics, 0, 256, 256, false );
			paintIcon( graphics, 256, 256, 128, false );
			paintIcon( graphics, 384, 384, 64, false );
			paintIcon( graphics, 448, 448, 32, false );
			paintIcon( graphics, 480, 480, 16, false );

			// Center 448
			paintIcon( graphics, 436, 308, 24, false );
			paintIcon( graphics, 296, 420, 48, false );
		}

		private void paintIcon( Graphics graphics, int x, int y, int size, boolean paintGrid ) {
			if( paintGrid ) graphics.drawImage( gridImage, x, y, size, size, null );
			graphics.setColor( border );
			graphics.drawRect( x, y, size - 1, size - 1 );
			graphics.drawImage( iconImage.getScaledInstance( size, size, Image.SCALE_SMOOTH ), x, y, null );
		}

		private void paintZoomedIcon( Graphics graphics, int x, int y, int size, boolean paintGrid ) {
			if( paintGrid ) graphics.drawImage( gridImage, x, y, 256, 256, null );
			graphics.setColor( border );
			graphics.drawRect( x, y, 255, 255 );
			graphics.drawImage( iconImage.getScaledInstance( size, size, Image.SCALE_SMOOTH ), x, y, 256, 256, null );
		}

		@Override
		public Dimension getMinimumSize() {
			return new Dimension( 512, 512 );
		}

		@Override
		public Dimension getPreferredSize() {
			return getMinimumSize();
		}

		@Override
		public Dimension getMaximumSize() {
			return getMinimumSize();
		}

	}

}
