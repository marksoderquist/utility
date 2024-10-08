package com.parallelsymmetry.utility;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import com.parallelsymmetry.utility.log.Log;

public final class JavaUtil {

	public static String getCallingClassName() {
		return getCallingClassName( 3 );
	}

	public static String getCallingClassName( int level ) {
		return Thread.currentThread().getStackTrace()[level].getClassName();
	}

	/**
	 * Get the simple class name from a full class name.
	 * 
	 * @param name
	 * @return
	 */
	public static String getClassName( String name ) {
		return name.substring( name.lastIndexOf( '.' ) + 1 );
	}

	/**
	 * Get the simple class name from a full class name.
	 * 
	 * @param name
	 * @return
	 */
	public static String getClassName( Class<?> type ) {
		return ( getClassName( type.getName() ) );
	}

	public static String getKeySafeClassName( String name ) {
		return name.replace( "$", "." );
	}

	public static String getKeySafeClassName( Class<?> type ) {
		return getKeySafeClassName( type.getName() );
	}

	public static String getPackageName( String name ) {
		return name.substring( 0, name.lastIndexOf( '.' ) );
	}

	public static String getPackageName( Class<?> type ) {
		return ( getPackageName( type.getName() ) );
	}

	public static String getPackagePath( String name ) {
		return "/" + getPackageName( name ).replace( '.', '/' );
	}

	public static String getPackagePath( Class<?> type ) {
		return ( getPackagePath( type.getName() ) );
	}

	public static List<URI> getClasspath() {
		try {
			return parseClasspath( System.getProperty( "class.path" ) );
		} catch( URISyntaxException exception ) {
			return null;
		}
	}

	public static List<URI> parseClasspath( String classpath ) throws URISyntaxException {
		return parseClasspath( classpath, File.pathSeparator );
	}

	/**
	 * Parse the relative URI strings from the specified classpath in system
	 * property format. See <a href=
	 * "http://java.sun.com/javase/6/docs/technotes/tools/windows/classpath.html"
	 * >Setting the Windows Classpath</a> or <a href=
	 * "http://java.sun.com/javase/6/docs/technotes/tools/solaris/classpath.html"
	 * >Setting the Unix Classpath</a>
	 */
	public static List<URI> parseClasspath( String classpath, String separator ) throws URISyntaxException {
		ArrayList<URI> list = new ArrayList<URI>();
		if( classpath == null ) return list;

		URI uri = null;
		String token = null;
		StringTokenizer tokenizer = new StringTokenizer( classpath, separator );
		while( tokenizer.hasMoreTokens() ) {
			token = tokenizer.nextToken();

			try {
				uri = new URI( URLDecoder.decode( token, "UTF-8" ) );
			} catch( URISyntaxException excpetion ) {
				uri = new File( token ).toURI();
			} catch( UnsupportedEncodingException exception ) {
				// Intentionally ignore exception because UTF-8 is always supported.
			}
			if( uri.getScheme() == null ) uri = new File( token ).toURI();

			list.add( uri );
		}

		return list;
	}

	/**
	 * Parse the relative URLs from the specified classpath in JAR file manifest
	 * format. See <a href=
	 * "http://java.sun.com/javase/6/docs/technotes/guides/jar/jar.html#Main%20Attributes"
	 * >Setting the JAR Manifest Class-Path Attribute</a>
	 */
	public static List<URL> parseManifestClasspath( URI base, String classpath ) throws IOException, MalformedURLException, URISyntaxException {
		List<URL> urls = new ArrayList<URL>();

		if( base == null || classpath == null ) return urls;

		StringTokenizer tokenizer = new StringTokenizer( classpath, " " );
		while( tokenizer.hasMoreTokens() ) {
			String path = tokenizer.nextToken();
			urls.add( new URL( base.resolve( path ).toString() ) );
		}

		return urls;
	}

	/**
	 * Get the root cause of a throwable.
	 * 
	 * @param throwable
	 * @return
	 */
	public static Throwable getRootCause( Throwable throwable ) {
		Throwable cause = throwable;

		while( cause != null && cause.getCause() != null ) {
			cause = cause.getCause();
		}

		return cause;
	}

	public static void dumpSystemProperties() {
		Properties properties = System.getProperties();
		List<String> keys = new ArrayList<String>( properties.size() );
		for( Object key : System.getProperties().keySet() ) {
			keys.add( key.toString() );
		}
		Collections.sort( keys );

		for( String key : keys ) {
			System.out.println( key + " = " + properties.get( key ) );
		}
	}

	public static void printClassLoader( Object object ) {
		if( object instanceof Class ) {
			Log.write( Log.TRACE, "Class loader for ", getClassName( (Class<?>)object ), ": ", ( (Class<?>)object ).getClassLoader() );
		} else {
			Log.write( Log.TRACE, "Class loader for ", getClassName( object.getClass() ), ": ", getClassLoader( object ) );
		}
	}

	public static ClassLoader getClassLoader( Object object ) {
		return object.getClass().getClassLoader();
	}

	public static int compareJavaVersion( String a, String b ) {
		if( a.startsWith( "1." ) ) a = a.substring( 2 );
		if( b.startsWith( "1." ) ) b = b.substring( 2 );
		return Version.compareVersions( a, b );
	}

}
