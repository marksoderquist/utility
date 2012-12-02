package com.parallelsymmetry.utility.setting;

import java.util.Set;

import junit.framework.TestCase;

import com.parallelsymmetry.utility.Descriptor;
import com.parallelsymmetry.utility.setting.DescriptorSettingProvider;

public class DescriptorSettingProvider2Test extends TestCase {

	private Descriptor descriptor;

	private DescriptorSettingProvider provider;

	private DescriptorSettingProvider rootedProvider;

	@Override
	public void setUp() throws Exception {
		descriptor = new Descriptor( getClass().getResourceAsStream( "/test.descriptor.xml" ) );
		provider = new DescriptorSettingProvider( descriptor );
		rootedProvider = new DescriptorSettingProvider( descriptor, false );
	}

	public void testGet() {
		assertEquals( "test.path.value", provider.get( "/path/value" ) );
		assertEquals( "test.path.value", rootedProvider.get( "/test/path/value" ) );
	}

	public void testGetWithAttributes() {
		assertNull( provider.get( "/invalid/path" ) );
		assertEquals( "5", provider.get( "/bounds/x" ) );
		assertEquals( "10", provider.get( "/bounds/y" ) );
		assertEquals( "20", provider.get( "/bounds/w" ) );
		assertEquals( "15", provider.get( "/bounds/h" ) );
	}

	public void testGetChildNames() {
		Set<String> names = provider.getChildNames( "" );

		assertEquals( 4, names.size() );
		assertTrue( names.contains( "path" ) );
		assertTrue( names.contains( "bounds" ) );
		assertTrue( names.contains( "list" ) );
		assertTrue( names.contains( "nodes" ) );
	}

	public void testNodeExists() {
		assertFalse( provider.nodeExists( "/path/invalid" ) );
		assertFalse( rootedProvider.nodeExists( "/test/path/invalid" ) );
		assertTrue( provider.nodeExists( "/path" ) );
		assertTrue( rootedProvider.nodeExists( "/test/path" ) );
	}

}