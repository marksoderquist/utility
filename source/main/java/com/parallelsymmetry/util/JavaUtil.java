package com.parallelsymmetry.util;

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
	 * Parse the relative URI strings from the specified classpath in system
	 * property format. See <a href=
	 * "http://java.sun.com/javase/6/docs/technotes/tools/windows/classpath.html"
	 * >Setting the Windows Classpath</a> or <a href=
	 * "http://java.sun.com/javase/6/docs/technotes/tools/solaris/classpath.html"
	 * >Setting the Unix Classpath</a>
	 */
	public static String[] parseSystemClasspath( String classpath ) {
		List<String> entries = new ArrayList<String>();

		StringTokenizer tokenizer = new StringTokenizer( classpath, File.pathSeparator );
		while( tokenizer.hasMoreTokens() ) {
			entries.add( tokenizer.nextToken() );
		}

		return entries.toArray( new String[entries.size()] );
	}

	/**
	 * Parse the relative URLs from the specified classpath in JAR file manifest
	 * format. See <a href="http://java.sun.com/javase/6/docs/technotes/guides/jar/jar.html#Main%20Attributes"
	 * >Setting the JAR Manifest Class-Path Attribute</a>
	 */
	public static URL[] parseManifestClasspath( URI base, String classpath ) throws IOException, MalformedURLException, URISyntaxException {
		List<URL> urls = new ArrayList<URL>();

		StringTokenizer tokenizer = new StringTokenizer( classpath, " " );
		while( tokenizer.hasMoreTokens() ) {
			String path = tokenizer.nextToken();
			URL url = new URL( base.resolve( path ).toString() );
			if( url == null ) continue;
			urls.add( url );
		}

		return urls.toArray( new URL[urls.size()] );
	}

}
