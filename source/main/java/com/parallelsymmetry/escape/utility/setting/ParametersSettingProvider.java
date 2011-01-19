package com.parallelsymmetry.escape.utility.setting;

import com.parallelsymmetry.escape.utility.Parameters;

public class ParametersSettingProvider implements SettingProvider {

	private Parameters parameters;

	private String prefix;

	public ParametersSettingProvider( Parameters parameters ) {
		this( null, parameters );
	}

	public ParametersSettingProvider( String prefix, Parameters parameters ) {
		this.parameters = parameters;
		this.prefix = prefix;
	}

	@Override
	public String get( String path ) {
		if( parameters == null ) return null;

		String name = prefix == null ? path : prefix + path; 
		name = name.replace( '/', '.' ).substring( 1 );

		return parameters.get( name );
	}

	@Override
	public void put( String path, String value ) {}

	@Override
	public boolean isWritable() {
		return false;
	}

}
