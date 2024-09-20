package com.parallelsymmetry.utility.setting;

import org.junit.jupiter.api.BeforeEach;

import java.util.prefs.Preferences;

public class PreferencesSettingsProviderStandardTest extends SettingsProviderStandardTest {

	@BeforeEach
	@Override
	public void setup() throws Exception {
		super.setup();
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

		provider = new PreferencesSettingsProvider( preferences );
	}

}
