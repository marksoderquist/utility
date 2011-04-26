package com.parallelsymmetry.escape.utility.setting;

import java.util.HashSet;
import java.util.Set;

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
		return descriptor.getValue( root + path );
	}

	@Override
	public Set<String> getNames( String path ) {
		Set<String> names = new HashSet<String>();

		names.addAll( descriptor.getNames( root + path ) );

		return names;
	}

	@Override
	public boolean nodeExists( String path ) {
		return descriptor.getNode( root + path ) != null;
	}

}
