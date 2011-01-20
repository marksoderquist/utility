package com.parallelsymmetry.escape.utility.setting;

import com.parallelsymmetry.escape.utility.Descriptor;

public class DescriptorSettingProvider implements SettingProvider {
	
	private Descriptor descriptor;
	
	public DescriptorSettingProvider(Descriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public boolean isWritable() {
		return false;
	}

	@Override
	public String get( String path ) {
		return descriptor.getValue( path );
	}

	@Override
	public void put( String path, String value ) {}

}
