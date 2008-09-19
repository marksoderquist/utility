package com.parallelsymmetry.util;

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

}
