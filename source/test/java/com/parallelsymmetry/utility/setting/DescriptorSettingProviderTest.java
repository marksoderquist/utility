package com.parallelsymmetry.utility.setting;

import com.parallelsymmetry.utility.Descriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DescriptorSettingProviderTest extends BaseSettingProviderTest {

	private Descriptor descriptor;

	private DescriptorSettingsProvider provider;

	private DescriptorSettingsProvider rootedProvider;

	@BeforeEach
	@Override
	public void setup() throws Exception {
		super.setup();
		descriptor = new Descriptor( getClass().getResourceAsStream( "/test.descriptor.xml" ) );
		provider = new DescriptorSettingsProvider( descriptor );
		rootedProvider = new DescriptorSettingsProvider( descriptor, false );
	}

	@Test
	public void testGet() {
		assertEquals( "test.path.value", provider.get( "/path/value" ) );
	}

	@Test
	public void testGetWithAttributes() {
		assertNull( provider.get( "/invalid/path" ) );
		assertEquals( "5", provider.get( "/bounds/x" ) );
		assertEquals( "10", provider.get( "/bounds/y" ) );
		assertEquals( "20", provider.get( "/bounds/w" ) );
		assertEquals( "15", provider.get( "/bounds/h" ) );
	}

	@Test
	public void testGetChildNames() {
		Set<String> names = provider.getChildNames( "" );

		assertEquals( 4, names.size() );
		assertTrue( names.contains( "path" ) );
		assertTrue( names.contains( "bounds" ) );
		assertTrue( names.contains( "list" ) );
		assertTrue( names.contains( "nodes" ) );
	}

	@Test
	public void testNodeExists() {
		assertFalse( provider.nodeExists( "/path/invalid" ) );
		assertTrue( provider.nodeExists( "/path" ) );
	}

	@Test
	public void testRootedProviderGet() {
		assertEquals( "test.path.value", rootedProvider.get( "/test/path/value" ) );
	}

	@Test
	public void testRootedProviderGetWithAttributes() {
		assertNull( provider.get( "/test/invalid/path" ) );
		assertEquals( "5", rootedProvider.get( "/test/bounds/x" ) );
		assertEquals( "10", rootedProvider.get( "/test/bounds/y" ) );
		assertEquals( "20", rootedProvider.get( "/test/bounds/w" ) );
		assertEquals( "15", rootedProvider.get( "/test/bounds/h" ) );
	}

	@Test
	public void testRootedProviderGetChildNames() {
		Set<String> names = rootedProvider.getChildNames( "" );

		assertEquals( 1, names.size() );
		assertTrue( names.contains( "test" ) );
	}

	@Test
	public void testRootedProviderNodeExists() {
		assertFalse( rootedProvider.nodeExists( "/test/path/invalid" ) );
		assertTrue( rootedProvider.nodeExists( "/test/path" ) );
	}

}
