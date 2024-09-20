package com.parallelsymmetry.utility.event;

import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventTest extends BaseTestCase {

	@Test
	public void testEventConstructor() {
		Event event = new Event( this );
		assertEquals( this, event.getSource() );
		assertEquals( null, event.getScope() );
		assertEquals( Event.DEFAULT_PRIORITY, event.getPriority() );
	}

	@Test
	public void testEventConstructorWithSourceScopePriority() {
		Object source = new Object();
		Object scope = new Object();
		int priority = Event.HIGHEST_PRIORITY;
		Event event = new Event( source, scope, priority );
		assertEquals( source, event.getSource() );
		assertEquals( scope, event.getScope() );
		assertEquals( priority, event.getPriority() );
	}

}
