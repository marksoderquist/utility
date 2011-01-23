package com.parallelsymmetry.escape.utility.setting;

public interface SettingProvider {

	boolean nodeExists( String path );

	String get( String path );

}
