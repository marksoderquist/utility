package com.parallelsymmetry.escape.utility.setting;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.Parameters;

public class ParametersSettingProviderTest extends TestCase {

	private Parameters parameters;

	private ParametersSettingProvider provider;

	public void setUp() {
		parameters = Parameters.parse( new String[] { "-host", "localhost" } );
		provider = new ParametersSettingProvider( parameters );
	}

	public void testGet() {
		assertNull( provider.get( "/port" ) );
		assertEquals( "localhost", provider.get( "/host" ) );
	}

	public void testPut() {
		assertNull( provider.get( "/port" ) );
		provider.put( "/port", "80" );
		assertNull( provider.get( "/port" ) );
	}

	public void testIsWritable() {
		assertFalse( provider.isWritable() );
	}
	
	public void testPrefix() {
		parameters = Parameters.parse( new String[] { "-host", "localhost" } );
		provider = new ParametersSettingProvider("/test", parameters );
		assertNull( provider.get( "/test/port" ) );
		assertEquals( "localhost", provider.get( "/test/host" ) );
	}

}
