package com.parallelsymmetry.utility.setting;

import java.util.ArrayList;
import java.util.List;

import com.parallelsymmetry.utility.Parameters;

public class ParametersSettingsProviderStandardTest extends SettingsProviderStandardTest {

	@Override
	public void setUp() {
		List<String> values = new ArrayList<String>();
		values.add( "-key1" );
		values.add( "value1" );
		values.add( "-key2" );
		values.add( "value2" );
		values.add( "-key3" );
		values.add( "value3" );
		values.add( "-path.subkey1" );
		values.add( "subvalue1" );
		values.add( "-path.subkey2" );
		values.add( "subvalue2" );
		values.add( "-path.subkey3" );
		values.add( "subvalue3" );

		provider = new ParametersSettingsProvider( Parameters.parse( values.toArray( new String[values.size()] ) ) );
	}

}
