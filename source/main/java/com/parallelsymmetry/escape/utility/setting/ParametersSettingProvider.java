package com.parallelsymmetry.escape.utility.setting;

import com.parallelsymmetry.escape.utility.Parameters;

public class ParametersSettingProvider implements SettingProvider {

	private Parameters parameters;

	public ParametersSettingProvider( Parameters parameters ) {
		this.parameters = parameters;
	}

	@Override
	public String get( String path ) {
		if( parameters == null ) return null;

		String name = path.replace( '/', '.' ).substring( 1 );

		return parameters.get( name );
	}

	@Override
	public void put( String path, String value ) {}

	@Override
	public boolean isWritable() {
		return false;
	}

}
