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

	private Document descriptor;

	private List<String> paths;

	public Descriptor( InputStream input ) throws SAXException, IOException, ParserConfigurationException {
		descriptor = XmlUtil.loadXmlDocument( input );
	}

	public List<String> getPaths() {
		if( paths == null ) paths = listPaths( descriptor );
		return paths;
	}

	public String getValue( String path ) {
		if( path == null ) return null;

		String value = null;

		try {
			value = (String)xpath.evaluate( path, descriptor, XPathConstants.STRING );
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

	private List<String> listPaths( Node parent ) {
		NodeList list = parent.getChildNodes();
		List<String> paths = new ArrayList<String>();

		Node node = null;
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
		while( parent != descriptor ) {
			builder.insert( 0, parent.getNodeName() );
			builder.insert( 0, "/" );
			parent = parent.getParentNode();
		}
		return builder.toString();
	}

}
