package com.parallelsymmetry.utility;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * The MappedResourceBundle class implements ResourceBundle in a manner that
 * many collections of resources can be added to a single bundle. Key/value
 * pairs are added on a first-come/first-serve basis. This ensures that added
 * collections cannot override existing collections.
 * 
 * @author mvsoder
 */
public class MappedResourceBundle extends ResourceBundle {

	private Map<String, Object> values;

	public MappedResourceBundle() {
		values = new HashMap<String, Object>();
	}

	public void add( Properties properties ) {
		for( Object key : properties.keySet() ) {
			if( values.containsKey( key ) ) continue;
			values.put( key.toString(), properties.get( key ) );
		}
	}

	public void add( ResourceBundle bundle ) {
		Enumeration<String> keys = bundle.getKeys();
		while( keys.hasMoreElements() ) {
			String key = keys.nextElement();
			if( values.containsKey( key ) ) continue;
			values.put( key, bundle.getObject( key ) );
		}
	}

	public void add( Map<String, Object> values ) {
		for( String key : values.keySet() ) {
			if( values.containsKey( key ) ) continue;
			this.values.put( key, values.get( key ) );
		}
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
