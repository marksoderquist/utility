package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class JavaUtil {

	public static final String getCallingClassName() {
		return getCallingClassName( 3 );
	}

	public static final String getCallingClassName( int level ) {
		return Thread.currentThread().getStackTrace()[level].getClassName();
	}

	/**
	 * Get the simple class name from a full class name.
	 * 
	 * @param name
	 * @return
	 */
	public static final String getClassName( String name ) {
		return name.substring( name.lastIndexOf( '.' ) + 1 );
	}

	/**
	 * Get the simple class name from a full class name.
	 * 
	 * @param name
	 * @return
	 */
	public static final String getClassName( Class<?> type ) {
		return ( getClassName( type.getName() ) );
	}

	public static final String getPackageName( String name ) {
		return name.substring( 0, name.lastIndexOf( '.' ) );
	}

	public static final String getPackageName( Class<?> type ) {
		return ( getPackageName( type.getName() ) );
	}

	public static final String getPackagePath( String name ) {
		return "/" + getPackageName( name ).replace( '.', '/' );
	}

	public static final String getPackagePath( Class<?> type ) {
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

}
