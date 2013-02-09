package com.parallelsymmetry.utility.ui;

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
		if( icon instanceof AnimatedIcon ) ( (AnimatedIcon)icon ).stopAnimation();
	}

	private static class SamplePanel extends JComponent implements ActionListener {

		private static final long serialVersionUID = 7020998970315590613L;

		private Icon icon;

		private ImageFilter filter;

		private Image iconImage;

		private Image gridImage;

		private Color border = new Color( 255, 0, 0, 64 );

		public SamplePanel( Icon icon, ImageFilter filter, int animateDelay ) {
			this.icon = icon;

			iconImage = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );

			Graphics graphics = iconImage.getGraphics();
			icon.paintIcon( null, graphics, 0, 0 );
			graphics.dispose();

			iconImage = Images.filter( iconImage, filter );

			gridImage = createBackgroundImage();
			setBackground( new Color( 220, 220, 220 ) );
			setBackground( Color.WHITE );

			if( icon instanceof AnimatedIcon ) ( (AnimatedIcon)icon ).startAnimation( this, animateDelay );
		}

		@Override
		public void paint( Graphics graphics ) {
			graphics.setColor( getBackground() );
			graphics.fillRect( 0, 0, getWidth(), getHeight() );

			iconImage = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
			Graphics scratch = iconImage.getGraphics();
			icon.paintIcon( null, scratch, 0, 0 );
			scratch.dispose();

			iconImage = Images.filter( iconImage, filter );

			paintIcon( graphics, 0, 0, 256, true );
			paintZoomedIcon( graphics, 256, 0, 16, false );
			paintZoomedIcon( graphics, 256, 256, 24, false );

			int x = 0;
			int y = 256;
			paintIcon( graphics, x + 0, y + 0, 128, false );
			paintIcon( graphics, x + 128, y + 128, 64, false );
			paintIcon( graphics, x + 192, y + 192, 32, false );
			paintIcon( graphics, x + 224, y + 224, 16, false );

			// Center 448
			paintIcon( graphics, x + 180, y + 52, 24, false );
			paintIcon( graphics, x + 40, y + 164, 48, false );
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
			if( icon instanceof AnimatedIcon ) ( (AnimatedIcon)icon ).incrementFrame();
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
			int count = 16;

			Image image = new BufferedImage( count, count, BufferedImage.TYPE_INT_RGB );

			Graphics graphics = image.getGraphics();
			for( int x = 0; x < count; x++ ) {
				for( int y = 0; y < count; y++ ) {
					graphics.setColor( ( x + y ) % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY );
					graphics.fillRect( x, y, 1, 1 );
				}
			}

			return image;
		}

	}

}
