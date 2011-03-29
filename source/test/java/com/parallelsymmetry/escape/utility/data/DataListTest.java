package com.parallelsymmetry.escape.utility.data;

import java.lang.reflect.Modifier;

import org.junit.Test;

public class DataListTest extends DataTestCase {

	@Test
	public void testDataListIsAbstract() {
		assertTrue( "DataList class is not abstract.", ( DataList.class.getModifiers() & Modifier.ABSTRACT ) == Modifier.ABSTRACT );
	}

	public void testConstructor() {
		MockDataList list = new MockDataList();
		assertDataListState( list, false, 0, 0 );
	}

	public void testAdd() {

	}

}
