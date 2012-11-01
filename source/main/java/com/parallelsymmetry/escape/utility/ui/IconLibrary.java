package com.parallelsymmetry.escape.utility.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * The IconLibrary is the class where the program can easily obtain an icon by
 * name. For example, when the program needs an icon for the exit action, simply
 * call iconLibrary.getIcon( "exit" ) and an icon is returned. The source of the
 * icon can be registered with the registerIcon() method or be a PNG image file
 * on the class path. In order to use PNG images on the class path the base path
 * of the icons must be registered with the addPath() method. More than one base
 * path may be registered.
 * 
 * @author Mark Soderquist
 */
public class IconLibrary {

	public static final int DEFAULT_ICON_SIZE = 16;

	public static final String BROKEN = "broken";

	private static final String DEFAULT_ICON_PATH = "/";

	private Map<String, ClassLoader> loaders;

	private List<String> paths;

	private Map<String, IconProxy> proxies;

	private Map<IconProxy, Icon> icons;

	private int size = DEFAULT_ICON_SIZE;

	private boolean debug = false;

	/**
	 * Create the icon library. Icons are generated by loading images from the
	 * paths specified in the constructor or by using addPath(). Icon images are
	 * located using the specified name and the .png extension. This way icon
	 * images only need to be located in one if the paths and then called by name.
	 * For example, if an image exit.png is located in one of the paths then
	 * calling <code>library.getIcon( "exit" )</code> will return the icon using
	 * the exit.png image. Note that file names and extensions are case sensitive.
	 */
	public IconLibrary() {
		this( DEFAULT_ICON_PATH );
	}

	public IconLibrary( String path ) {
		this.paths = new CopyOnWriteArrayList<String>();
		this.loaders = new ConcurrentHashMap<String, ClassLoader>();
		proxies = new ConcurrentHashMap<String, IconProxy>();
		icons = new ConcurrentHashMap<IconProxy, Icon>();

		ClassLoader loader = getClass().getClassLoader();
		addPath( path, loader );

		// No other icons need to be installed. See JavaDoc.
		registerIcon( BROKEN, new BrokenIcon() );
	}

	/**
	 * Get an icon from the cache.
	 */
	public Icon getIcon( String name ) {
		return getIcon( name, size );
	}

	/**
	 * Get an icon from the cache.
	 */
	public Icon getIcon( String name, int size ) {
		if( name == null ) return getIcon( BROKEN, this.size );

		String key = name + ":" + size;
		IconProxy proxy = null;

		// Get a proxy from the cache.
		proxy = proxies.get( key );
		if( proxy != null ) return proxy;

		// Get a proxy from a resource image.
		proxy = proxies.get( name );
		if( proxy == null ) {
			URL url = getIconUrl( name );
			if( url != null ) {
				try {
					registerIcon( name, new ImageIcon( ImageIO.read( url ) ) );
					proxy = proxies.get( name );
				} catch( IOException e ) {
					// Intentionally ignore exception.
				}
			}
		}

		// If there is still no proxy use the broken icon.
		if( proxy == null ) proxy = proxies.get( BROKEN );

		// Scale the
		IconProxy scaledProxy = new IconProxy( name, proxy.getRenderer(), size );
		proxies.put( key, scaledProxy );

		return scaledProxy;
	}

	public Icon getIcon( String name, ImageFilter filter ) {
		return new IconProxy( name, getIcon( name ), filter );
	}

	public Icon getIcon( String name, int size, ImageFilter filter ) {
		return new IconProxy( name, getIcon( name, size ), filter );
	}

	public Image getImage( String name ) {
		return getImage( getIcon( name ) );
	}

	public Image getImage( String name, int size ) {
		return getImage( getIcon( name, size ) );
	}

	public Image getImage( String name, ImageFilter filter ) {
		return getImage( getIcon( name, filter ) );
	}

	public Image getImage( String name, int size, ImageFilter filter ) {
		return getImage( getIcon( name, size ), filter );
	}

	/**
	 * Creates a rendered image from an icon. This is done by creating a
	 * <code>BufferedImage</code> the size of the icon and painting the icon on
	 * the image's graphics object.
	 */
	public BufferedImage getImage( Icon icon ) {
		if( icon == null ) icon = getIcon( BROKEN );

		BufferedImage image = new BufferedImage( icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB );
		Graphics graphics = image.getGraphics();
		icon.paintIcon( null, graphics, 0, 0 );
		graphics.dispose();
		
		return image;
	}

	public Image getImage( Icon icon, ImageFilter filter ) {
		return getImage( createRenderedIcon( icon, filter ) );
	}

	/**
	 * Get the icon size for the library.
	 */
	public int getIconSize() {
		return size;
	}

	/**
	 * Set the icon size for the library. This will clear the icon cache.
	 */
	public void setIconSize( int size ) {
		this.size = size;
		icons.clear();
	}

	/**
	 * Create a named cached icon.
	 */
	public void registerIcon( String name, Icon renderer ) {
		proxies.put( name, new IconProxy( name, renderer ) );
	}

	public void addPath( String path ) {
		addPath( path, getClass().getClassLoader() );
	}

	/**
	 * Add path to the search paths.
	 * 
	 * @param path
	 */
	public void addPath( String path, ClassLoader loader ) {
		if( path == null || loader == null || paths.contains( path ) ) return;
		if( path.startsWith( "/" ) ) path = path.substring( 1 );
		loaders.put( path, loader );
		paths.add( path );
	}

	/**
	 * Remove a path from the search paths.
	 * 
	 * @param path
	 */
	public void removePath( String path ) {
		if( path.startsWith( "/" ) ) path = path.substring( 1 );
		paths.remove( path );
		loaders.remove( path );
	}

	public void reset() {
		proxies.clear();
	}
	
	@Override
	protected void finalize() {
		proxies.clear();
		icons.clear();
	}

	private URL getIconUrl( String name ) {
		URL url = null;

		for( String path : paths ) {
			url = loaders.get( path ).getResource( path + "/" + name + ".png" );
			if( url != null ) break;
		}

		return url;
	}

	/**
	 * Creates a rendered icon from a renderer icon. This is done by creating a
	 * <code>BufferedImage</code> the size of the icon and painting the icon on
	 * the image's graphics object.
	 */
	private Icon createRenderedIcon( Icon icon, ImageFilter filter ) {
		return createRenderedIcon( icon, icon.getIconWidth(), icon.getIconHeight(), filter );
	}

	/**
	 * Creates a rendered icon from a renderer icon. This is done by creating a
	 * <code>BufferedImage</code> the size of the icon and painting the icon on
	 * the image's graphics object.
	 */
	private Icon createRenderedIcon( Icon icon, int size, ImageFilter filter ) {
		return createRenderedIcon( icon, size, size, filter );
	}

	/**
	 * Creates a rendered icon from a renderer icon. This is done by creating a
	 * <code>BufferedImage</code> the size of the icon and painting the icon on
	 * the image's graphics object.
	 */
	private Icon createRenderedIcon( Icon icon, int width, int height, ImageFilter filter ) {
		// Create the rendered image from the icon.
		Image image = getImage( icon );

		// Scale the image to the icon size of the cache.
		image = image.getScaledInstance( width, height, Image.SCALE_SMOOTH );

		image = filterImage( image, filter );

		// Return a new ImageIcon.
		return new ImageIcon( image );
	}

	/**
	 * Filter the image.
	 * 
	 * @param image
	 * @param filter
	 * @return
	 */
	private Image filterImage( Image image, ImageFilter filter ) {
		if( image == null ) return null;
		if( filter == null ) return image;
		return Toolkit.getDefaultToolkit().createImage( new FilteredImageSource( image.getSource(), filter ) );
	}

	/**
	 * The proxy icon is used to lazy load the icons.
	 */
	private class IconProxy implements Icon {

		/**
		 * The key of the icon.
		 */
		private String key;

		/**
		 * The renderer Icon.
		 */
		private Icon renderer;

		/**
		 * The icon size. Zero for default size.
		 */
		private int size;

		/**
		 * The image filter. Null for no filter.
		 */
		private ImageFilter filter;

		/**
		 * Construct a <code>IconProxy</code>.
		 */
		public IconProxy( String key, Icon renderer ) {
			this( key, renderer, 0, null );
		}

		/**
		 * Construct a <code>IconProxy</code>.
		 */
		public IconProxy( String key, Icon renderer, int size ) {
			this( key, renderer, size, null );
		}

		/**
		 * Construct a <code>IconProxy</code>.
		 */
		public IconProxy( String key, Icon renderer, ImageFilter filter ) {
			this( key, renderer, 0, filter );
		}

		/**
		 * Construct a <code>IconProxy</code>.
		 */
		public IconProxy( String key, Icon renderer, int size, ImageFilter filter ) {
			this.key = key;
			this.renderer = renderer;
			this.size = size;
			this.filter = filter;
		}

		/**
		 * Get the renderer icon.
		 */
		public Icon getRenderer() {
			return renderer;
		}

		/**
		 * Get the width of the icon.
		 */
		@Override
		public int getIconWidth() {
			return getRenderedIcon().getIconWidth();
		}

		/**
		 * Get the height of the icon.
		 */
		@Override
		public int getIconHeight() {
			return getRenderedIcon().getIconHeight();
		}

		/**
		 * Paint the icon.
		 * 
		 * @param graphics The <code>Graphics</code> object to use.
		 */
		@Override
		public void paintIcon( Component component, Graphics graphics, int x, int y ) {
			Icon icon = getRenderedIcon();
			icon.paintIcon( component, graphics, x, y );
			if( debug ) {
				graphics.setColor( Color.RED );
				graphics.drawRect( 0, 0, icon.getIconWidth() - 1, icon.getIconHeight() - 1 );
			}
		}

		@Override
		public String toString() {
			return key;
		}

		/**
		 * Get the rendered version of the icon.
		 */
		private Icon getRenderedIcon() {
			Icon icon = icons.get( this );
			if( icon != null ) return icon;

			if( size == 0 ) {
				icon = createRenderedIcon( renderer, IconLibrary.this.size, filter );
			} else {
				icon = createRenderedIcon( renderer, size, filter );
			}

			icons.put( this, icon );
			return icon;
		}

	}

}
