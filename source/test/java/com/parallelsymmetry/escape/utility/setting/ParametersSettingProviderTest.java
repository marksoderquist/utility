package com.parallelsymmetry.escape.utility.setting;

import java.util.Set;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.Parameters;

public class ParametersSettingProviderTest extends TestCase {

	private Parameters parameters;

	private ParametersSettingProvider provider;

	public void setUp() {
		parameters = Parameters.parse( new String[] { "-host", "localhost", "-log.level", "debug" } );
		provider = new ParametersSettingProvider( parameters );
	}

	public void testGet() {
		assertNull( provider.get( "/port" ) );
		assertEquals( "localhost", provider.get( "/host" ) );
	}

	public void testGetChildNames() {
		Set<String> names = provider.getChildNames( "" );
		assertEquals( 1, names.size() );
		assertTrue( names.contains( "log" ) );
	}

	public void testNodeExists() {
		assertFalse( provider.nodeExists( "/invalid" ) );
		assertTrue( provider.nodeExists( "/log" ) );
	}

}
