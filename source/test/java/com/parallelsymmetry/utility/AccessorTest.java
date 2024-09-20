package com.parallelsymmetry.utility;

import com.parallelsymmetry.utility.Accessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccessorTest {

	@Test
	public void testCreate() throws Exception {
		PrivateClass object1 = null;
		object1 = Accessor.create( PrivateClass.class );
		assertNotNull( object1 );
		assertEquals( PrivateClass.class, object1.getClass() );

		try {
			Accessor.create( PrivateClass.class, "object" );
			fail( "Constructor should not have been found." );
		} catch( NoSuchMethodException exception ) {}

		PrivateClass object3 = null;
		object3 = Accessor.create( PrivateClass.class, Object.class, "object" );
		assertNotNull( object3 );
		assertEquals( PrivateClass.class, object3.getClass() );
		assertEquals( "object", Accessor.getField( object3, "field" ) );
	}

	@Test
	public void testCreateNestedClass() throws Exception {
		Object object1 = Accessor.create( PrivateClass.NestedClass.class );
		assertNotNull( object1 );
		assertEquals( PrivateClass.NestedClass.class, object1.getClass() );

		try {
			Accessor.create( PrivateClass.NestedClass.class, "object" );
			fail( "Constructor should not have been found." );
		} catch( NoSuchMethodException exception ) {}

		Object checkObject = new Object();
		Object object2 = Accessor.create( PrivateClass.NestedClass.class, checkObject );
		assertNotNull( object2 );
		assertEquals( PrivateClass.NestedClass.class, object2.getClass() );
		assertEquals( checkObject, Accessor.getField( object2, "object" ) );
	}

	@Test
	public void testCreateInnerClass() throws Exception {
		PrivateClass object = new PrivateClass();
		PrivateClass.InnerClass object1 = Accessor.create( PrivateClass.InnerClass.class, object );
		assertNotNull( object1 );
		assertEquals( PrivateClass.InnerClass.class, object1.getClass() );

		try {
			Accessor.create( PrivateClass.InnerClass.class.getName(), "loopback" );
			fail( "Constructor should not have been found." );
		} catch( NoSuchMethodException exception ) {}

		PrivateClass.InnerClass object2 = Accessor.create( PrivateClass.InnerClass.class, object, Object.class, "object" );
		assertNotNull( object2 );
		assertEquals( PrivateClass.InnerClass.class, object2.getClass() );
	}

	@Test
	public void testCreateWithString() throws Exception {
		Object object1 = Accessor.create( PrivateClass.class.getName() );
		assertNotNull( object1 );
		assertEquals( PrivateClass.class, object1.getClass() );

		try {
			Accessor.create( PrivateClass.class.getName(), "loopback" );
			fail( "Constructor should not have been found." );
		} catch( NoSuchMethodException exception ) {}

		Object object2 = Accessor.create( PrivateClass.class.getName(), new Object() );
		assertNotNull( object2 );
		assertEquals( PrivateClass.class, object2.getClass() );
	}

	@Test
	public void testCreateNestedClassWithString() throws Exception {
		Object object1 = Accessor.create( PrivateClass.NestedClass.class.getName() );
		assertNotNull( object1 );
		assertEquals( PrivateClass.NestedClass.class, object1.getClass() );

		try {
			Accessor.create( PrivateClass.NestedClass.class.getName(), "loopback" );
			fail( "Constructor should not have been found." );
		} catch( NoSuchMethodException exception ) {}

		Object object2 = Accessor.create( PrivateClass.NestedClass.class.getName(), new Object() );
		assertNotNull( object2 );
		assertEquals( PrivateClass.NestedClass.class, object2.getClass() );
	}

	@Test
	public void testGetField() throws Exception {
		Object object = new Object();
		PrivateClass privateClass = new PrivateClass( object );
		assertEquals( object, Accessor.getField( privateClass, "field" ) );
	}

	@Test
	public void testSetField() throws Exception {
		PrivateClass privateClass = new PrivateClass();
		assertNull( Accessor.getField( privateClass, "field" ) );

		Object object = new Object();
		Accessor.setField( privateClass, "field", object );
		assertEquals( object, Accessor.getField( privateClass, "field" ) );
	}

	@Test
	public void testSetStaticField() throws Exception {
		PrivateClass privateClass = new PrivateClass();
		assertNull( Accessor.getField( PrivateClass.class, "staticField" ) );

		Object object = new Object();
		Accessor.setField( privateClass, "staticField", object );
		assertEquals( object, Accessor.getField( privateClass, "staticField" ) );
	}

	@Test
	public void testCallMethod() throws Exception {
		Object object = new Object();
		PrivateClass privateClass = new PrivateClass( object );
		assertEquals( object, Accessor.callMethod( privateClass, "getObject" ) );
	}

	@Test
	public void testCallMethodWithParameter() throws Exception {
		Object object = new Object();
		PrivateClass privateClass = new PrivateClass();
		assertEquals( object, Accessor.callMethod( privateClass, "loopback", object ) );
	}

	@Test
	public void testCallMethodWithTypeAndParameter() throws Exception {
		String string = new String();
		PrivateClass privateClass = new PrivateClass();

		try {
			Accessor.callMethod( privateClass, "loopback", string );
			fail( "Method should not have been found." );
		} catch( NoSuchMethodException exception ) {}

		assertEquals( string, Accessor.callMethod( privateClass, "loopback", Object.class, string ) );
	}

	@Test
	public void testCallStaticMethod() throws Exception {
		Object object = new Object();
		PrivateClass.staticField = object;
		assertEquals( object, Accessor.callMethod( PrivateClass.class, "staticMethod" ) );
	}

	@Test
	public void testCallStaticMethodWithParameter() throws Exception {
		Object object = new Object();
		assertEquals( object, Accessor.callMethod( PrivateClass.class, "staticLoopback", object ) );
	}

	@Test
	public void testCallStaticMethodWithTypeAndParameter() throws Exception {
		String string = new String();
		PrivateClass privateClass = new PrivateClass( string );

		try {
			Accessor.callMethod( privateClass, "staticLoopback", string );
			fail( "Method should not have been found." );
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
		private class InnerClass {

			private Object object;

			public InnerClass() {
				this( null );
			}

			public InnerClass( Object object ) {
				this.object = object;
			}

			public Object getObject() {
				return object;
			}

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
