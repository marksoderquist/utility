package com.parallelsymmetry.utility;

import com.parallelsymmetry.utility.log.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseTestCase {

	public BaseTestCase() {}

	@BeforeEach
	public void setup() throws Exception {
		Log.setLevel( Log.NONE );
	}

	@AfterEach
	public void teardown() throws Exception {
		Log.setLevel( Log.NONE );
	}

}
