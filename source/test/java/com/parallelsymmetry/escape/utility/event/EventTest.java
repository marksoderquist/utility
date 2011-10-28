package com.parallelsymmetry.escape.utility.event;

import junit.framework.TestCase;

public class EventTest extends TestCase {

	public void testEventConstructor() throws Exception {
		Event event = new Event( this );
		assertEquals( this, event.getSource() );
		assertEquals( null, event.getScope() );
		assertEquals( Event.DEFAULT_PRIORITY, event.getPriority() );
	}

	public void testEventConstructorWithSourceScopePriority() throws Exception {
		Object source = new Object();
		Object scope = new Object();
		int priority = Event.HIGHEST_PRIORITY;
		Event event = new Event( source, scope, priority );
		assertEquals( source, event.getSource() );
		assertEquals( scope, event.getScope() );
		assertEquals( priority, event.getPriority() );
	}

}
