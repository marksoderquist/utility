package com.parallelsymmetry.escape.utility;

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
	 * @param key
	 * @return
	 */
	public static final String getString( String path, String key ) {
		return getKeyOrString( null, path, key, null, true );
	}

	public static final String getString( ClassLoader loader, String path, String key ) {
		return getKeyOrString( loader, path, key, null, true );
	}

	/**
	 * Get a string from a resource bundle.
	 * 
	 * @param path
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static final String getString( String path, String key, String defaultValue ) {
		return getKeyOrString( null, path, key, defaultValue, true );
	}

	public static final String getString( ClassLoader loader, String path, String key, String defaultValue ) {
		return getKeyOrString( loader, path, key, defaultValue, true );
	}

	/**
	 * Get a string from a resource bundle. This method allows the caller to
	 * disable showing the bundle name and key by passing false to the showKey
	 * parameter.
	 * <p>
	 * Note: This method allows the caller to get a value of null.
	 * 
	 * @param path
	 * @param key
	 * @param showKey
	 * @return
	 */
	public static final String getString( String path, String key, boolean showKey ) {
		return getKeyOrString( null, path, key, null, showKey );
	}

	public static final String getString( ClassLoader loader, String path, String key, boolean showKey ) {
		return getKeyOrString( loader, path, key, null, showKey );
	}

	/**
	 * Get a string from a resource bundle. This method allows the caller to
	 * disable showing the bundle name and key by passing false to the showKey
	 * parameter.
	 * <p>
	 * Note: This method allows the caller to get a value of null.
	 * 
	 * @param path
	 * @param key
	 * @param defaultValue
	 * @param showKey
	 * @return
	 */
	public static final String getString( String path, String key, String defaultValue, boolean showKey ) {
		return getKeyOrString( null, path, key, defaultValue, showKey );
	}

	public static final String getString( ClassLoader loader, String path, String key, String defaultValue, boolean showKey ) {
		return getKeyOrString( loader, path, key, defaultValue, showKey );
	}

	private static final String getKeyOrString( ClassLoader loader, String path, String key, String defaultValue, boolean showKey ) {
		String string = null;
		if( loader == null ) loader = ClassLoader.getSystemClassLoader();
		ResourceBundle bundle = getBundle( loader, path, Locale.getDefault() );

		try {
			if( bundle != null ) string = bundle.getString( key );
		} catch( MissingResourceException exception ) {
			// Intentionally ignore exception.
		}

		if( string == null ) string = defaultValue;

		if( string == null && showKey ) string = defaultValue == null ? "[" + path + ":" + key + "]" : defaultValue;

		return string;
	}

	private static ResourceBundle getBundle( ClassLoader loader, String path, Locale locale ) {
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

				List<InputStream> streams = getResourceStreams( path, locale, loader );

				for( InputStream input : streams ) {
					if( bundle == null ) bundle = new MappedResourceBundle();
					try {
						bundle.add( new PropertyResourceBundle( new InputStreamReader( input, TextUtil.DEFAULT_ENCODING ) ) );
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

	private static List<InputStream> getResourceStreams( String path, Locale locale, ClassLoader loader ) {
		List<InputStream> streams = new ArrayList<InputStream>();
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
						streams.add( urls.nextElement().openStream() );
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
					streams.add( urls.nextElement().openStream() );
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
					streams.add( urls.nextElement().openStream() );
				}
			} catch( IOException exception ) {
				// Intentionally ignore exception.
			}
		}

		return streams;
	}

	private static class LoaderResources {

		public Set<String> missing = new CopyOnWriteArraySet<String>();

		public Map<String, MappedResourceBundle> bundles = new ConcurrentHashMap<String, MappedResourceBundle>();

	}

}
