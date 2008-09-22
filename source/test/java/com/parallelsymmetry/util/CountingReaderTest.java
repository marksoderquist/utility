package com.parallelsymmetry.util;

import java.io.StringReader;

import junit.framework.TestCase;

import org.junit.Test;

public class CountingReaderTest extends TestCase {

	@Test
	public void testGetCount() throws Exception {
		String data = "test";

		CountingReader reader = new CountingReader( new StringReader( data ) );
		assertEquals( 0, reader.getCount() );

		// Read a character.
		reader.read();
		assertEquals( 1, reader.getCount() );

		// Read all characters.
		while( reader.read() >= 0 ) {}
		assertEquals( data.length(), reader.getCount() );
	}

}
