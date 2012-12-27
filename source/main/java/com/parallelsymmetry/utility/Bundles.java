package com.parallelsymmetry.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class Bundles {

	private static final Map<ClassLoader, LoaderResources> cache = new HashMap<ClassLoader, LoaderResources>();

	public static final void clearCache() {
		cache.clear();
	}

	/**
	 * Get a string from a resource bundle.
	 * 
	 * @param path
	 * @param name
	 * @return
	 */
	public static final String getString( String path, String name ) {
		return getKeyOrString( null, path, name, null, true );
	}

	public static final String getString( ClassLoader loader, String path, String name ) {
		return getKeyOrString( loader, path, name, null, true );
	}

	/**
	 * Get a string from a resource bundle.
	 * 
	 * @param path
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public static final String getString( String path, String name, String defaultValue ) {
		return getKeyOrString( null, path, name, defaultValue, true );
	}

	public static final String getString( ClassLoader loader, String path, String name, String defaultValue ) {
		return getKeyOrString( loader, path, name, defaultValue, true );
	}

	/**
	 * Get a string from a resource bundle. This method allows the caller to
	 * disable showing the bundle name and key by passing false to the showKey
	 * parameter.
	 * <p>
	 * Note: This method allows the caller to get a value of null.
	 * 
	 * @param path
	 * @param name
	 * @param showKey
	 * @return
	 */
	public static final String getString( String path, String name, boolean showKey ) {
		return getKeyOrString( null, path, name, null, showKey );
	}

	public static final String getString( ClassLoader loader, String path, String name, boolean showKey ) {
		return getKeyOrString( loader, path, name, null, showKey );
	}

	/**
	 * Get a string from a resource bundle. This method allows the caller to
	 * disable showing the bundle name and key by passing false to the showKey
	 * parameter.
	 * <p>
	 * Note: This method allows the caller to get a value of null.
	 * 
	 * @param path
	 * @param name
	 * @param defaultValue
	 * @param showKey
	 * @return
	 */
	public static final String getString( String path, String name, String defaultValue, boolean showKey ) {
		return getKeyOrString( null, path, name, defaultValue, showKey );
	}

	public static final String getString( ClassLoader loader, String path, String name, String defaultValue, boolean showKey ) {
		return getKeyOrString( loader, path, name, defaultValue, showKey );
	}

	private static final String getKeyOrString( ClassLoader loader, String path, String name, String defaultValue, boolean showKey ) {
		if( loader == null ) loader = ClassLoader.getSystemClassLoader();
		ResourceBundle bundle = getBundle( loader, path, name, Locale.getDefault() );

		// Define the string.
		String string = null;

		// If the string is null try and get the string from the bundle.
		try {
			if( bundle != null ) string = bundle.getString( name );
		} catch( MissingResourceException exception ) {
			// Intentionally ignore exception.
		}

		// If the string is null try and use the default value.
		if( string == null ) string = defaultValue;

		// If the string is null use the path and name to create a string
		if( string == null && showKey ) string = "[" + path + ":" + name + "]";

		return string;
	}

	private static ResourceBundle getBundle( ClassLoader loader, String path, String name, Locale locale ) {
		StringBuilder builder = new StringBuilder( path );
		builder.append( "." );
		builder.append( locale.toString() );
		String key = builder.toString();

		// Get the loader resources.
		LoaderResources resources = null;
		synchronized( cache ) {
			resources = cache.get( loader );
			if( resources == null ) {
				resources = new LoaderResources();
				cache.put( loader, resources );
			}
		}
		Set<String> missing = resources.missing;
		Map<String, MappedResourceBundle> bundles = resources.bundles;

		// If already known not to be available return null.
		if( missing.contains( key ) ) return null;

		// Get the resource bundle.
		MappedResourceBundle bundle = null;

		synchronized( bundles ) {
			bundle = bundles.get( key );

			// Load the bundle if it does not exist.
			if( bundle == null ) {

				List<URL> urls = getResources( loader, path, locale );

				for( URL url : urls ) {
					if( bundle == null ) bundle = new MappedResourceBundle();
					try {
						PropertyResourceBundle propertyBundle = new PropertyResourceBundle( new InputStreamReader( url.openStream(), TextUtil.DEFAULT_ENCODING ) );
						bundle.add( propertyBundle );
					} catch( IOException exception ) {
						// Intentionally ignore exception.
					}
				}

				if( bundle == null ) {
					missing.add( key );
				} else {
					bundles.put( key, bundle );
				}
			}
		}

		return bundle;
	}

	private static List<URL> getResources( ClassLoader loader, String path, Locale locale ) {
		List<URL> resources = new ArrayList<URL>();
		InputStream input = null;

		String[] parts = getLocaleParts( locale );

		for( int count = parts.length; count > -1; count-- ) {
			if( input == null ) {
				StringBuilder builder = new StringBuilder( path );
				builder.append( "." );
				builder.append( TextUtil.toString( parts, "_", 0, count ) );
				builder.append( ".properties" );
				try {
					Enumeration<URL> urls = loader.getResources( builder.toString() );
					while( urls.hasMoreElements() ) {
						URL url = urls.nextElement();
						if( !resources.contains( url ) ) resources.add( url );

					}
				} catch( IOException exception ) {
					// Intentionally ignore exception.
				}
			}
		}

		if( input == null ) {
			StringBuilder builder = new StringBuilder( path );
			builder.append( ".en.properties" );
			try {
				Enumeration<URL> urls = loader.getResources( builder.toString() );
				while( urls.hasMoreElements() ) {
					URL url = urls.nextElement();
					if( !resources.contains( url ) ) resources.add( url );
				}
			} catch( IOException exception ) {
				// Intentionally ignore exception.
			}
		}

		if( input == null ) {
			StringBuilder builder = new StringBuilder( path );
			builder.append( ".properties" );
			try {
				Enumeration<URL> urls = loader.getResources( builder.toString() );
				while( urls.hasMoreElements() ) {
					URL url = urls.nextElement();
					if( !resources.contains( url ) ) resources.add( url );
				}
			} catch( IOException exception ) {
				// Intentionally ignore exception.
			}
		}

		return resources;
	}

	private static String[] getLocaleParts( Locale locale ) {
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String variant = locale.getVariant();

		boolean l = language.length() != 0;
		boolean c = country.length() != 0;
		boolean v = variant.length() != 0;

		int count = 0;
		if( l ) count++;
		if( c ) count++;
		if( v ) count++;

		String[] parts = new String[count];

		int index = 0;
		if( l ) parts[index++] = language;
		if( c ) parts[index++] = country;
		if( v ) parts[index++] = variant;

		return parts;
	}

	private static class LoaderResources {

		public Set<String> missing = new CopyOnWriteArraySet<String>();

		public Map<String, MappedResourceBundle> bundles = new ConcurrentHashMap<String, MappedResourceBundle>();

	}

}
