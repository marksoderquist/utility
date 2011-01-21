package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class JavaUtil {

	/**
	 * Get the simple class name from a full class name.
	 * 
	 * @param name
	 * @return
	 */
	public static final String getSimpleClassName( String name ) {
		return name.substring( name.lastIndexOf( '.' ) + 1 );
	}

	/**
	 * Parse the relative URI strings from the specified classpath in system
	 * property format. See <a href=
	 * "http://java.sun.com/javase/6/docs/technotes/tools/windows/classpath.html"
	 * >Setting the Windows Classpath</a> or <a href=
	 * "http://java.sun.com/javase/6/docs/technotes/tools/solaris/classpath.html"
	 * >Setting the Unix Classpath</a>
	 */
	public static List<URI> parseSystemClasspath( String classpath ) throws URISyntaxException {
		ArrayList<URI> list = new ArrayList<URI>();
		if( classpath == null ) return list;

		StringTokenizer tokenizer = new StringTokenizer( classpath, File.pathSeparator );
		while( tokenizer.hasMoreTokens() ) {
			list.add( new URI( tokenizer.nextToken() ) );
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
