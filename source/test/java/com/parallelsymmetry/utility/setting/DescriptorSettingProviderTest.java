package com.parallelsymmetry.utility.setting;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.parallelsymmetry.utility.Descriptor;

public class DescriptorSettingProviderTest extends SettingProviderTest {

	@Override
	public void setUp() throws Exception {
		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

		Element root = document.createElement( "settings" );
		document.appendChild( root );

		Element key1 = document.createElement( "key1" );
		key1.setTextContent( "value1" );
		Element key2 = document.createElement( "key2" );
		key2.setTextContent( "value2" );
		Element key3 = document.createElement( "key3" );
		key3.setTextContent( "value3" );

		root.appendChild( key1 );
		root.appendChild( key2 );
		root.appendChild( key3 );
		
		Element path = document.createElement( "path");
		root.appendChild( path );
		
		Element subkey1 = document.createElement( "subkey1" );
		subkey1.setTextContent( "subvalue1" );
		Element subkey2 = document.createElement( "subkey2" );
		subkey2.setTextContent( "subvalue2" );
		Element subkey3 = document.createElement( "subkey3" );
		subkey3.setTextContent( "subvalue3" );
		
		path.appendChild( subkey1 );
		path.appendChild( subkey2 );
		path.appendChild( subkey3 );

		provider = new DescriptorSettingProvider( new Descriptor( document ), true );
	}

}
