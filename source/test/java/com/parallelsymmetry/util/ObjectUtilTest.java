package com.parallelsymmetry.util;

import junit.framework.TestCase;

import org.junit.Test;

public class ObjectUtilTest extends TestCase {

	@Test
	public void testAreEqual() {
		assertTrue( ObjectUtil.areEqual( null, null ) );
		assertTrue( ObjectUtil.areEqual( "test", "test" ) );
		assertFalse( ObjectUtil.areEqual( null, new Object() ) );
		assertFalse( ObjectUtil.areEqual( new Object(), null ) );
		assertFalse( ObjectUtil.areEqual( new Object(), new Object() ) );
		assertFalse( ObjectUtil.areEqual( "test1", "test2" ) );
	}
}
