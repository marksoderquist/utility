package org.novaworx.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Accessor {

	@SuppressWarnings( "unchecked" )
	public static <T> T getField( Object object, String name ) throws NoSuchFieldException {
		if( object == null ) throw new NullPointerException( "Object cannot be null." );

		Field field = null;
		Class clazz = object.getClass();
		while( field == null & clazz != null ) {
			try {
				field = clazz.getDeclaredField( name );
			} catch( NoSuchFieldException exception ) {}
			clazz = clazz.getSuperclass();
		}
		if( field == null ) throw new NoSuchFieldException( name );
		field.setAccessible( true );
		try {
			return (T)field.get( object );
		} catch( IllegalAccessException exception ) {
			assert false;
		}
		return null;
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T callMethod( Object object, String name, Object... parameters ) throws NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		if( object == null ) throw new NullPointerException( "Object cannot be null." );

		Class< ? >[] parameterTypes = new Class< ? >[ parameters.length ];
		for( int index = 0; index < parameters.length; index++ ) {
			parameterTypes[ index ] = parameters[ index ].getClass();
		}

		Method method = null;
		Class clazz = object.getClass();
		while( method == null & clazz != null ) {
			try {
				method = clazz.getDeclaredMethod( name, parameterTypes );
			} catch( NoSuchMethodException exception ) {}
			clazz = clazz.getSuperclass();
		}
		if( method == null ) throw new NoSuchMethodException( name );
		method.setAccessible( true );
		try {
			return (T)method.invoke( object, parameters );
		} catch( IllegalAccessException exception ) {
			assert false;
		}
		return null;
	}

	@SuppressWarnings( "unchecked" )
	public static <T> T callStaticMethod( Class clazz, String name, Object... parameters ) throws NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		if( clazz == null ) throw new NullPointerException( "Class cannot be null." );

		Class< ? >[] parameterTypes = new Class< ? >[ parameters.length ];
		for( int index = 0; index < parameters.length; index++ ) {
			parameterTypes[ index ] = parameters[ index ].getClass();
		}

		Method method = clazz.getDeclaredMethod( name, parameterTypes );
		method.setAccessible( true );
		try {
			return (T)method.invoke( null, parameters );
		} catch( IllegalAccessException exception ) {
			assert false;
		}
		return null;
	}

}
