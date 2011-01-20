package com.parallelsymmetry.escape.utility.setting;

import com.parallelsymmetry.escape.utility.Parameters;

public class ParametersSettingProvider implements SettingProvider {

	private Parameters parameters;

	private String path;

	public ParametersSettingProvider( Parameters parameters ) {
		this( null, parameters );
	}

	public ParametersSettingProvider( String path, Parameters parameters ) {
		this.parameters = parameters;
		this.path = path;
	}

	@Override
	public String get( String path ) {
		if( parameters == null ) return null;

		String name = path;
		if( this.path != null && name.startsWith( this.path ) ) name = name.substring( this.path.length() );
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
