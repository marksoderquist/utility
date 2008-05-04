package com.parallelsymmetry.util;

import junit.framework.TestCase;

public class AccessorTest extends TestCase {

	public void testCreate() throws Exception {
		Object object1 = Accessor.create( "com.parallelsymmetry.util.AccessorTest$PrivateClass$NestedClass" );
		assertNotNull( object1 );
		assertEquals( "com.parallelsymmetry.util.AccessorTest$PrivateClass$NestedClass", object1.getClass().getName() );

		Object object2 = Accessor.create( "com.parallelsymmetry.util.AccessorTest$PrivateClass$NestedClass", new Object() );
		assertNotNull( object2 );
		assertEquals( "com.parallelsymmetry.util.AccessorTest$PrivateClass$NestedClass", object2.getClass().getName() );
	}

	public void testCreateWithTypeAndParameter() throws Exception {
		PrivateClass privateClass = null;
		privateClass = Accessor.create( PrivateClass.class.getName(), new Object() );
		assertNotNull( privateClass );

		privateClass = Accessor.create( PrivateClass.class.getName(), Object.class, "loopback" );
		assertNotNull( privateClass );
	}

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
		PrivateClass privateClass = new PrivateClass();
		assertEquals( object, Accessor.callMethod( privateClass, "loopback", object ) );
	}

	public void testCallMethodWithTypeAndParameter() throws Exception {
		String string = new String();
		PrivateClass privateClass = new PrivateClass();

		try {
			Accessor.callMethod( privateClass, "loopback", string );
			fail( "Method should not be found with String parameter." );
		} catch( NoSuchMethodException exception ) {}

		assertEquals( string, Accessor.callMethod( privateClass, "loopback", Object.class, string ) );
	}

	public void testCallStaticMethod() throws Exception {
		Object object = new Object();
		PrivateClass.staticField = object;
		assertEquals( object, Accessor.callMethod( PrivateClass.class, "staticMethod" ) );
	}

	public void testCallStaticMethodWithParameter() throws Exception {
		Object object = new Object();
		assertEquals( object, Accessor.callMethod( PrivateClass.class, "staticLoopback", object ) );
	}

	public void testCallStaticMethodWithTypeAndParameter() throws Exception {
		String string = new String();
		PrivateClass privateClass = new PrivateClass( string );

		try {
			Accessor.callMethod( privateClass, "staticLoopback", string );
			fail( "Method should not be found with String parameter." );
		} catch( NoSuchMethodException exception ) {}

		assertEquals( string, Accessor.callMethod( PrivateClass.class, "staticLoopback", Object.class, string ) );
	}

	private static class PrivateClass {

		private Object field;

		private static Object staticField;

		public PrivateClass() {
			this( null );
		}

		public PrivateClass( Object object ) {
			field = object;
			staticField = object;
		}

		@SuppressWarnings( "unused" )
		private Object getObject() {
			return field;
		}

		@SuppressWarnings( "unused" )
		private Object loopback( Object object ) {
			return object;
		}

		@SuppressWarnings( "unused" )
		private static Object staticMethod() {
			return staticField;
		}

		@SuppressWarnings( "unused" )
		private static Object staticLoopback( Object object ) {
			return object;
		}

		@SuppressWarnings( "unused" )
		private static class NestedClass {

			private Object object;

			public NestedClass() {
				this( null );
			}

			public NestedClass( Object object ) {
				this.object = object;
			}

			public Object getObject() {
				return object;
			}

		}

	}

}
