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

	public void testPut() {
		assertNull( provider.get( "/xtest/path" ) );
		provider.put( "/xtest/path", "value" );
		assertNull( provider.get( "/xtest/path" ) );
	}

	public void testIsWritable() {
		assertFalse( provider.isWritable() );
	}

}
