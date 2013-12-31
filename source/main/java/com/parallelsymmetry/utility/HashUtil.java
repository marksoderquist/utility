package com.parallelsymmetry.utility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.parallelsymmetry.utility.log.Log;

import fr.cryptohash.DigestEngine;
import fr.cryptohash.Keccak256;

public class HashUtil {

	public static final HashStrategy SHA1 = new DigestStrategy( new MessageDigestWrapper( "SHA-1" ) );

	public static final HashStrategy MD5 = new DigestStrategy( new MessageDigestWrapper( "MD5" ) );

	public static final HashStrategy KECCAK = new DigestStrategy( new KeccakDigestWrapper( new Keccak256() ) );

	public static final HashStrategy DEFAULT_STRATEGY = SHA1;

	public static final String hash( String text ) {
		if( text == null ) return null;
		return hash( text, DEFAULT_STRATEGY );
	}

	public static final String hash( String text, HashStrategy strategy ) {
		return hash( text.getBytes( TextUtil.DEFAULT_CHARSET ), strategy );
	}

	public static final String hash( byte[] bytes ) {
		if( bytes == null ) return null;
		return hash( new ByteArrayInputStream( bytes ), DEFAULT_STRATEGY );
	}

	public static final String hash( byte[] bytes, HashStrategy strategy ) {
		return hash( new ByteArrayInputStream( bytes ), strategy );
	}

	public static final String hash( File file ) {
		return hash( file, DEFAULT_STRATEGY );
	}

	public static final String hash( File file, HashStrategy strategy ) {
		if( file == null || !file.exists() || !file.isFile() ) return null;

		FileInputStream input = null;
		try {
			input = new FileInputStream( file );
			return hash( input, strategy );
		} catch( FileNotFoundException exception ) {
			Log.write( exception );
			return null;
		} finally {
			if( input != null ) {
				try {
					input.close();
				} catch( IOException exception ) {
					Log.write( exception );
				}
			}
		}
	}

	public static final String hash( InputStream input ) {
		return hash( input, DEFAULT_STRATEGY );
	}

	public static final String hash( InputStream input, HashStrategy strategy ) {
		if( input == null ) return null;
		return strategy.hash( input );
	}

	private static interface DigestWrapper {
	
		void reset();
	
		void update( byte[] input, int offset, int len );
	
		byte[] digest();
	
	}

	private static final class DigestStrategy implements HashStrategy {

		private DigestWrapper digest;

		public DigestStrategy( DigestWrapper digest ) {
			setDigest( digest );
		}

		protected void setDigest( DigestWrapper digest ) {
			this.digest = digest;
		}

		@Override
		public String hash( InputStream input ) {
			if( input == null ) return null;

			byte[] buffer = new byte[4096];
			digest.reset();

			int count = 0;
			try {
				while( ( count = input.read( buffer ) ) > -1 ) {
					digest.update( buffer, 0, count );
				}
			} catch( IOException exception ) {
				Log.write( exception );
				return null;
			}

			return TextUtil.toHexEncodedString( digest.digest() );
		}

	}

	private static final class MessageDigestWrapper implements DigestWrapper {

		private MessageDigest digest;

		public MessageDigestWrapper( String algorithm ) {
			MessageDigest digest = null;
			try {
				digest = MessageDigest.getInstance( algorithm );
			} catch( NoSuchAlgorithmException exception ) {
				Log.write( exception );
			}
			this.digest = digest;
		}

		@Override
		public void reset() {
			digest.reset();
		}

		@Override
		public void update( byte[] input, int offset, int length ) {
			digest.update( input, offset, length );
		}

		@Override
		public byte[] digest() {
			return digest.digest();
		}

	}

	private static final class KeccakDigestWrapper implements DigestWrapper {

		private DigestEngine digest;

		public KeccakDigestWrapper( DigestEngine digest ) {
			this.digest = digest;
		}

		@Override
		public void reset() {
			digest.reset();
		}

		@Override
		public void update( byte[] input, int offset, int length ) {
			digest.update( input, offset, length );
		}

		@Override
		public byte[] digest() {
			return digest.digest();
		}

	}

}
