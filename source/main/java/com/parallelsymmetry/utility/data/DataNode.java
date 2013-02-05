package com.parallelsymmetry.utility.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.parallelsymmetry.utility.ObjectUtil;

public abstract class DataNode {

	public static final String MODIFIED = "modified";

	private static final Object NULL = new Object();

	protected boolean modified;

	protected Set<DataNode> parents = new CopyOnWriteArraySet<DataNode>();

	protected Set<DataListener> listeners = new CopyOnWriteArraySet<DataListener>();

	private boolean selfModified;

	private Map<String, Object> attributes;

	private int modifiedAttributeCount;

	private Map<String, Object> modifiedAttributes;

	private Map<String, Object> resources;

	/**
	 * Is the node modified. The node is modified if any attribute has been
	 * modified or any child node has been modified since the last time
	 * {@link #unmodify()} was called.
	 * 
	 * @return true if this node or any child nodes are modified, false otherwise.
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Set the modified flag for this node.
	 */
	public void setModified( boolean modified ) {
		if( modified ) {
			modify();
		} else {
			unmodify( null );
		}
	}

	/**
	 * Get an attribute value. Normally this method is not called directly but is
	 * wrapped by an attribute getter method. Example:
	 * 
	 * <pre>
	 * public String getName() {
	 * 	return getAttribute( &quot;name&quot; );
	 * }
	 * </pre>
	 * 
	 * Note: Be sure to handle primitives correctly by checking for null values:
	 * 
	 * <pre>
	 * public int getLimit() {
	 * 	Integer result = getAttribute( &quot;limit&quot; );
	 * 	return result == null ? 0 : result;
	 * }
	 * </pre>
	 * 
	 * @param <T> The return value type.
	 * @param name The attribute name.
	 * @return The attribute value or null if it does not exist.
	 */
	@SuppressWarnings( "unchecked" )
	public <T> T getAttribute( String name ) {
		// Null attribute names are not allowed.
		if( name == null ) throw new NullPointerException( "Attribute name cannot be null." );

		return (T)( attributes == null ? null : attributes.get( name ) );
	}

	/**
	 * Set an attribute value. Normally this method is not called directly but is
	 * wrapped by an attribute setter method. Example:
	 * 
	 * <pre>
	 * public void setName( String string ) {
	 * 	setAttribute( &quot;name&quot;, string );
	 * }
	 * </pre>
	 * 
	 * @param name The attribute name.
	 * @param newValue The attribute value.
	 */
	public void setAttribute( String name, Object newValue ) {
		// Null attribute names are not allowed.
		if( name == null ) throw new NullPointerException( "Attribute name cannot be null." );

		// If the old value is equal to the new value no changes are necessary.
		Object oldValue = getAttribute( name );
		if( ObjectUtil.areEqual( oldValue, newValue ) ) return;

		if( newValue instanceof DataNode ) checkForCircularReference( (DataNode)newValue );

		Transaction transaction = new Transaction();
		transaction.setAttribute( this, name, newValue );
		transaction.commit();
	}

	public int getModifiedAttributeCount() {
		return modifiedAttributeCount;
	}

	/**
	 * Copy all attributes and resources from the specified node.
	 * 
	 * @param node
	 */
	public void copy( DataNode node ) {
		for( String key : getAttributeKeys() ) {
			setAttribute( key, node.getAttribute( key ) );
		}
		for( String key : getResourceKeys() ) {
			putResource( key, node.getResource( key ) );
		}
	}

	/**
	 * Fill in any missing attributes and resources from the specified node.
	 * 
	 * @param node
	 */
	public void fill( DataNode node ) {
		for( String key : node.getAttributeKeys() ) {
			if( getAttribute( key ) == null ) setAttribute( key, node.getAttribute( key ) );
		}
		for( String key : node.getResourceKeys() ) {
			if( getResource( key ) == null ) putResource( key, node.getResource( key ) );
		}
	}

	/**
	 * Get the set of attribute keys.
	 * 
	 * @return The attribute key set.
	 */
	public Set<String> getAttributeKeys() {
		return attributes == null ? new HashSet<String>() : attributes.keySet();
	}

	/**
	 * Get the set of resource keys.
	 * 
	 * @return The resource key set.
	 */
	public Set<String> getResourceKeys() {
		return resources == null ? new HashSet<String>() : resources.keySet();
	}

	/**
	 * Get the parent node.
	 * 
	 * @return The parent node or null if there is no parent.
	 */
	public Set<DataNode> getParents() {
		return Collections.unmodifiableSet( parents );
	}

	public Set<List<DataNode>> getNodePaths() {
		return getNodePaths( null );
	}

	public Set<List<DataNode>> getNodePaths( DataNode stop ) {
		Set<List<DataNode>> paths = new HashSet<List<DataNode>>();

		if( this == stop || parents.size() == 0 ) {
			List<DataNode> path = new ArrayList<DataNode>();
			path.add( this );
			paths.add( path );
		} else {
			for( DataNode parent : parents ) {
				for( List<DataNode> path : parent.getNodePaths( stop ) ) {
					path.add( this );
					paths.add( path );
				}
			}
		}

		return paths;
	}

	/**
	 * Get a stored resource. Putting or removing a resource will not modify the
	 * data.
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
	 * Store a resource. Putting or removing a resource will not modify the data.
	 * A resource is removed by setting the resource value to null.
	 * 
	 * @param value
	 */
	public void putResource( String key, Object value ) {
		if( value == null ) {
			if( resources == null ) return;
			resources.remove( key );
			if( resources.size() == 0 ) resources = null;
		} else {
			if( resources == null ) resources = new ConcurrentHashMap<String, Object>();
			resources.put( key, value );
		}
	}

	public void addDataListener( DataListener listener ) {
		listeners.add( listener );
	}

	public void removeDataListener( DataListener listener ) {
		listeners.remove( listener );
	}

	//	@Override
	//	public boolean equals( Object object ) {
	//		return equalsUsingAttributes( object );
	//	}

	/**
	 * Compares the object using the class and attributes for equality testing.
	 */
	public boolean equalsUsingAttributes( Object object ) {
		if( !( object instanceof DataNode ) ) return false;

		DataNode that = (DataNode)object;

		Map<String, Object> thisAttr = this.attributes;
		Map<String, Object> thatAttr = that.attributes;

		if( thisAttr == null && thatAttr == null ) return true;
		if( thisAttr == null && thatAttr != null ) return false;
		if( thisAttr != null && thatAttr == null ) return false;

		if( thisAttr.size() != thatAttr.size() ) return false;

		Set<String> thisKeys = thisAttr.keySet();
		Set<String> thatKeys = thatAttr.keySet();
		for( String key : thisKeys ) {
			if( !thatKeys.contains( key ) ) return false;

			Object thisObject = thisAttr.get( key );
			Object thatObject = thatAttr.get( key );

			if( thisObject instanceof DataNode ) {
				if( !( (DataNode)thisObject ).equalsUsingAttributes( thatObject ) ) return false;
			} else {
				if( !ObjectUtil.areEqual( thisObject, thatObject ) ) return false;
			}
		}

		return true;
	}

	public Transaction startTransaction() {
		return Transaction.startTransaction();
	}
	
	public Transaction getTransaction() {
		return Transaction.getTransaction();
	}

	public boolean commitTransaction() {
		return Transaction.commitTransaction();
	}
	
	/**
	 * Set the modified flag for this node.
	 */
	void modify() {
		if( modified ) return;

		Transaction transaction = new Transaction();
		transaction.modify( this );
		transaction.commit();
	}

	/**
	 * Clear the modified flag for this node and all child nodes.
	 */
	void unmodify( Transaction transaction ) {
		if( !modified ) return;

		boolean commit = transaction == null;
		if( transaction == null ) transaction = new Transaction();

		transaction.unmodify( this );

		// Clear the modified flag of any attribute nodes.
		if( attributes != null ) {
			for( Object child : attributes.values() ) {
				if( child instanceof DataNode ) {
					DataNode childNode = (DataNode)child;
					if( childNode.isModified() ) childNode.unmodify( transaction );
				}
			}
		}

		if( commit ) transaction.commit();
	}

	void doModify() {
		selfModified = true;
		updateModifiedFlag();
	}

	void doUnmodify() {
		selfModified = false;
		modifiedAttributes = null;
		modifiedAttributeCount = 0;
		updateModifiedFlag();
	}

	void updateModifiedFlag() {
		modified = selfModified || modifiedAttributeCount != 0;
	}

	void doSetAttribute( String name, Object oldValue, Object newValue ) {
		// Create the attribute map if necessary.
		if( attributes == null ) attributes = new ConcurrentHashMap<String, Object>();

		// Set the attribute value.
		if( newValue == null ) {
			attributes.remove( name );
			if( oldValue instanceof DataNode ) ( (DataNode)oldValue ).removeParent( this );
		} else {
			attributes.put( name, newValue );
			if( newValue instanceof DataNode ) ( (DataNode)newValue ).addParent( this );
		}

		// Remove the attribute map if necessary.
		if( attributes.size() == 0 ) attributes = null;

		// Update the modified attribute value map.
		Object preValue = modifiedAttributes == null ? null : modifiedAttributes.get( name );
		if( preValue == null ) {
			// Only add the value if there is not an existing previous value.
			if( modifiedAttributes == null ) modifiedAttributes = new ConcurrentHashMap<String, Object>();
			modifiedAttributes.put( name, oldValue == null ? NULL : oldValue );
			modifiedAttributeCount++;
		} else if( ObjectUtil.areEqual( preValue == NULL ? null : preValue, newValue ) ) {
			modifiedAttributes.remove( name );
			modifiedAttributeCount--;
			if( modifiedAttributes.size() == 0 ) modifiedAttributes = null;
		}

		updateModifiedFlag();
	}

	/*
	 * Similar logic is found in DataList.childNodeModified().
	 */
	void attributeNodeModified( boolean modified ) {
		if( modified ) {
			modifiedAttributeCount++;
		} else {
			modifiedAttributeCount--;

			// The reason for the following line is that doUnmodify() is 
			// processed by transactions before processing child and parent nodes.
			if( modifiedAttributeCount < 0 ) modifiedAttributeCount = 0;
		}

		updateModifiedFlag();
	}

	void addParent( DataNode parent ) {
		this.parents.add( parent );
	}

	void removeParent( DataNode parent ) {
		this.parents.remove( parent );
	}

	void dispatchEvent( DataEvent event ) {
		switch( event.getType() ) {
			case DATA_CHANGED: {
				fireDataChanged( (DataChangedEvent)event );
				break;
			}
			case META_ATTRIBUTE: {
				fireMetaAttributeChanged( (MetaAttributeEvent)event );
				break;
			}
			case DATA_ATTRIBUTE: {
				fireDataAttributeChanged( (DataAttributeEvent)event );
				break;
			}
		}
	}

	void checkForCircularReference( DataNode node ) {
		for( DataNode parent : parents ) {
			if( parent == node ) throw new RuntimeException( "Circular reference detected: " + node );
		}
	}

	private void fireDataChanged( DataChangedEvent event ) {
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

}
