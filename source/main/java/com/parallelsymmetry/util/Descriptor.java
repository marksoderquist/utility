package com.parallelsymmetry.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class Descriptor {

	private XPath xpath = XPathFactory.newInstance().newXPath();

	private Document document;

	private List<String> paths;

	public Descriptor() throws SAXException, IOException, ParserConfigurationException {
		this( null );
	}

	public Descriptor( InputStream input ) throws SAXException, IOException, ParserConfigurationException {
		if( input != null ) document = XmlUtil.loadXmlDocument( input );
	}

	public Document getDocument() {
		return document;
	}

	public List<String> getPaths() {
		if( paths == null ) paths = listPaths( document );
		return paths;
	}

	public String getValue( String path ) {
		if( path == null || document == null ) return null;

		String value = null;

		try {
			value = (String)xpath.evaluate( path, document, XPathConstants.STRING );
		} catch( XPathExpressionException exception ) {
			// Intentionally ignore exception.
		}

		if( TextUtil.isEmpty( value ) ) return null;

		return value;
	}

	public String getValue( String path, String defaultValue ) {
		String value = getValue( path );
		if( value == null ) return defaultValue;
		return value;
	}

	/**
	 * Get an array of all the values that have the same path.
	 * 
	 * @param path
	 * @return An array of values with the same path.
	 */
	public String[] getValues( String path ) {
		if( path == null || document == null ) return null;

		NodeList nodes = null;

		try {
			nodes = (NodeList)xpath.evaluate( path, document, XPathConstants.NODESET );
		} catch( XPathExpressionException exception ) {
			// Intentionally ignore exception.
		}

		ArrayList<String> values = new ArrayList<String>();
		int count = nodes.getLength();
		for( int index = 0; index < count; index++ ) {
			Node node = nodes.item( index );
			values.add( node.getTextContent() );
		}

		return values.toArray( new String[values.size()] );
	}

	private List<String> listPaths( Node parent ) {
		List<String> paths = new ArrayList<String>();
		if( parent == null ) return paths;

		Node node = null;
		NodeList list = parent.getChildNodes();
		int count = list.getLength();
		for( int index = 0; index < count; index++ ) {
			node = list.item( index );
			if( node instanceof Text ) {
				if( TextUtil.isEmpty( node.getTextContent() ) ) continue;
				paths.add( getPath( node ) );
			}
			paths.addAll( listPaths( node ) );
		}

		return paths;
	}

	private String getPath( Node node ) {
		StringBuilder builder = new StringBuilder();
		Node parent = node.getParentNode();
		while( parent != document ) {
			builder.insert( 0, parent.getNodeName() );
			builder.insert( 0, "/" );
			parent = parent.getParentNode();
		}
		return builder.toString();
	}

}
