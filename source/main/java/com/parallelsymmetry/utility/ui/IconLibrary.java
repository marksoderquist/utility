package com.parallelsymmetry.utility.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.parallelsymmetry.utility.FileUtil;
import com.parallelsymmetry.utility.HashUtil;
import com.parallelsymmetry.utility.IoUtil;
import com.parallelsymmetry.utility.log.Log;

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

	private File cachePath;

	private List<String> paths;

	private Map<String, Icon> icons;

	private Map<String, IconProxy> proxies;

	private Map<String, ClassLoader> loaders;

	private int size = DEFAULT_ICON_SIZE;

	/**
	 * Create the icon library. Icons are generated by loading images from the
	 * paths specified in the constructor or by using addPath(). Icon images are
	 * located using the specified name and the .png extension. This way icon
	 * images only need to be located in one if the paths and then called by name.
	 * For example, if an image exit.png is located in one of the paths then
	 * calling <code>library.getIcon( "exit" )</code> will return the icon using
	 * the exit.png image.
	 * <p>
	 * Note: File names and extensions are case sensitive.
	 */
	public IconLibrary() {
		this.paths = new CopyOnWriteArrayList<String>();
		this.icons = new ConcurrentHashMap<String, Icon>();
		this.proxies = new ConcurrentHashMap<String, IconProxy>();
		this.loaders = new ConcurrentHashMap<String, ClassLoader>();

		// Register the default path.
		addSearchPath( DEFAULT_ICON_PATH, getClass().getClassLoader() );

		// Register the broken icon.
		putIcon( BROKEN, new BrokenIcon() );
	}

	public File getCachePath() {
		return cachePath;
	}

	public void setCachePath( File path ) {
		if( this.cachePath != null ) FileUtil.delete( cachePath );

		this.cachePath = path;

		if( this.cachePath != null ) cachePath.mkdirs();
	}

	public Set<String> getKeys() {
		return icons.keySet();
	}

	public Icon getIcon( URI uri ) {
		return getProxiedIcon( uri, size, null );
	}

	public Icon getIcon( URI uri, int size ) {
		return getProxiedIcon( uri, size, null );
	}

	public Icon getIcon( URI uri, ImageFilter filter ) {
		return getProxiedIcon( uri, size, filter );
	}

	public Icon getIcon( URI uri, int size, ImageFilter filter ) {
		return getProxiedIcon( uri, size, filter );
	}

	/**
	 * Get an icon from the cache.
	 */
	public Icon getIcon( String name ) {
		return getProxiedIcon( name, size, null );
	}

	/**
	 * Get an icon from the cache.
	 */
	public Icon getIcon( String name, int size ) {
		return getProxiedIcon( name, size, null );
	}

	public Icon getIcon( String name, ImageFilter filter ) {
		return getProxiedIcon( name, size, filter );
	}

	public Icon getIcon( String name, int size, ImageFilter filter ) {
		return getProxiedIcon( name, size, filter );
	}

	public Image getImage( String name ) {
		return getImage( getIcon( name ) );
	}

	public Image getImage( String name, int size ) {
		return getImage( getIcon( name, size ) );
	}

	public Image getImage( String name, ImageFilter filter ) {
		return filterImage( getImage( name ), filter );
	}

	public Image getImage( String name, int size, ImageFilter filter ) {
		return filterImage( getImage( name, size ), filter );
	}

	/**
	 * Creates a rendered image from an icon. This is done by creating a
	 * <code>BufferedImage</code> the size of the icon and painting the icon on
	 * the image's graphics object.
	 */
	public BufferedImage getImage( Icon icon ) {
		if( icon == null ) icon = icons.get( BROKEN );

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
		proxies.clear();
	}

	/**
	 * Create a named cached icon.
	 */
	public void putIcon( String name, Icon renderer ) {
		if( renderer == null ) {
			icons.remove( name );
		} else {
			icons.put( name, renderer );
		}
	}

	public void removeIcon( String name ) {
		putIcon( name, null );
	}

	/**
	 * Add path to the search paths.
	 * 
	 * @param path
	 */
	public void addSearchPath( String path, ClassLoader loader ) {
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
	public void removeSearchPath( String path ) {
		if( path.startsWith( "/" ) ) path = path.substring( 1 );
		paths.remove( path );
		loaders.remove( path );
	}

	@Override
	protected void finalize() {
		proxies.clear();
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
	 * Get an icon from the cache.
	 */
	private IconProxy getProxiedIcon( String name, int size, ImageFilter filter ) {
		if( name == null ) name = BROKEN;
		//name = BROKEN;

		IconProxy proxy = null;
		String key = name + ":" + size;

		// Get a proxy from the cache.
		proxy = proxies.get( key );
		if( proxy != null ) return proxy;

		// Get the icon renderer.
		Icon renderer = icons.get( name );

		// If the renderer is null, try to load the image from the class loader.
		if( renderer == null ) {
			URL url = getIconUrl( name );
			if( url != null ) {
				try {
					renderer = new ImageIcon( ImageIO.read( url ) );
				} catch( IOException exception ) {
					// Intentionally ignore exception.
				}
			}
		}

		// If there is still no renderer use the broken icon.
		if( renderer == null ) renderer = icons.get( BROKEN );

		// Create a new icon proxy.
		proxy = new IconProxy( name, renderer, size, filter );
		proxies.put( key, proxy );

		return proxy;
	}

	private IconProxy getProxiedIcon( URI uri, int size, ImageFilter filter ) {
		//if( uri == null ) return getProxiedIcon( (String)null, size, filter );
		if( uri == null ) return null;

		IconProxy proxy = null;
		String name = HashUtil.hash( uri.toString() );
		String key = name + ":" + size;

		// Get a proxy from the cache.
		proxy = proxies.get( key );
		if( proxy != null ) return proxy;

		// Get the icon renderer.
		Icon renderer = icons.get( name );

		// If the renderer is null, load the image from the cache.
		if( renderer == null ) {
			Image image = null;
			InputStream input = null;
			OutputStream output = null;

			File file = new File( cachePath, name );

			// If the image is not in the cache, download it.
			if( !file.exists() ) {
				Log.write( Log.DEBUG, "Download icon: " + uri );
				try {
					input = uri.toURL().openStream();
					output = new FileOutputStream( file );
					IoUtil.copy( input, output );
				} catch( FileNotFoundException exception ) {
					Log.write( Log.WARN, "Icon not found: ", uri.toString() );
					return null;
				} catch( IOException exception ) {
					Log.write( exception, uri.toString() );
					return null;
				} finally {
					try {
						if( input != null ) input.close();
					} catch( IOException exception ) {
						// Intentionally ignore exception.
					}
					try {
						if( output != null ) output.close();
					} catch( IOException exception ) {
						// Intentionally ignore exception.
					}
				}
			}

			if( file.exists() && file.length() > 0 ) {
				try {
					image = ImageIO.read( file );
					renderer = new ImageIcon( image );
				} catch( IOException exception ) {
					return null;
				}
			}

			//			if( image != null ) renderer = new ImageIcon( image );
		}

		// If there is still no renderer use the broken icon.
		//if( renderer == null ) renderer = icons.get( BROKEN );
		if( renderer == null ) return null;

		// Create a new icon proxy.
		proxy = new IconProxy( name, renderer, size, filter );
		proxies.put( key, proxy );

		return proxy;
	}

	/**
	 * Creates a rendered icon from a renderer icon. This is done by creating a
	 * <code>BufferedImage</code> the size of the icon and painting the icon on
	 * the image's graphics object.
	 */
	private ImageIcon createRenderedIcon( Icon icon, ImageFilter filter ) {
		return createRenderedIcon( icon, icon.getIconWidth(), icon.getIconHeight(), filter );
	}

	/**
	 * Creates a rendered icon from a renderer icon. This is done by creating a
	 * <code>BufferedImage</code> the size of the icon and painting the icon on
	 * the image's graphics object.
	 */
	private ImageIcon createRenderedIcon( Icon icon, int size, ImageFilter filter ) {
		return createRenderedIcon( icon, size, size, filter );
	}

	/**
	 * Creates a rendered icon from a renderer icon. This is done by creating a
	 * <code>BufferedImage</code> the size of the icon and painting the icon on
	 * the image's graphics object.
	 */
	private ImageIcon createRenderedIcon( Icon icon, int width, int height, ImageFilter filter ) {
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
		private String name;

		/**
		 * The renderer Icon.
		 */
		private Icon icon;

		/**
		 * The icon size. Zero for default size.
		 */
		private int size;

		/**
		 * The image filter. Null for no filter.
		 */
		private ImageFilter filter;

		/**
		 * The rendered icon.
		 */

		private ImageIcon renderedIcon;

		/**
		 * Construct a <code>IconProxy</code>.
		 */
		public IconProxy( String name, Icon icon, int size, ImageFilter filter ) {
			this.name = name;
			this.icon = icon;
			this.size = size;
			this.filter = filter;
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
			getRenderedIcon().paintIcon( component, graphics, x, y );
		}

		@Override
		public String toString() {
			return name;
		}

		private Icon getRenderedIcon() {
			ImageIcon icon = renderedIcon;
			if( icon == null ) {
				icon = createRenderedIcon( this.icon, size, filter );
				renderedIcon = icon;
			}
			return icon;
		}

	}

}
