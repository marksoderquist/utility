package com.parallelsymmetry.utility.setting;

import java.util.HashSet;
import java.util.Set;

import com.parallelsymmetry.utility.Parameters;

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
	public Set<String> getKeys( String path ) {
		Set<String> keys = new HashSet<String>();
		if( parameters == null ) return keys;
		if( !nodeExists( path ) ) return keys;

		// Incoming paths should always be absolute.
		if( !path.endsWith( "/" ) ) path += "/";

		String node = getName( path );

		for( String name : parameters.getFlags() ) {
			name = removePrefix( name );
			if( name.startsWith( node ) && name.indexOf( ".", node.length() ) < 0 ) {
				keys.add( name.substring( node.length() ) );
			}
		}

		return keys;
	}

	@Override
	public Set<String> getChildNames( String path ) {
		Set<String> names = new HashSet<String>();
		if( parameters == null ) return names;
		if( !nodeExists( path ) ) return names;

		// Incoming paths should always be absolute.
		if( !path.endsWith( "/" ) ) path += "/";

		String node = getName( path );

		for( String name : parameters.getFlags() ) {
			name = removePrefix( name );
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
		if( parameters == null ) return false;
		if( !path.endsWith( "/" ) ) path += "/";

		String node = getName( path );

		Set<String> names = parameters.getFlags();
		for( String name : names ) {
			name = removePrefix( name );
			if( name.startsWith( node ) ) return true;
		}

		return false;
	}

	private String getName( String path ) {
		return path.replace( '/', '.' ).substring( 1 );
	}

	private static String removePrefix( String flag ) {
		if( flag.startsWith( "--" ) ) {
			return flag.substring( "--".length() );
		} else if( flag.startsWith( "-" ) ) {
			return flag.substring( "-".length() );
		}
		return flag;
	}

}
