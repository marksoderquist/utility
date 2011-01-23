package com.parallelsymmetry.escape.utility.setting;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.Descriptor;

public class DescriptorSettingProviderTest extends TestCase {

	private Descriptor descriptor;

	private DescriptorSettingProvider provider;

	public void setUp() throws Exception {
		descriptor = new Descriptor( getClass().getResourceAsStream( "/test.descriptor.xml" ) );
		provider = new DescriptorSettingProvider( descriptor );
	}

	public void testGet() {
		assertEquals( "test.path.value", provider.get( "/test/path/value" ) );
	}

	public void testNodeExists() {
		assertFalse( provider.nodeExists( "/test/path/invalid" ) );
		assertTrue( provider.nodeExists( "/test/path" ) );
	}

}
