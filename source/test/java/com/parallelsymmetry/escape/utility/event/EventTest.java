package com.parallelsymmetry.escape.utility.event;

import junit.framework.TestCase;

public class EventTest extends TestCase {

	public void testEventConstructor() throws Exception {
		Event event = new Event( this );
		assertEquals( this, event.getSource() );
	}

}
