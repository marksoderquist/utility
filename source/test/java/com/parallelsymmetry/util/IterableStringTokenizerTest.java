package com.parallelsymmetry.util;

import junit.framework.TestCase;

import org.junit.Test;

public class IterableStringTokenizerTest extends TestCase {

	@Test
	public void testConstructor() {
		assertNotNull( new IterableStringTokenizer( "" ) );
		assertNotNull( new IterableStringTokenizer( "", "" ) );
		assertNotNull( new IterableStringTokenizer( "", "", true ) );
	}

	@Test
	public void testIterator() {
		IterableStringTokenizer tokenizer = new IterableStringTokenizer( "0:1:2", ":" );
		int index = 0;
		for( String token : tokenizer ) {
			assertEquals( String.valueOf( index++ ), token );
		}
	}

}
