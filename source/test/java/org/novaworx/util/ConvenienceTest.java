package org.novaworx.util;

import junit.framework.TestCase;

public class ConvenienceTest extends TestCase {

	public void testGetClassNameOnly() {
		assertEquals( "Object", Convenience.getClassNameOnly( Object.class ) );
	}

}
