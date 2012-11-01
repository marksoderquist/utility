package com.parallelsymmetry.escape.utility.setting;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;

import com.parallelsymmetry.escape.utility.Descriptor;

public class DescriptorSettingProvider implements SettingProvider {

	private Descriptor descriptor;

	private String root = "";

	public DescriptorSettingProvider( Descriptor descriptor ) {
		this( descriptor, true );
	}

	public DescriptorSettingProvider( Descriptor descriptor, boolean skipRoot ) {
		this.descriptor = descriptor;
		if( skipRoot ) root = "/" + descriptor.getDocument().getDocumentElement().getNodeName();
	}

	@Override
	public String get( String path ) {
		// Incoming paths should always be absolute.
		String value = descriptor.getValue( root + path );

		// Check attributes.
		if( value == null ) {
			Node node = descriptor.getNode( getParentPath( root + path ) );
			if( node != null ) value = Descriptor.getAttribute( node, getFile( path ) );
		}

		return value;
	}

	@Override
	public Set<String> getChildNames( String path ) {
		// Incoming paths should always be absolute.
		return new HashSet<String>( descriptor.getNames( root + path ) );
	}

	@Override
	public boolean nodeExists( String path ) {
		// Incoming paths should always be absolute.
		return descriptor.getNode( root + path ) != null;
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

}
