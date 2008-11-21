package com.parallelsymmetry.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlUtil {

	public static final Document loadXmlDocument( InputStream stream ) throws SAXException, IOException, ParserConfigurationException {
		if( stream == null ) return null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		return factory.newDocumentBuilder().parse( stream );
	}

	public static final void format( InputStream input, OutputStream output ) throws IOException {
		format( input, output, 2 );
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
