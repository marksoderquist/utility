package com.parallelsymmetry.escape.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class Bundles {

	public static final String MESSAGES = "messages";

	private static final Set<ClassLoader> loaders = new CopyOnWriteArraySet<ClassLoader>();

	private static final Map<String, MappedResourceBundle> bundles = new ConcurrentHashMap<String, MappedResourceBundle>();

	private static final Set<String> missing = new CopyOnWriteArraySet<String>();

	static {
		register( Bundles.class.getClassLoader() );
	}

	public static final void register( ClassLoader loader ) {
		if( loader == null || loaders.contains( loader ) ) return;
		loaders.add( loader );
		clearCache();
	}

	public static final void clearCache() {
		bundles.clear();
		missing.clear();
	}

	/**
	 * Get a string from a resource bundle.
	 * 
	 * @param path
	 * @param key
	 * @return
	 */
	public static final String getString( String path, String key ) {
		return getKeyOrString( path, key, null, true );
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
		return getKeyOrString( path, key, defaultValue, true );
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
		return getKeyOrString( path, key, null, showKey );
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
		return getKeyOrString( path, key, defaultValue, showKey );
	}

	private static final String getKeyOrString( String path, String key, String defaultValue, boolean showKey ) {
		String string = null;
		ResourceBundle bundle = getBundle( path, Locale.getDefault() );

		try {
			if( bundle != null ) string = bundle.getString( key );
		} catch( MissingResourceException exception ) {
			// Intentionally ignore exception.
		}

		if( string == null ) string = defaultValue;

		if( string == null && showKey ) string = defaultValue == null ? "[" + path + ":" + key + "]" : defaultValue;

		return string;
	}

	private static ResourceBundle getBundle( String name, Locale locale ) {
		StringBuilder builder = new StringBuilder( name );
		builder.append( "_" );
		builder.append( locale.getLanguage() );
		String key = builder.toString();

		// If already known not to be available return null.
		if( missing.contains( key ) ) return null;

		MappedResourceBundle bundle = bundles.get( key );
		if( bundle == null ) {
			for( ClassLoader loader : loaders ) {
				InputStream input = getResourceAsStream( name, locale, loader );

				if( input != null ) {
					if( bundle == null ) bundle = new MappedResourceBundle();
					try {
						bundle.add( new PropertyResourceBundle( new InputStreamReader( input, TextUtil.DEFAULT_ENCODING ) ) );
					} catch( IOException exception ) {
						// Intentionally ignore exception.
					}
				}
			}

			if( bundle == null ) {
				missing.add( key );
			} else {
				bundles.put( key, bundle );
			}
		}

		return bundle;
	}

	private static InputStream getResourceAsStream( String name, Locale locale, ClassLoader loader ) {
		InputStream input = null;

		if( input == null ) {
			StringBuilder builder = new StringBuilder( name );
			builder.append( "_" );
			builder.append( locale.getISO3Language() );
			builder.append( ".properties" );
			input = loader.getResourceAsStream( builder.toString() );
		}

		if( input == null ) {
			StringBuilder builder = new StringBuilder( name );
			builder.append( "_en.properties" );
			input = loader.getResourceAsStream( builder.toString() );
		}

		if( input == null ) {
			StringBuilder builder = new StringBuilder( name );
			builder.append( ".properties" );
			input = loader.getResourceAsStream( builder.toString() );
		}

		return input;
	}

}
