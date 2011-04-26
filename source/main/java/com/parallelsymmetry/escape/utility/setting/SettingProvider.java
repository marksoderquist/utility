package com.parallelsymmetry.escape.utility.setting;

import java.util.Set;

public interface SettingProvider {

	String get( String path );

	Set<String> getNames( String path );

	boolean nodeExists( String path );

}
