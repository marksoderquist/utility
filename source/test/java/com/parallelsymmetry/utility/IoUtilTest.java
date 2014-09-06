package com.parallelsymmetry.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

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
		output.close();

		assertEquals( content, IoUtil.load( new ByteArrayInputStream( output.toByteArray() ) ) );
	}

	public void testSaveAndLoadWithEncoding() throws Exception {
		String content = "This is test content to test save and load methods.";

		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IoUtil.save( content, output, TextUtil.DEFAULT_ENCODING );

		assertEquals( content, IoUtil.load( new ByteArrayInputStream( output.toByteArray() ), TextUtil.DEFAULT_ENCODING ) );
	}

	public void testLoadAsLines() throws Exception {
		String content = "This\nis\ntest\ncontent\n.";

		List<String> lines = IoUtil.loadAsLines( new ByteArrayInputStream( content.getBytes( TextUtil.DEFAULT_CHARSET ) ), TextUtil.DEFAULT_ENCODING );

		int index = 0;
		assertEquals( "This", lines.get( index++ ) );
		assertEquals( "is", lines.get( index++ ) );
		assertEquals( "test", lines.get( index++ ) );
		assertEquals( "content", lines.get( index++ ) );
		assertEquals( ".", lines.get( index++ ) );
		assertEquals( 5, lines.size() );
	}

	public void testLoadAsLineArray() throws Exception {
		String content = "This\nis\ntest\ncontent\n.";

		String[] lines = IoUtil.loadAsLineArray( new ByteArrayInputStream( content.getBytes( TextUtil.DEFAULT_CHARSET ) ), TextUtil.DEFAULT_ENCODING );

		int index = 0;
		assertEquals( "This", lines[index++] );
		assertEquals( "is", lines[index++] );
		assertEquals( "test", lines[index++] );
		assertEquals( "content", lines[index++] );
		assertEquals( ".", lines[index++] );
		assertEquals( 5, lines.length );
	}

}
