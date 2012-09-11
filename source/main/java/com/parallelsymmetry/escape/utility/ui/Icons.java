package com.parallelsymmetry.escape.utility.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class Icons {

	public static Icon filter( Icon icon, ImageFilter filter ) {
		return new ImageIcon( getImage( icon, filter ) );
	}

	public static Image getImage( Icon icon ) {
		return getImage( icon, icon.getIconWidth(), icon.getIconHeight() );
	}

	public static Image getImage( Icon icon, int size ) {
		return getImage( icon, size, size );
	}

	public static Image getImage( Icon icon, int width, int height ) {
		return getImage( icon, width, height, null );
	}

	public static Image getImage( Icon icon, ImageFilter filter ) {
		return getImage( icon, icon.getIconWidth(), icon.getIconHeight(), filter );
	}

	public static Image getImage( Icon icon, int size, ImageFilter filter ) {
		return getImage( icon, size, size, filter );
	}

	public static Image getImage( Icon icon, int width, int height, ImageFilter filter ) {
		// Create the rendered image from the icon.
		Image image = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
		icon.paintIcon( null, image.getGraphics(), 0, 0 );

		// Scale the image to the icon size of the cache.
		image = Images.scale( image, width, height );

		// Apply the filter.
		if( filter != null ) image = Toolkit.getDefaultToolkit().createImage( new FilteredImageSource( image.getSource(), filter ) );

		return image;
	}

	public static void save( Icon icon, File target, String name ) {
		save( icon, target, name, icon.getIconWidth(), icon.getIconHeight() );
	}

	public static void save( Icon icon, File target, String name, int size ) {
		save( icon, target, name, size, size, null );
	}

	public static void save( Icon icon, File target, String name, int width, int height ) {
		save( icon, target, name, width, height, null );
	}

	public static void save( Icon icon, File target, String name, ImageFilter filter ) {
		save( icon, target, name, icon.getIconWidth(), icon.getIconHeight(), filter );
	}

	public static void save( Icon icon, File target, String name, int size, ImageFilter filter ) {
		save( icon, target, name, size, size, filter );
	}

	public static void save( Icon icon, File target, String name, int width, int height, ImageFilter filter ) {
		if( target.exists() && target.isFile() ) {
			System.err.println( "Target not a folder: " + target );
			return;
		}
		if( !target.exists() && !target.mkdirs() ) {
			System.err.println( "Could not create target: " + target );
			return;
		}

		BufferedImage image = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
		icon.paintIcon( null, image.getGraphics(), 0, 0 );

		// Scale the icon.
		if( icon.getIconWidth() != width || icon.getIconHeight() != height ) image = Images.scale( image, width, height );

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

	public static void show( Icon icon ) {
		show( icon, Color.WHITE );
	}

	public static void show( Icon icon, int size ) {
		show( icon, Color.WHITE, size );
	}

	public static void show( Icon icon, Color color ) {
		show( icon, color, 5 );
	}

	public static void show( Icon icon, Color color, int size ) {
		JPanel border = new JPanel();
		border.setBackground( color );
		border.setBorder( new LineBorder( color, size ) );
		border.setLayout( new BorderLayout() );

		IconPanel panel = new IconPanel( icon );
		border.add( panel, BorderLayout.CENTER );
		JOptionPane.showMessageDialog( null, border, null, JOptionPane.PLAIN_MESSAGE );
	}

	public static void proof( Icon icon ) {
		proof( icon, null );
	}

	public static void proof( Icon icon, int animationDelay ) {
		proof( icon, null, animationDelay );
	}

	public static void proof( Icon icon, ImageFilter filter ) {
		proof( icon, filter, 0 );
	}

	public static void proof( Icon icon, ImageFilter filter, int animationDelay ) {
		SamplePanel panel = new SamplePanel( icon, filter, animationDelay );
		JOptionPane.showMessageDialog( null, panel, null, JOptionPane.PLAIN_MESSAGE );
		panel.stopAnimation();
	}

	private static class SamplePanel extends JComponent implements ActionListener {

		private static final long serialVersionUID = 7020998970315590613L;
		
		private Timer timer;

		private Image iconImage;

		private Image gridImage;

		private int animateDelay;

		private Color border = new Color( 255, 0, 0, 64 );

		public SamplePanel( Icon icon, ImageFilter filter, int animateDelay ) {
			iconImage = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
			this.animateDelay = animateDelay;

			Graphics graphics = iconImage.getGraphics();
			icon.paintIcon( null, graphics, 0, 0 );
			graphics.dispose();

			iconImage = Images.filter( iconImage, filter );

			gridImage = createBackgroundImage();
			setBackground( new Color( 220, 220, 220 ) );
			setBackground( Color.WHITE );

			startAnimation();
		}

		public void startAnimation() {
			if( animateDelay <= 0 ) return;
			timer = new Timer( animateDelay, this );
			timer.setRepeats( true );
			timer.start();
		}
		
		public void stopAnimation() {
			if( timer != null ) timer.stop();
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

		@Override
		public void actionPerformed( ActionEvent event ) {
			repaint();
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

	}

}
