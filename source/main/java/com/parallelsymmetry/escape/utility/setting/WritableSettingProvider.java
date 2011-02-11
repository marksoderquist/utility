package com.parallelsymmetry.escape.utility.setting;

public interface WritableSettingProvider extends SettingProvider {

	void put( String path, String value );

	void removeNode( String path ) throws SettingsStoreException;

	void flush( String path ) throws SettingsStoreException;

	void sync( String path ) throws SettingsStoreException;

}
