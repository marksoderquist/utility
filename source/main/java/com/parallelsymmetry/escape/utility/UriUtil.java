package com.parallelsymmetry.escape.utility;

import java.net.URI;
import java.util.Deque;
import java.util.LinkedList;

public final class UriUtil {

	public static URI resolve( URI base, URI uri ) {
		if( uri == null ) return null;
		return base == null ? uri : base.resolve( uri );
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
			uri = URI.create( uri.getSchemeSpecificPart() );
		}

		uri = uri.resolve( "." );

		String scheme = null;
		while( ( scheme = queue.pollLast() ) != null ) {
			uri = URI.create( scheme + ":" + uri.toString() );
		}

		return uri;
	}

}
