package com.parallelsymmetry.escape.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.parallelsymmetry.escape.utility.log.Log;

public class HashUtil {

	public static final String hash( String text ) {
		if( text == null ) return null;
		try {
			MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
			digest.reset();
			digest.update( text.getBytes( TextUtil.DEFAULT_CHARSET ) );
			return TextUtil.toHexEncodedString( digest.digest() );
		} catch( NoSuchAlgorithmException exception ) {
			Log.write( exception );
		}

		return text;
	}
}
