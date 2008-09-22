package com.parallelsymmetry.util;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.junit.Test;

public class CountingInputStreamTest extends TestCase {

	@Test
	public void testGetCount() throws Exception {
		byte[] data = "test".getBytes( Charset.forName( "US-ASCII" ) );

		CountingInputStream input = new CountingInputStream( new ByteArrayInputStream( data ) );
		assertEquals( 0, input.getCount() );

		// Read a character.
		input.read();
		assertEquals( 1, input.getCount() );

		// Read all characters.
		while( input.read() >= 0 ) {}
		assertEquals( data.length, input.getCount() );
	}
}
