package com.parallelsymmetry.utility;

import junit.framework.TestCase;

import org.junit.Test;

import com.parallelsymmetry.utility.ObjectUtil;

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

	@Test
	public void testCompare() throws Exception {
		assertEquals( 0, ObjectUtil.compare( (String)null, (String)null ) );
		assertEquals( 1, ObjectUtil.compare( "", null ) );
		assertEquals( -1, ObjectUtil.compare( null, "" ) );
		assertEquals( 0, ObjectUtil.compare( "", "" ) );
	}

}