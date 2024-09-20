package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrimitivesTest extends BaseTestCase {

	@Test
	public void testParseBoolean() {
		assertFalse( Primitives.parseBoolean( null ) );
		assertTrue( Primitives.parseBoolean( "true" ) );
	}

	@Test
	public void testParseInt() {
		assertEquals( 0, Primitives.parseInt( null ) );

		assertEquals( Integer.MIN_VALUE, Primitives.parseInt( Integer.MIN_VALUE ) );
		assertEquals( Integer.MAX_VALUE, Primitives.parseInt( Integer.MAX_VALUE ) );

		assertEquals( Integer.MIN_VALUE, Primitives.parseInt( String.valueOf( Integer.MIN_VALUE ) ) );
		assertEquals( Integer.MAX_VALUE, Primitives.parseInt( String.valueOf( Integer.MAX_VALUE ) ) );
	}

	@Test
	public void testParseIntWithDefault() {
		assertEquals( 0, Primitives.parseInt( null, 0 ) );

		assertEquals( Integer.MIN_VALUE, Primitives.parseInt( null, Integer.MIN_VALUE ) );
		assertEquals( Integer.MAX_VALUE, Primitives.parseInt( null, Integer.MAX_VALUE ) );

		assertEquals( Integer.MIN_VALUE, Primitives.parseInt( "invalid", Integer.MIN_VALUE ) );
		assertEquals( Integer.MAX_VALUE, Primitives.parseInt( "invalid", Integer.MAX_VALUE ) );

		assertEquals( 20, Primitives.parseInt( "20", Integer.MIN_VALUE ) );
		assertEquals( 30, Primitives.parseInt( "30", Integer.MAX_VALUE ) );
	}

}
