package com.parallelsymmetry.escape.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

public class IoUtilTest extends TestCase {

	public void testSaveAndLoad() throws Exception {
		String content = "This is test content to test save and load methods.";

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IoUtil.save( content, output );
		assertEquals( content, IoUtil.load( new ByteArrayInputStream( output.toByteArray() ) ) );
	}

}
