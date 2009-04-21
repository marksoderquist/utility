package com.parallelsymmetry.util;

import junit.framework.TestCase;

public class ClassUtilTest extends TestCase {

	public void testGetClassNameOnly() {
		assertEquals( "Object", ClassUtil.getClassNameOnly( Object.class ) );
	}

}
