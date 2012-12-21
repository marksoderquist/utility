package com.parallelsymmetry.utility;

import java.net.URI;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public final class UriUtil {

	public static URI resolve( URI uri, URI ref ) {
		if( ref == null ) return null;
		if( uri == null ) return ref;

		Deque<String> queue = new LinkedList<String>();

		if( "jar".equals( uri.getScheme() ) ) {
			while( uri.isOpaque() ) {
				queue.add( uri.getScheme() );
				uri = URI.create( uri.getRawSchemeSpecificPart() );
			}
		}

		uri = uri.resolve( ref );

		if( "file".equals( uri.getScheme() ) ) {
			String scheme = null;
			while( ( scheme = queue.pollLast() ) != null ) {
				uri = URI.create( scheme + ":" + uri.toString() );
			}
		}

		return uri;
	}

	/**
	 * Get the parent URI taking into account opaque URI's.
	 * 
	 * @param uri
	 * @return
	 */
	public static URI getParent( URI uri ) {
		Deque<String> queue = new LinkedList<String>();

		while( uri.isOpaque() ) {
			queue.add( uri.getScheme() );
			uri = URI.create( uri.getRawSchemeSpecificPart() );
		}

		uri = uri.resolve( "." );

		String scheme = null;
		while( ( scheme = queue.pollLast() ) != null ) {
			uri = URI.create( scheme + ":" + uri.toString() );
		}

		return uri;
	}

	public static Map<String, String> parseQuery( URI uri ) {
		if( uri == null ) return null;
		return parseQuery( uri.getQuery() );
	}

	public static Map<String, String> parseQuery( String query ) {
		if( query == null ) return null;

		Map<String, String> parameters = new HashMap<String, String>();

		String[] values = query.split( "\\&" );

		for( String value : values ) {
			int index = value.indexOf( "=" );
			if( index < 0 ) {
				parameters.put( value, "true" );
			} else {
				parameters.put( value.substring( 0, index ), value.substring( index + 1 ) );
			}
		}

		return parameters;
	}

}
