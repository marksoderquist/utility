package com.parallelsymmetry.utility.setting;

import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class SettingsProviderStandardTest extends BaseTestCase {

	protected SettingsProvider provider;

	@Test
	public void testGet() {
		assertNull( provider.get( "/invalid" ) );

		assertEquals( "value1", provider.get( "/key1" ) );
		assertEquals( "value2", provider.get( "/key2" ) );
		assertEquals( "value3", provider.get( "/key3" ) );

		assertEquals( "subvalue1", provider.get( "/path/subkey1" ) );
		assertEquals( "subvalue2", provider.get( "/path/subkey2" ) );
		assertEquals( "subvalue3", provider.get( "/path/subkey3" ) );
	}

	@Test
	public void testGetKeys() {
		assertEquals( 0, provider.getKeys( "/invalid" ).size() );

		Set<String> keys = provider.getKeys( "/" );
		assertEquals( 3, keys.size() );
		assertTrue( keys.contains( "key1" ) );
		assertTrue( keys.contains( "key2" ) );
		assertTrue( keys.contains( "key3" ) );
		//assertTrue( keys.contains( "path" ) );

		keys = provider.getKeys( "/path" );
		assertEquals( 3, keys.size() );
		assertTrue( keys.contains( "subkey1" ) );
		assertTrue( keys.contains( "subkey2" ) );
		assertTrue( keys.contains( "subkey3" ) );
	}

	@Test
	public void testGetChildNames() {
		assertEquals( 0, provider.getChildNames( "/invalid" ).size() );

		Set<String> names = provider.getChildNames( "/" );
		assertEquals( 1, names.size() );
		assertTrue( names.contains( "path" ) );

		assertEquals( 0, provider.getChildNames( "/path" ).size() );
	}

	@Test
	public void testNodeExists() {
		assertTrue( provider.nodeExists( "/" ) );
		assertTrue( provider.nodeExists( "/path" ) );
		assertFalse( provider.nodeExists( "/key1" ) );
		assertFalse( provider.nodeExists( "/invalid" ) );
	}

}
