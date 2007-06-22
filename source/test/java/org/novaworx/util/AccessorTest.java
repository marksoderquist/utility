package org.novaworx.util;

import junit.framework.TestCase;

public class AccessorTest extends TestCase {

	public void testGetField() throws Exception {
		Object object = new Object();
		PrivateClass privateClass = new PrivateClass( object );
		assertEquals( object, Accessor.getField( privateClass, "field" ) );
	}

	public void testCallMethod() throws Exception {
		Object object = new Object();
		PrivateClass privateClass = new PrivateClass( object );
		assertEquals( object, Accessor.callMethod( privateClass, "getObject" ) );
	}

	public void testCallMethodWithParameter() throws Exception {
		Object object = new Object();
		PrivateClass privateClass = new PrivateClass( object );
		assertEquals( object, Accessor.callMethod( privateClass, "loopback", object ) );
	}

	public void testCallStaticMethod() throws Exception {
		Object object = new Object();
		PrivateClass privateClass = new PrivateClass( object );
		assertEquals( object, Accessor.callMethod( privateClass, "staticMethod" ) );
	}

	public void testCallStaticMethodWithParameter() throws Exception {
		Object object = new Object();
		PrivateClass privateClass = new PrivateClass( object );
		assertEquals( object, Accessor.callMethod( privateClass, "staticLoopback", object ) );
	}

	private static class PrivateClass {

		private Object field;

		private static Object staticField;

		public PrivateClass( Object object ) {
			field = object;
			staticField = object;
		}

		@SuppressWarnings("unused")
		private Object getObject() {
			return field;
		}

		@SuppressWarnings("unused")
		private Object loopback( Object object ) {
			return object;
		}

		@SuppressWarnings("unused")
		private static Object staticMethod() {
			return staticField;
		}

		@SuppressWarnings("unused")
		private static Object staticLoopback( Object object ) {
			return object;
		}

	}

}
