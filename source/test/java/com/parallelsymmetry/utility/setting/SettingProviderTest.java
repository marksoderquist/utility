package com.parallelsymmetry.utility.setting;

import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

public abstract class SettingProviderTest extends TestCase {

	protected SettingProvider provider;

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
		assertNull( provider.getKeys( "/invalid" ) );

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
		assertNull( provider.getChildNames( "/invalid" ) );

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
