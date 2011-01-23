package com.parallelsymmetry.escape.utility.setting;

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
	public boolean nodeExists( String path ) {
		return descriptor.getNode( root + path ) != null;
	}

}
