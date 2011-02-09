package com.parallelsymmetry.escape.utility.setting;

public interface SettingProvider {

	String get( String path );

	boolean nodeExists( String path );

}
