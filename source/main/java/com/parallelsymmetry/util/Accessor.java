package com.parallelsymmetry.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Accessor {

	@SuppressWarnings( "unchecked" )
	public static <T> T create( String name, Object... parameters ) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException {
		if( name == null ) throw new NullPointerException( "Class name cannot be null." );

		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for( int index = 0; index < parameters.length; index++ ) {
			parameterTypes[index] = parameters[index].getClass();
		}

		Class<?> clazz = Class.forName( name );
		Constructor<?> constructor = clazz.getDeclaredConstructor( parameterTypes );
		constructor.setAccessible( true );
		return (T)constructor.newInstance( parameters );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T getField( Object object, String name ) throws NoSuchFieldException, IllegalAccessException {
		return (T)getField( object.getClass(), object, name );
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T getField( Class clazz, Object object, String name ) throws NoSuchFieldException, IllegalAccessException {
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
	public static <T> T getField( Class clazz, String name ) throws NoSuchFieldException, IllegalAccessException {
		if( clazz == null ) throw new NullPointerException( "Class cannot be null." );

		Field field = clazz.getDeclaredField( name );
		field.setAccessible( true );
		return (T)field.get( null );
	}

	/**
	 * Parameters can be specified in two ways:
	 * <ol>
	 * <li>Values only. The value types must exactly match the method parameter
	 * types.</li>
	 * <li>Type/value pairs. The type must exactly match the method parameter
	 * type and be compatible with the value.</li>
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
			Class clazz = object.getClass();
			Class<?>[] parameterTypes = new Class<?>[parameters.length];
			for( int index = 0; index < parameters.length; index++ ) {
				parameterTypes[index] = parameters[index].getClass();
			}

			while( method == null & clazz != null ) {
				try {
					method = clazz.getDeclaredMethod( name, parameterTypes );
				} catch( NoSuchMethodException exception ) {}
				clazz = clazz.getSuperclass();
			}
		}

		if( method == null ) {
			Class clazz = object.getClass();
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
	public static <T> T callMethod( Class clazz, String name, Object... parameters ) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if( clazz == null ) throw new NullPointerException( "Class cannot be null." );

		Method method = null;
		if( method == null ) {
			Class<?>[] parameterTypes = new Class<?>[parameters.length];
			for( int index = 0; index < parameters.length; index++ ) {
				parameterTypes[index] = parameters[index].getClass();
			}

			try {
				method = clazz.getDeclaredMethod( name, parameterTypes );
			} catch( NoSuchMethodException exception ) {}
		}

		if( method == null ) {
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
