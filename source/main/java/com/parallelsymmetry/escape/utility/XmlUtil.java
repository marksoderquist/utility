package com.parallelsymmetry.escape.utility;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtil {

	private static final int DEFAULT_INDENT = 2;

	public static final Document loadXmlDocument( File file ) throws SAXException, IOException, ParserConfigurationException {
		return loadXmlDocument( new InputStreamReader( new FileInputStream( file ), TextUtil.DEFAULT_ENCODING ) );
	}

	public static final Document loadXmlDocument( String uri ) throws SAXException, IOException, ParserConfigurationException {
		if( uri == null ) return null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = factory.newDocumentBuilder().parse( uri );
		document.getDocumentElement().normalize();
		return document;
	}

	public static final Document loadXmlDocument( Reader reader ) throws SAXException, IOException, ParserConfigurationException {
		if( reader == null ) return null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = factory.newDocumentBuilder().parse( new InputSource( reader ) );
		document.getDocumentElement().normalize();
		return document;
	}

	public static final Document loadXmlDocument( InputStream stream ) throws SAXException, IOException, ParserConfigurationException {
		if( stream == null ) return null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = factory.newDocumentBuilder().parse( new InputSource( stream ) );
		document.getDocumentElement().normalize();
		return document;
	}

	public static final void save( Document document, File file ) throws IOException {
		Writer writer = new OutputStreamWriter( new BufferedOutputStream( new FileOutputStream( file ) ), "UTF-8" );
		try {
			save( document, writer );
		} finally {
			if( writer != null ) writer.close();
		}
	}

	public static final void save( Document document, Writer output ) throws IOException {
		save( document, output, DEFAULT_INDENT );
	}

	public static final void save( Document document, Writer output, int indentAmount ) throws IOException {
		Transformer transformer;
		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			transformer = factory.newTransformer();
			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
			transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", String.valueOf( indentAmount ) );
			transformer.transform( new DOMSource( document ), new StreamResult( output ) );
		} catch( TransformerException exception ) {
			throw new IOException( exception );
		}
	}

	public static final void format( InputStream input, OutputStream output ) throws IOException {
		format( input, output, DEFAULT_INDENT );
	}

	public static final void format( InputStream input, OutputStream output, int indentAmount ) throws IOException {
		Transformer transformer;
		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			transformer = factory.newTransformer();
			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
			transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", String.valueOf( indentAmount ) );
			transformer.transform( new StreamSource( input ), new StreamResult( output ) );
		} catch( TransformerException exception ) {
			throw new IOException( exception );
		}
	}

}
