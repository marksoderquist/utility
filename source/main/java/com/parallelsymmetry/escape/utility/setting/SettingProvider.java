package com.parallelsymmetry.escape.utility.setting;

public interface SettingProvider {
	
	boolean isWritable();

	String get( String path );
	
	void put( String path, String value );
	
}
