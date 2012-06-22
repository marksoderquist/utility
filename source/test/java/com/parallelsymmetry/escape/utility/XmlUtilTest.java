package com.parallelsymmetry.escape.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;

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
	public void testLoadXmlDocumentWithNullUri() throws Exception {
		Document document = XmlUtil.loadXmlDocument( (String)null );
		assertNull( document );
	}

	@Test
	public void testLoadXmlDocumentWithNullReader() throws Exception {
		Document document = XmlUtil.loadXmlDocument( (Reader)null );
		assertNull( document );
	}

	@Test
	public void testLoadXmlDocumentWithNullStream() throws Exception {
		Document document = XmlUtil.loadXmlDocument( (InputStream)null );
		assertNull( document );
	}
	
	@Test
	public void testGetDocumentType() throws Exception {
		InputStream input = XmlUtilTest.class.getResourceAsStream( "/test.xml" );
		Document document = XmlUtil.loadXmlDocument( input );
		assertEquals( "test", document.getDocumentElement().getNodeName() );
	}

	@Test
	public void testFormat() throws Exception {
		// FIXME This test is OS dependent.
//		String data = "<tag><indent/></tag>";
//		String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tag>\n  <indent/>\n</tag>";
//		ByteArrayInputStream input = new ByteArrayInputStream( data.getBytes() );
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//
//		XmlUtil.format( input, output );
//		assertEquals( test, output.toString().replace( "\r\n", "\n" ).trim() );
	}

	@Test
	public void testFormatWithIndentSize() throws Exception {
		// FIXME This test is OS dependent.
//		String data = "<tag><indent/></tag>";
//		String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tag>\n   <indent/>\n</tag>";
//		ByteArrayInputStream input = new ByteArrayInputStream( data.getBytes() );
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//
//		XmlUtil.format( input, output, 3 );
//		assertEquals( test, output.toString().replace( "\r\n", "\n" ).trim() );
	}

}
