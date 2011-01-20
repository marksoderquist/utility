package com.parallelsymmetry.escape.utility.setting;

import junit.framework.TestCase;

public class SettingTest extends TestCase {

	private MockSettingProvider provider1 = new MockSettingProvider( false );

	private MockSettingProvider provider2 = new MockSettingProvider( true );

	private MockSettingProvider provider3 = new MockSettingProvider( false );

	private MockSettingProvider providerD = new MockSettingProvider( false );

	Settings settings = new Settings();

	public void setUp() {
		settings.addProvider( provider1 );
		settings.addProvider( provider2 );
		settings.addProvider( provider3 );
		settings.setDefaultProvider( providerD );
	}

	public void testGet() {
		String path = "/test/get/value";
		assertNull( settings.get( path ) );

		// Setting the value in the default provider should give us a default value.
		providerD.set( path, "D" );
		assertEquals( "D", settings.get( path ) );

		// Setting the value in provider 2 should override the default provider.
		provider2.set( path, "2" );
		assertEquals( "2", settings.get( path ) );

		// Setting the value in provider 3 should not override provider 2.
		provider3.set( path, "3" );
		assertEquals( "2", settings.get( path ) );

		// Setting the value in provider 1 should override provider 2.
		provider1.set( path, "1" );
		assertEquals( "1", settings.get( path ) );
	}

	public void testPut() {
		String path = "/test/put/value";
		String value = "X";
		settings.put( path, value );

		// The value should be stored in provider 2 since it is the only one writable.
		assertNull( provider1.get( path ) );
		assertEquals( value, provider2.get( path ) );
		assertNull( provider3.get( path ) );
		assertNull( providerD.get( path ) );
	}

	public void testMounts() {
		settings = new Settings();
		settings.addProvider( provider1, "/1" );
		settings.addProvider( provider2, "/2" );
		settings.addProvider( provider3, "/3" );
		settings.setDefaultProvider( providerD, "/D" );

		provider1.set( "/value", "1" );
		provider2.set( "/value", "2" );
		provider3.set( "/value", "3" );
		providerD.set( "/value", "D" );

		assertEquals( "1", settings.get( "/1/value" ) );
		assertEquals( "2", settings.get( "/2/value" ) );
		assertEquals( "3", settings.get( "/3/value" ) );
		assertEquals( "D", settings.get( "/D/value" ) );
	}

	public void testNoProviders() {
		assertNull( new Settings().get( "/" ) );
	}

	public void testProviderOverride() {
		provider1.set( "/test/path/1", "1" );

		provider2.set( "/test/path/1", "2" );
		provider2.set( "/test/path/2", "2" );

		provider3.set( "/test/path/1", "3" );
		provider3.set( "/test/path/2", "3" );
		provider3.set( "/test/path/3", "3" );

		providerD.set( "/test/path/1", "D" );
		providerD.set( "/test/path/2", "D" );
		providerD.set( "/test/path/3", "D" );
		providerD.set( "/test/path/D", "D" );

		assertEquals( "1", settings.get( "/test/path/1" ) );
		assertEquals( "2", settings.get( "/test/path/2" ) );
		assertEquals( "3", settings.get( "/test/path/3" ) );
		assertEquals( "D", settings.get( "/test/path/D" ) );
	}

}
