package com.parallelsymmetry.utility.setting;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parallelsymmetry.utility.FileUtil;
import com.parallelsymmetry.utility.TextUtil;

public class PersistentMapSettingProviderStandardTest extends SettingProviderStandardTest {

	private File folder = new File( "target/test/java" );

	private Map<String, String> map;

	@Override
	public void setUp() {
		map = new HashMap<String, String>();
		map.put( "/key1", "value1" );
		map.put( "/key2", "value2" );
		map.put( "/key3", "value3" );
		map.put( "/path/subkey1", "subvalue1" );
		map.put( "/path/subkey2", "subvalue2" );
		map.put( "/path/subkey3", "subvalue3" );
		provider = new PersistentMapSettingProvider( map, new File( folder, "persistent.map.settings" ) );
	}

	public void testSync() throws Exception {
		// Define the settings file.
		File syncFile = new File( folder, "persistent.map.sync.settings" );
	
		// Cleanup a previous test if it exists.
		if( syncFile.exists() ) assertTrue( syncFile.delete() );
	
		// Create an existing file.
		FileUtil.save( "/key1=value1\n/key2=value2\n/key3=value3\n/path/subkey1=subvalue1\n/path/subkey2=subvalue2\n/path/subkey3=subvalue3\n", syncFile, TextUtil.DEFAULT_ENCODING );
		assertTrue( syncFile.exists() );
	
		PersistentMapSettingProvider provider = new PersistentMapSettingProvider( syncFile );
		provider.sync( "/" );
	
		Map<String, String> map = provider.getStore();
	
		assertEquals( 6, map.size() );
		assertEquals( "value1", provider.get( "/key1" ) );
		assertEquals( "value2", provider.get( "/key2" ) );
		assertEquals( "value3", provider.get( "/key3" ) );
		assertEquals( "subvalue1", provider.get( "/path/subkey1" ) );
		assertEquals( "subvalue2", provider.get( "/path/subkey2" ) );
		assertEquals( "subvalue3", provider.get( "/path/subkey3" ) );
	}

	public void testFlush() throws Exception {
		// Define the settings file.
		File flushFile = new File( folder, "persistent.map.flush.settings" );

		// Cleanup a previous test if it exists.
		if( flushFile.exists() ) assertTrue( flushFile.delete() );

		// Flush the settings.
		PersistentMapSettingProvider provider = new PersistentMapSettingProvider( map, flushFile );
		provider.flush( "/" );
		assertTrue( flushFile.exists() );

		// Check the resulting file.
		List<String> lines = FileUtil.loadAsLines( flushFile, TextUtil.DEFAULT_ENCODING );
		assertEquals( 6, lines.size() );

		assertTrue( lines.contains( "/key1=value1" ) );
		assertTrue( lines.contains( "/key2=value2" ) );
		assertTrue( lines.contains( "/key3=value3" ) );
		assertTrue( lines.contains( "/path/subkey1=subvalue1" ) );
		assertTrue( lines.contains( "/path/subkey2=subvalue2" ) );
		assertTrue( lines.contains( "/path/subkey3=subvalue3" ) );
	}

	public void testFlushPartial() throws Exception {
		// Define the settings file.
		File flushFile = new File( folder, "persistent.map.flush.settings" );

		// Cleanup a previous test if it exists.
		if( flushFile.exists() ) assertTrue( flushFile.delete() );

		// Flush the settings.
		PersistentMapSettingProvider provider = new PersistentMapSettingProvider( map, flushFile );
		provider.flush( "/path" );
		assertTrue( flushFile.exists() );

		// Check the resulting file.
		List<String> lines = FileUtil.loadAsLines( flushFile, TextUtil.DEFAULT_ENCODING );
		assertEquals( 3, lines.size() );

		assertTrue( lines.contains( "/path/subkey1=subvalue1" ) );
		assertTrue( lines.contains( "/path/subkey2=subvalue2" ) );
		assertTrue( lines.contains( "/path/subkey3=subvalue3" ) );
	}

	public void testFlushPartialWithExisting() throws Exception {
		// Define the settings file.
		File flushFile = new File( folder, "persistent.map.flush.settings" );

		// Cleanup a previous test if it exists.
		if( flushFile.exists() ) assertTrue( flushFile.delete() );

		// Create an existing file.
		FileUtil.save( "/key1=value1\n/key2=value2\n/key3=value3\n", flushFile, TextUtil.DEFAULT_ENCODING );
		assertTrue( flushFile.exists() );

		// Flush the settings.
		PersistentMapSettingProvider provider = new PersistentMapSettingProvider( map, flushFile );
		provider.flush( "/path" );
		assertTrue( flushFile.exists() );

		// Check the resulting file.
		List<String> lines = FileUtil.loadAsLines( flushFile, TextUtil.DEFAULT_ENCODING );
		assertEquals( 6, lines.size() );

		assertTrue( lines.contains( "/key1=value1" ) );
		assertTrue( lines.contains( "/key2=value2" ) );
		assertTrue( lines.contains( "/key3=value3" ) );
		assertTrue( lines.contains( "/path/subkey1=subvalue1" ) );
		assertTrue( lines.contains( "/path/subkey2=subvalue2" ) );
		assertTrue( lines.contains( "/path/subkey3=subvalue3" ) );
	}

}
