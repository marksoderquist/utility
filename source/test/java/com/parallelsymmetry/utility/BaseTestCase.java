package com.parallelsymmetry.utility;

import com.parallelsymmetry.utility.log.Log;

import junit.framework.TestCase;

public abstract class BaseTestCase extends TestCase {

	public BaseTestCase() {}

	public BaseTestCase( String name ) {
		super( name );
	}

	@Override
	public void setUp() throws Exception {
		Log.setLevel( Log.NONE );
	}

	@Override
	public void tearDown() throws Exception {
		Log.setLevel( Log.NONE );
	}

}
