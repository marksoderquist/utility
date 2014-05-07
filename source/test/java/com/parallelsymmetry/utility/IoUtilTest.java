package com.parallelsymmetry.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;

public class IoUtilTest extends TestCase {

	public void testCopyWithBytes() throws Exception {
		String content = "This is test content to test save and load methods.";
		byte[] sourceData = content.getBytes( TextUtil.DEFAULT_CHARSET );

		ByteArrayInputStream input = new ByteArrayInputStream( sourceData );
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		long count = IoUtil.copy( input, output );

		byte[] targetData = output.toByteArray();

		assertEquals( sourceData.length, count );
		assertEquals( sourceData.length, targetData.length );
		for( int index = 0; index < sourceData.length; index++ ) {
			assertEquals( sourceData[index], targetData[index] );
		}
	}

	public void testCopyWithChars() throws Exception {
		String content = "This is test content to test save and load methods.";

		StringReader reader = new StringReader( content );
		StringWriter writer = new StringWriter();

		long count = IoUtil.copy( reader, writer );

		assertEquals( content.length(), count );
		assertEquals( content, writer.toString() );
	}

	public void testSaveAndLoad() throws Exception {
		String content = "This is test content to test save and load methods.";

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IoUtil.save( content, output );

		assertEquals( content, IoUtil.load( new ByteArrayInputStream( output.toByteArray() ) ) );
	}

	public void testSaveAndLoadWithEncoding() throws Exception {
		String content = "This is test content to test save and load methods.";

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IoUtil.save( content, output, TextUtil.DEFAULT_ENCODING );

		assertEquals( content, IoUtil.load( new ByteArrayInputStream( output.toByteArray() ), TextUtil.DEFAULT_ENCODING ) );
	}

}
