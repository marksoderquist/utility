package com.parallelsymmetry.escape.utility;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

public class MappedResourceBundle extends ResourceBundle {

	private Map<String, Object> values;

	public MappedResourceBundle() {
		values = new HashMap<String, Object>();
	}

	public void add( Properties properties ) {
		for( Object key : properties.keySet() ) {
			values.put( key.toString(), properties.get( key ) );
		}
	}

	public void add( ResourceBundle bundle ) {
		Enumeration<String> keys = bundle.getKeys();
		while( keys.hasMoreElements() ) {
			String key = keys.nextElement();
			values.put( key, bundle.getObject( key ) );
		}
	}

	public void add( Map<String, Object> values ) {
		this.values.putAll( values );
	}

	@Override
	public Enumeration<String> getKeys() {
		return new IteratorEnumeration<String>( values.keySet().iterator() );
	}

	@Override
	protected Object handleGetObject( String key ) {
		return values.get( key );
	}

}
