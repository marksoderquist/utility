package com.parallelsymmetry.utility.setting;

public interface WritableSettingsProvider extends SettingsProvider {

	void put( String path, String value );

	void removeNode( String path ) throws SettingsStoreException;

	void flush( String path ) throws SettingsStoreException;

	void sync( String path ) throws SettingsStoreException;

}
