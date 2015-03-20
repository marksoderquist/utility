package com.parallelsymmetry.utility.setting;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;

import com.parallelsymmetry.utility.Descriptor;

/**
 * @deprecated This class is deprecated because it can cause anomalous effects
 *             when there are values and sub elements in the same element.
 */
@Deprecated
public class DescriptorSettingsProvider implements SettingsProvider {

	private Descriptor descriptor;

	private String root = "/";

	public DescriptorSettingsProvider( Descriptor descriptor ) {
		this( descriptor, true );
	}

	public DescriptorSettingsProvider( Descriptor descriptor, boolean skipRoot ) {
		init( descriptor, skipRoot );
	}

	public DescriptorSettingsProvider( InputStream input ) {
		this( input, true );
	}

	public DescriptorSettingsProvider( InputStream input, boolean skipRoot ) {
		Descriptor descriptor = null;
		try {
			descriptor = new Descriptor( input );
		} catch( IOException exception ) {
			throw new RuntimeException( exception );
		}
		init( descriptor, skipRoot );
	}

	private void init( Descriptor descriptor, boolean skipRoot ) {
		this.descriptor = descriptor;
		if( skipRoot ) root = "/" + descriptor.getDocument().getDocumentElement().getNodeName();
	}

	@Override
	public String get( String path ) {
		// Incoming paths should always be absolute.
		if( path.endsWith( "/" ) ) path = path.substring( 0, path.length() - 1 );
		String value = descriptor.getValue( root + path );

		// Check attributes.
		if( value == null ) {
			Node node = descriptor.getNode( getParentPath( root + path ) );
			if( node != null ) value = Descriptor.getAttribute( node, getFile( path ) );
		}

		return value;
	}

	@Override
	public Set<String> getKeys( String path ) {
		Set<String> keys = new HashSet<String>();
		if( descriptor == null ) return keys;
		if( !nodeExists( path ) ) return keys;

		// Incoming paths should always be absolute.
		if( path.endsWith( "/" ) ) path = path.substring( 0, path.length() - 1 );

		for( String name : descriptor.getAttributeNames( root + path ) ) {
			keys.add( name );
		}

		for( String name : descriptor.getNames( root + path ) ) {
			Node node = descriptor.getNode( root + path + "/" + name );
			if( node != null && isKey( node ) ) keys.add( name );
		}

		return keys;
	}

	@Override
	public Set<String> getChildNames( String path ) {
		Set<String> names = new HashSet<String>();
		if( descriptor == null ) return names;
		if( !nodeExists( path ) ) return names;

		// Incoming paths should always be absolute.
		if( path.endsWith( "/" ) ) path = path.substring( 0, path.length() - 1 );

		for( String name : descriptor.getNames( root + path ) ) {
			Node node = descriptor.getNode( root + path + "/" + name );
			if( node != null && !isKey( node ) ) names.add( name );
		}

		return names;
	}

	@Override
	public boolean nodeExists( String path ) {
		// Incoming paths should always be absolute.
		if( path.endsWith( "/" ) ) path = path.substring( 0, path.length() - 1 );
		Node node = descriptor.getNode( root + path );
		return node != null && !isKey( node );
	}

	private String getParentPath( String path ) {
		if( path == null ) return null;
		if( path.endsWith( "/" ) ) path = path.substring( 0, path.length() - 1 );
		int index = path.lastIndexOf( "/" );
		return index < 0 ? null : path.substring( 0, index );
	}

	private String getFile( String path ) {
		if( path == null ) return null;
		if( path.endsWith( "/" ) ) path = path.substring( 0, path.length() - 1 );
		int index = path.lastIndexOf( "/" );
		return index < 0 ? null : path.substring( index + 1 );
	}

	private boolean isKey( Node node ) {
		return node.getChildNodes().getLength() == 1 && node.getFirstChild().getNodeType() == Node.TEXT_NODE;
	}

}
