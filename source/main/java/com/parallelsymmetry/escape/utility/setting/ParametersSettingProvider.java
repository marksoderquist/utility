package com.parallelsymmetry.escape.utility.setting;

import java.util.Set;

import com.parallelsymmetry.escape.utility.Parameters;

public class ParametersSettingProvider implements SettingProvider {

	private Parameters parameters;

	public ParametersSettingProvider( Parameters parameters ) {
		this.parameters = parameters;
	}

	@Override
	public String get( String path ) {
		if( parameters == null ) return null;

		return parameters.get( getName( path ) );
	}

	@Override
	public boolean nodeExists( String path ) {
		String node = getName( path + "/" );

		Set<String> names = parameters.getNames();
		for( String name : names ) {
			if( name.startsWith( node ) ) return true;
		}

		return false;
	}

	private String getName( String path ) {
		return path.replace( '/', '.' ).substring( 1 );
	}

}
