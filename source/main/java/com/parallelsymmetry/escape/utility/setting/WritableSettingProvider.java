package com.parallelsymmetry.escape.utility.setting;

public interface WritableSettingProvider extends SettingProvider {

	void put( String path, String value );

	void removeNode( String path );

	void renameNode( String oldPath, String newPath );

}
