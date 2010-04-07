package com.parallelsymmetry.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import org.junit.Test;
import org.w3c.dom.Document;

public class XmlUtilTest extends TestCase {

	@Test
	public void testLoadXmlDocument() throws Exception {
		InputStream input = XmlUtilTest.class.getResourceAsStream( "/test.xml" );
		Document document = XmlUtil.loadXmlDocument( input );
		assertNotNull( document );
	}

	@Test
	public void testLoadXmlDocumentWithNullStream() throws Exception {
		Document document = XmlUtil.loadXmlDocument( null );
		assertNull( document );
	}

	@Test
	public void testFormat() throws Exception {
		String data = "<tag><indent/></tag>";
		String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tag>\n  <indent/>\n</tag>";
		ByteArrayInputStream input = new ByteArrayInputStream( data.getBytes() );
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		XmlUtil.format( input, output );
		assertEquals( test, output.toString().replace( "\r\n", "\n" ).trim() );
	}

	@Test
	public void testFormatWithIndentSize() throws Exception {
		String data = "<tag><indent/></tag>";
		String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tag>\n   <indent/>\n</tag>";
		ByteArrayInputStream input = new ByteArrayInputStream( data.getBytes() );
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		XmlUtil.format( input, output, 3 );
		assertEquals( test, output.toString().replace( "\r\n", "\n" ).trim() );
	}

}
