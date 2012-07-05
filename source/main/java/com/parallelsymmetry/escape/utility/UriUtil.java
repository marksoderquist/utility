package com.parallelsymmetry.escape.utility;

import java.net.URI;

public final class UriUtil {
	
	public static URI resolve( URI base, URI uri ) {
		if( uri == null ) return null;
		return base == null ? uri : base.resolve( uri );
	}

}
