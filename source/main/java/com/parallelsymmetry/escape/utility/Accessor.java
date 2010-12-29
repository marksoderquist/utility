package com.parallelsymmetry.escape.utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Accessor {

	@SuppressWarnings( "unchecked" )
	public static <T> T create( String name, Object... parameters ) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException {
		if( name == null ) throw new NullPointerException( "Class name cannot be null." );
		Class<?> clazz = Class.forName( name );
		return (T)create( clazz, parameters );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T create( Class<?> clazz, Object... parameters ) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException {
		if( clazz == null ) throw new NullPointerException( "Class cannot be null." );

		Constructor<?> constructor = null;

		if( constructor == null ) {
			Class<?>[] parameterTypes = new Class<?>[parameters.length];
			for( int index = 0; index < parameters.length; index++ ) {
				if( parameters[index] != null ) parameterTypes[index] = parameters[index].getClass();
			}
			try {
				constructor = clazz.getDeclaredConstructor( parameterTypes );
			} catch( NoSuchMethodException exception ) {}
		}

		if( constructor == null ) {
			Class<?>[] parameterTypes = new Class<?>[parameters.length / 2];
			if( parameters.length % 2 == 0 ) {
				int count = parameters.length / 2;
				parameterTypes = new Class<?>[count];
				for( int index = 0; index < count; index++ ) {
					parameterTypes[index] = (Class<?>)parameters[index * 2];
				}
			} else {
				int count = ( parameters.length - 1 ) / 2;
				parameterTypes = new Class<?>[count + 1];
				parameterTypes[0] = parameters[0].getClass();
				for( int index = 0; index < count; index++ ) {
					parameterTypes[index + 1] = (Class<?>)parameters[( index * 2 ) + 1];
				}
			}

			Class<?> checkClass = clazz;
			while( constructor == null & checkClass != null ) {
				try {
					constructor = checkClass.getDeclaredConstructor( parameterTypes );
				} catch( NoSuchMethodException exception ) {}
				checkClass = checkClass.getSuperclass();
			}

			if( constructor != null ) {
				if( parameters.length % 2 == 0 ) {
					Object[] incomming = parameters;
					parameters = new Object[parameters.length / 2];
					for( int index = 0; index < parameters.length; index++ ) {
						parameters[index] = incomming[index * 2 + 1];
					}
				} else {
					Object[] incomming = parameters;
					parameters = new Object[parameters.length / 2 + 1];
					for( int index = 0; index < parameters.length; index++ ) {
						parameters[index] = incomming[index * 2];
					}
				}
			}
		}

		if( constructor == null ) throw new NoSuchMethodException( clazz.getName() );
		constructor.setAccessible( true );
		return (T)constructor.newInstance( parameters );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T getField( Object object, String name ) throws NoSuchFieldException, IllegalAccessException {
		return (T)getField( object.getClass(), object, name );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T getField( Class<?> clazz, Object object, String name ) throws NoSuchFieldException, IllegalAccessException {
		if( object == null ) throw new NullPointerException( "Object cannot be null." );

		Field field = null;
		while( field == null & clazz != null ) {
			try {
				field = clazz.getDeclaredField( name );
			} catch( NoSuchFieldException exception ) {}
			clazz = clazz.getSuperclass();
		}
		if( field == null ) throw new NoSuchFieldException( name );
		field.setAccessible( true );
		return (T)field.get( object );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T getField( Class<?> clazz, String name ) throws NoSuchFieldException, IllegalAccessException {
		if( clazz == null ) throw new NullPointerException( "Class cannot be null." );

		Field field = clazz.getDeclaredField( name );
		field.setAccessible( true );
		return (T)field.get( null );
	}

	public static void setField( Object object, String name, Object value ) throws NoSuchFieldException, IllegalAccessException {
		setField( object.getClass(), object, name, value );
	}

	public static void setField( Class<?> clazz, Object object, String name, Object value ) throws NoSuchFieldException, IllegalAccessException {
		if( clazz == null ) throw new NullPointerException( "Class cannot be null." );

		Field field = clazz.getDeclaredField( name );
		field.setAccessible( true );
		field.set( object, value );
	}

	public static void setField( Class<?> clazz, String name, Object value ) throws NoSuchFieldException, IllegalAccessException {
		if( clazz == null ) throw new NullPointerException( "Class cannot be null." );

		Field field = clazz.getDeclaredField( name );
		field.setAccessible( true );
		field.set( null, value );
	}

	/**
	 * Parameters can be specified in two ways:
	 * <ol>
	 * <li>Values only. The value types must exactly match the method parameter
	 * types.</li>
	 * <li>Type/value pairs. The type must exactly match the method parameter type
	 * and be compatible with the value.</li>
	 * </ol>
	 * 
	 * @param <T>
	 * @param object
	 * @param name
	 * @param parameters
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings( "unchecked" )
	public static <T> T callMethod( Object object, String name, Object... parameters ) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if( object == null ) throw new NullPointerException( "Object cannot be null." );

		Method method = null;
		if( method == null ) {
			Class<?> clazz = object.getClass();
			Class<?>[] parameterTypes = new Class<?>[parameters.length];
			for( int index = 0; index < parameters.length; index++ ) {
				if( parameters[index] != null ) parameterTypes[index] = parameters[index].getClass();
			}

			while( method == null & clazz != null ) {
				try {
					method = clazz.getDeclaredMethod( name, parameterTypes );
				} catch( NoSuchMethodException exception ) {}
				clazz = clazz.getSuperclass();
			}
		}

		if( method == null && parameters.length % 2 == 0 ) {
			Class<?> clazz = object.getClass();
			Class<?>[] parameterTypes = new Class<?>[parameters.length / 2];
			for( int index = 0; index < parameters.length / 2; index++ ) {
				parameterTypes[index] = (Class<?>)parameters[index * 2];
			}

			while( method == null & clazz != null ) {
				try {
					method = clazz.getDeclaredMethod( name, parameterTypes );
				} catch( NoSuchMethodException exception ) {}
				clazz = clazz.getSuperclass();
			}

			if( method != null ) {
				Object[] incomming = parameters;
				parameters = new Object[parameters.length / 2];
				for( int index = 0; index < parameters.length; index++ ) {
					parameters[index] = incomming[index * 2 + 1];
				}
			}
		}

		if( method == null ) throw new NoSuchMethodException( name );
		method.setAccessible( true );
		return (T)method.invoke( object, parameters );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T callMethod( Class<?> clazz, String name, Object... parameters ) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if( clazz == null ) throw new NullPointerException( "Class cannot be null." );

		Method method = null;
		if( method == null ) {
			Class<?>[] parameterTypes = new Class<?>[parameters.length];
			for( int index = 0; index < parameters.length; index++ ) {
				if( parameters[index] != null ) parameterTypes[index] = parameters[index].getClass();
			}

			try {
				method = clazz.getDeclaredMethod( name, parameterTypes );
			} catch( NoSuchMethodException exception ) {}
		}

		if( method == null && parameters.length % 2 == 0 ) {
			Class<?>[] parameterTypes = new Class<?>[parameters.length / 2];
			for( int index = 0; index < parameters.length / 2; index++ ) {
				parameterTypes[index] = (Class<?>)parameters[index * 2];
			}

			try {
				method = clazz.getDeclaredMethod( name, parameterTypes );
			} catch( NoSuchMethodException exception ) {}

			if( method != null ) {
				Object[] incomming = parameters;
				parameters = new Object[parameters.length / 2];
				for( int index = 0; index < parameters.length; index++ ) {
					parameters[index] = incomming[index * 2 + 1];
				}
			}
		}

		if( method == null ) throw new NoSuchMethodException( name );
		method.setAccessible( true );
		return (T)method.invoke( null, parameters );
	}

}
