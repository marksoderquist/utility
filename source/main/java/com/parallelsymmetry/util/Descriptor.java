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

import com.parallelsymmetry.log.Log;

public class Descriptor {

	private Node node;

	private List<String> paths;

	public Descriptor() throws SAXException, IOException, ParserConfigurationException {}

	public Descriptor( InputStream input ) throws SAXException, IOException, ParserConfigurationException {
		if( input == null ) return;
		node = XmlUtil.loadXmlDocument( input );
	}

	public Descriptor( Node node ) {
		this.node = node;
	}

	public Document getDocument() {
		return ( node instanceof Document ) ? (Document)node : node.getOwnerDocument();
	}

	public Node getNode() {
		return node;
	}

	public List<String> getPaths() {
		if( paths == null ) paths = listPaths( node );
		return paths;
	}

	public Node getNode( String path ) {
		if( node == null || TextUtil.isEmpty( path ) ) return null;

		Node value = null;
		XPath xpath = XPathFactory.newInstance().newXPath();

		try {
			value = (Node)xpath.evaluate( path, node, XPathConstants.NODE );
		} catch( XPathExpressionException exception ) {
			Log.write( exception );
		}

		return value;
	}

	public Node[] getNodes( String path ) {
		if( node == null || TextUtil.isEmpty( path ) ) return null;

		NodeList nodes = null;
		XPath xpath = XPathFactory.newInstance().newXPath();

		try {
			nodes = (NodeList)xpath.evaluate( path, node, XPathConstants.NODESET );
		} catch( XPathExpressionException exception ) {
			Log.write( exception );
		}
		if( nodes == null ) return null;

		ArrayList<Node> values = new ArrayList<Node>();
		int count = nodes.getLength();
		for( int index = 0; index < count; index++ ) {
			values.add( nodes.item( index ) );
		}

		return values.toArray( new Node[ values.size() ] );
	}

	public String getValue( String path ) {
		return getValue( this.node, path );
	}

	public String getValue( String path, String defaultValue ) {
		return getValue( this.node, path, defaultValue );
	}

	/**
	 * Get an array of all the values that have the same path.
	 * 
	 * @param path
	 * @return An array of values with the same path.
	 */
	public String[] getValues( String path ) {
		return getValues( this.node, path );
	}

	public static String getValue( Node node, String path ) {
		if( node == null || TextUtil.isEmpty( path ) ) return null;

		String value = null;
		XPath xpath = XPathFactory.newInstance().newXPath();

		try {
			value = (String)xpath.evaluate( path, node, XPathConstants.STRING );
		} catch( XPathExpressionException exception ) {
			Log.write( exception );
		}

		if( TextUtil.isEmpty( value ) ) return null;

		return value;
	}

	public static String getValue( Node node, String path, String defaultValue ) {
		String value = getValue( node, path );
		if( value == null ) return defaultValue;
		return value;
	}

	/**
	 * Get an array of all the values in the node that have the same path.
	 * 
	 * @param path
	 * @return An array of values with the same path.
	 */
	public static String[] getValues( Node node, String path ) {
		if( node == null || TextUtil.isEmpty( path ) ) return null;

		NodeList nodes = null;
		XPath xpath = XPathFactory.newInstance().newXPath();

		try {
			nodes = (NodeList)xpath.evaluate( path, node, XPathConstants.NODESET );
		} catch( XPathExpressionException exception ) {
			Log.write( exception );
		}
		if( nodes == null ) return null;

		ArrayList<String> values = new ArrayList<String>();
		int count = nodes.getLength();
		for( int index = 0; index < count; index++ ) {
			Node item = nodes.item( index );
			values.add( item.getTextContent() );
		}

		return values.toArray( new String[ values.size() ] );
	}

	public static String getAttribute( Node node, String name ) {
		Node attribute = node.getAttributes().getNamedItem( name );
		return attribute == null ? null : attribute.getNodeValue();
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
		while( parent != this.node ) {
			builder.insert( 0, parent.getNodeName() );
			builder.insert( 0, "/" );
			parent = parent.getParentNode();
		}
		return builder.toString();
	}

}
