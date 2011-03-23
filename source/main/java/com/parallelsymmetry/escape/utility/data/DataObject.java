package com.parallelsymmetry.escape.utility.data;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.parallelsymmetry.escape.utility.ObjectUtil;

public abstract class DataObject {

	public static final String MODIFIED = "modified";

	private static final Object NULL = new Object();

	private boolean modified;

	private Map<String, Object> attributes;

	private int modifiedAttributeCount;

	private Map<String, Object> modifiedAttributes;

	private Map<String, Object> resources;

	private Set<DataListener> listeners = new CopyOnWriteArraySet<DataListener>();

	public boolean isModified() {
		return modified;
	}

	public void commit() {
		if( !modified ) return;

		setModified( false );
		modifiedAttributes = null;
		modifiedAttributeCount = 0;

		// Notify listeners of the data change.
		fireDataChanged( new DataEvent( DataEvent.Type.MODIFY, this ) );
	}

	@SuppressWarnings( "unchecked" )
	public <T> T getAttribute( String name ) {
		// Null attribute names are not allowed.
		if( name == null ) throw new NullPointerException( "Attribute name cannot be null." );

		return (T)( attributes == null ? null : attributes.get( name ) );
	}

	public void setAttribute( String name, Object newValue ) {
		// Null attribute names are not allowed.
		if( name == null ) throw new NullPointerException( "Attribute name cannot be null." );

		// If the old value is equal to the new value no changes are necessary.
		Object oldValue = getAttribute( name );
		if( ObjectUtil.areEqual( oldValue, newValue ) ) return;

		// Create the attribute map if necessary.
		if( attributes == null ) attributes = new ConcurrentHashMap<String, Object>();

		// Set the attribute value.
		if( newValue == null ) {
			attributes.remove( name );
		} else {
			attributes.put( name, newValue );
		}

		// Remove the attribute map if necessary.
		if( attributes.size() == 0 ) attributes = null;

		// Update the modified attribute value map.
		Object preValue = modifiedAttributes == null ? null : modifiedAttributes.get( name );
		if( ObjectUtil.areEqual( preValue == NULL ? null : preValue, newValue ) ) {
			modifiedAttributes.remove( name );
			modifiedAttributeCount--;
			if( modifiedAttributes.size() == 0 ) modifiedAttributes = null;
		} else if( preValue == null ) {
			// Only add the value if there is not an existing previous value.
			if( modifiedAttributes == null ) modifiedAttributes = new ConcurrentHashMap<String, Object>();
			modifiedAttributes.put( name, oldValue == null ? NULL : oldValue );
			modifiedAttributeCount++;
		}

		// Notify listeners of data attribute change.
		DataEvent.Type type = DataEvent.Type.MODIFY;
		type = oldValue == null ? DataEvent.Type.INSERT : type;
		type = newValue == null ? DataEvent.Type.REMOVE : type;
		dispatchDataEvent( new DataAttributeEvent( type, this, name, oldValue, newValue ) );

		// Update the modified flag.
		setModified( modifiedAttributeCount != 0 );

		// Notify listeners of the data change.
		dispatchDataEvent( new DataEvent( DataEvent.Type.MODIFY, this ) );
	}

	public int getModifiedAttributeCount() {
		return modifiedAttributeCount;
	}

	/**
	 * Get a stored resource.
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	public <T> T getResource( String key ) {
		if( resources == null ) return null;
		return (T)resources.get( key );
	}

	/**
	 * Store a resource. Setting or removing a resource will not modify the data.
	 * A resource is removed by setting the resource value to null.
	 * 
	 * @param value
	 */
	public void putResource( String key, Object value ) {
		if( value == null ) {
			if( resources == null ) return;
			resources.remove( key );
			cleanupResourceMap();
		} else {
			ensureResourceMap();
			resources.put( key, value );
		}
	}

	public void addDataListener( DataListener listener ) {
		listeners.add( listener );
	}

	public void removeDataListener( DataListener listener ) {
		listeners.remove( listener );
	}

	protected void dispatchDataEvent( DataEvent event ) {
		if( event instanceof DataAttributeEvent ) {
			fireDataAttributeChanged( (DataAttributeEvent)event );
		} else if( event instanceof MetaAttributeEvent ) {
			fireMetaAttributeChanged( (MetaAttributeEvent)event );
		} else {
			fireDataChanged( event );
		}
	}

	private void fireDataChanged( DataEvent event ) {
		for( DataListener listener : this.listeners ) {
			listener.dataChanged( event );
		}
	}

	private void fireDataAttributeChanged( DataAttributeEvent event ) {
		for( DataListener listener : listeners ) {
			listener.dataAttributeChanged( event );
		}
	}

	private void fireMetaAttributeChanged( MetaAttributeEvent event ) {
		for( DataListener listener : listeners ) {
			listener.metaAttributeChanged( event );
		}
	}

	private void setModified( boolean modified ) {
		if( this.modified == modified ) return;

		this.modified = modified;

		// Notify listeners of modified change events.
		dispatchDataEvent( new MetaAttributeEvent( DataEvent.Type.MODIFY, this, MODIFIED, !modified, modified ) );
	}

	private void ensureResourceMap() {
		if( resources == null ) resources = new ConcurrentHashMap<String, Object>();
	}

	private void cleanupResourceMap() {
		if( resources.size() == 0 ) resources = null;
	}

}
