package com.parallelsymmetry.utility.setting;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A SettingProvider that starts with non-changeable base SettingProvider and
 * allows other SettingProviders to be added and removed. This is the
 * implementation of the Service default setting provider to allow for modules
 * to add their default settings.
 * <p>
 * Values from the base setting provider are always used if they exist. If not,
 * the other setting providers are check in undefined order. Care should be
 * given to not have path name collisions as this could give undefined behavior.
 * 
 * @author mvsoder
 */
public class BaseSettingProvider implements SettingProvider {

	private SettingProvider provider;

	private Set<SettingProvider> providers;

	public BaseSettingProvider( SettingProvider provider ) {
		this.provider = provider;
		providers = new CopyOnWriteArraySet<SettingProvider>();
	}

	@Override
	public String get( String path ) {
		String value = null;

		if( value == null ) value = provider.get( path );

		if( value == null ) {
			for( SettingProvider provider : providers ) {
				value = provider.get( path );
				if( value != null ) break;
			}
		}

		return value;
	}

	@Override
	public Set<String> getKeys( String path ) {
		Set<String> keys = new HashSet<String>();

		keys.addAll( provider.getKeys( path ) );
		for( SettingProvider provider : providers ) {
			keys.addAll( provider.getKeys( path ) );
		}

		return keys;
	}

	@Override
	public Set<String> getChildNames( String path ) {
		Set<String> names = new HashSet<String>();

		names.addAll( provider.getChildNames( path ) );
		for( SettingProvider provider : providers ) {
			names.addAll( provider.getChildNames( path ) );
		}

		return names;
	}

	@Override
	public boolean nodeExists( String path ) {
		boolean exists = false;

		if( exists == false ) exists = provider.nodeExists( path );

		if( exists == false ) {
			for( SettingProvider provider : providers ) {
				exists = provider.nodeExists( path );
				if( exists ) break;
			}
		}

		return exists;
	}

	public void addProvider( SettingProvider provider ) {
		providers.add( provider );
	}

	public void removeProvider( SettingProvider provider ) {
		providers.remove( provider );
	}

}
