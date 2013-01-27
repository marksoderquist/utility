package com.parallelsymmetry.utility.setting;

import java.util.HashMap;
import java.util.Map;


public class MapSettingProviderStandardTest extends SettingProviderStandardTest {

	@Override
	public void setUp() {
		Map<String,String> map = new HashMap<String,String>();
		map.put( "/key1", "value1" );		
		map.put( "/key2", "value2" );		
		map.put( "/key3", "value3" );		
		map.put( "/path/subkey1", "subvalue1" );		
		map.put( "/path/subkey2", "subvalue2" );		
		map.put( "/path/subkey3", "subvalue3" );		
		
		provider = new MapSettingProvider(map);
	}
	
}
