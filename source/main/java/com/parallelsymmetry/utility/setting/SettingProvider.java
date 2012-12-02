package com.parallelsymmetry.utility.setting;

import java.util.Set;

public interface SettingProvider {

	String get( String path );
	
	Set<String> getKeys( String path );

	Set<String> getChildNames( String path );

	boolean nodeExists( String path );

}
