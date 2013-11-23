package com.parallelsymmetry.utility;

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
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.parallelsymmetry.utility.log.Log;

public class XmlUtil {

	private static final int DEFAULT_INDENT = 2;

	public static final Document loadXmlDocument( File file ) throws SAXException, IOException {
		return loadXmlDocument( new InputStreamReader( new FileInputStream( file ), TextUtil.DEFAULT_ENCODING ) );
	}

	public static final Document loadXmlDocument( String uri ) throws SAXException, IOException {
		if( uri == null ) return null;

		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			document = factory.newDocumentBuilder().parse( uri );
			document.getDocumentElement().normalize();
		} catch( ParserConfigurationException exception ) {
			// Intentionally ignore exception.
		}

		return document;
	}

	public static final Document loadXmlDocument( Reader reader ) throws SAXException, IOException {
		if( reader == null ) return null;

		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			document = factory.newDocumentBuilder().parse( new InputSource( reader ) );
			document.getDocumentElement().normalize();
		} catch( ParserConfigurationException exception ) {
			// Intentionally ignore exception.
		}

		return document;
	}

	public static final Document loadXmlDocument( InputStream stream ) throws SAXException, IOException {
		if( stream == null ) return null;

		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			document = factory.newDocumentBuilder().parse( new InputSource( stream ) );
			document.getDocumentElement().normalize();
		} catch( ParserConfigurationException exception ) {
			// Intentionally ignore exception.
		}

		return document;
	}

	public static final Node getNode( Node node, String path ) {
		if( node == null || TextUtil.isEmpty( path ) ) return null;

		Node value = null;
		XPath xpath = XPathFactory.newInstance().newXPath();

		try {
			value = (Node)xpath.evaluate( path, node, XPathConstants.NODE );
		} catch( XPathExpressionException exception ) {
			Log.write( new Exception( path, exception ) );
		}

		return value;
	}

	public static final void save( Node node, File file ) throws IOException {
		Writer writer = new OutputStreamWriter( new BufferedOutputStream( new FileOutputStream( file ) ), "UTF-8" );
		try {
			save( node, writer );
		} finally {
			if( writer != null ) writer.close();
		}
	}

	public static final void save( Node node, Writer output ) throws IOException {
		save( node, output, DEFAULT_INDENT );
	}

	public static final void save( Node node, Writer output, int indentAmount ) throws IOException {
		format( new DOMSource( node ), new StreamResult( output ), indentAmount );
	}

	public static final void format( InputStream input, OutputStream output ) throws IOException {
		format( input, output, DEFAULT_INDENT );
	}

	public static final void format( InputStream input, OutputStream output, int indent ) throws IOException {
		format( new StreamSource( input ), new StreamResult( output ), indent );
	}

	public static final void format( Reader reader, Writer writer ) throws IOException {
		format( reader, writer, DEFAULT_INDENT );
	}

	public static final void format( Reader reader, Writer writer, int indent ) throws IOException {
		format( new StreamSource( reader ), new StreamResult( writer ), indent );
	}

	public static final String toString( Node node ) {
		StringWriter output = new StringWriter();
		try {
			save( node, output );
		} catch( IOException exception ) {
			Log.write( exception );
		}
		return output.toString();
	}

	public static final String getPath( Node node ) {
		if( node == null ) return null;

		StringBuilder builder = new StringBuilder();
		while( node.getNodeType() != Node.DOCUMENT_NODE ) {
			builder.insert( 0, node.getNodeName() );
			builder.insert( 0, "/" );
			node = node.getParentNode();
		}

		return builder.toString();
	}

	private static final void format( Source source, Result result, int indent ) throws IOException {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" );
			transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
			transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
			transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
			transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", String.valueOf( indent ) );
			transformer.transform( source, result );
		} catch( TransformerException exception ) {
			throw new IOException( exception );
		}
	}

}
