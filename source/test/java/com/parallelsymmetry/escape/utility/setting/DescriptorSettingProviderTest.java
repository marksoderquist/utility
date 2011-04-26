package com.parallelsymmetry.escape.utility.setting;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.Descriptor;

public class DescriptorSettingProviderTest extends TestCase {

	private Descriptor descriptor;

	private DescriptorSettingProvider provider;

	private DescriptorSettingProvider rootedProvider;

	public void setUp() throws Exception {
		descriptor = new Descriptor( getClass().getResourceAsStream( "/test.descriptor.xml" ) );
		provider = new DescriptorSettingProvider( descriptor );
		rootedProvider = new DescriptorSettingProvider( descriptor, false );
	}

	public void testGet() {
		assertEquals( "test.path.value", provider.get( "/path/value" ) );
		assertEquals( "test.path.value", rootedProvider.get( "/test/path/value" ) );
	}

	public void testGetNames() {
		assertEquals( 6, provider.getNames( "" ).size() );
	}

	public void testNodeExists() {
		assertFalse( provider.nodeExists( "/path/invalid" ) );
		assertFalse( rootedProvider.nodeExists( "/test/path/invalid" ) );
		assertTrue( provider.nodeExists( "/path" ) );
		assertTrue( rootedProvider.nodeExists( "/test/path" ) );
	}

}
