package com.parallelsymmetry.utility.setting;

import java.util.prefs.Preferences;

public class PreferencesSettingProviderStandardTest extends SettingProviderStandardTest {

	@Override
	public void setUp() throws Exception {
		Preferences preferences = Preferences.userNodeForPackage( getClass() );

		for( String name : preferences.childrenNames() ) {
			preferences.node( name ).removeNode();
		}
		preferences.clear();

		preferences.put( "key1", "value1" );
		preferences.put( "key2", "value2" );
		preferences.put( "key3", "value3" );

		Preferences path = preferences.node( "path" );
		path.put( "subkey1", "subvalue1" );
		path.put( "subkey2", "subvalue2" );
		path.put( "subkey3", "subvalue3" );

		provider = new PreferencesSettingProvider( preferences );
	}

}
