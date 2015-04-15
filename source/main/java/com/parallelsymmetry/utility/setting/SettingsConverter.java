package com.parallelsymmetry.utility.setting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.parallelsymmetry.utility.Descriptor;

public class SettingsConverter {

	public void convert( String descriptorPath ) {
		try {
			Descriptor descriptor = new Descriptor( getClass().getResourceAsStream( descriptorPath ) );
			DescriptorSettingsProvider provider = new DescriptorSettingsProvider( descriptor );

			List<String> paths = new ArrayList<String>( getPaths( provider ) );
			Collections.sort( paths );
			for( String path : paths ) {
				String value = provider.get( path );
				System.out.println( path + "=" + value );
			}

		} catch( Throwable throwable ) {
			throwable.printStackTrace( System.err );
		}
	}

	private Set<String> getPaths( SettingsProvider provider ) {
		return getPaths( provider, "/" );
	}

	private Set<String> getPaths( SettingsProvider provider, String path ) {
		Set<String> paths = new HashSet<String>();

		for( String key : provider.getKeys( path ) ) {
			if( "/".equals( path ) ) {
				paths.add( "/" + key );
			} else {
				paths.add( path + "/" + key );
			}
		}
		for( String child : provider.getChildNames( path ) ) {
			if( "/".equals( path ) ) {
				paths.addAll( getPaths( provider, "/" + child ) );
			} else {
				paths.addAll( getPaths( provider, path + "/" + child ) );
			}
		}

		return paths;
	}

}
