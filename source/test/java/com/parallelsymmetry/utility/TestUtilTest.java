package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtilTest {

	@Test
	public void testIsTest() {
		assertThat( TestUtil.isTest() ).isTrue();
	}

}
