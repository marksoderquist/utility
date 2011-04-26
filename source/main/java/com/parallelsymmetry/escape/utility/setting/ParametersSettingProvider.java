package com.parallelsymmetry.escape.utility.setting;

import java.util.HashSet;
import java.util.Set;

import com.parallelsymmetry.escape.utility.Parameters;

public class ParametersSettingProvider implements SettingProvider {

	private Parameters parameters;

	public ParametersSettingProvider( Parameters parameters ) {
		this.parameters = parameters;
	}

	@Override
	public String get( String path ) {
		// Incoming paths should always be absolute.
		if( parameters == null ) return null;

		return parameters.get( getName( path ) );
	}

	@Override
	public Set<String> getChildNames( String path ) {
		// Incoming paths should always be absolute.
		String node = getName( path + "/" );
		Set<String> names = new HashSet<String>();

		for( String name : parameters.getNames() ) {
			if( name.startsWith( node ) ) {
				int index = name.indexOf( ".", node.length() );
				if( index > 0 ) names.add( name.substring( node.length(), index ) );
			}
		}

		return names;
	}

	@Override
	public boolean nodeExists( String path ) {
		// Incoming paths should always be absolute.
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
