package com.parallelsymmetry.escape.utility.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.parallelsymmetry.escape.utility.ObjectUtil;
import com.parallelsymmetry.escape.utility.log.Log;

public abstract class DataNode {

	public static final String MODIFIED = "modified";

	protected boolean modified;

	protected Transaction transaction;

	protected Set<DataListener> listeners = new CopyOnWriteArraySet<DataListener>();

	private static final Object NULL = new Object();

	private Map<String, Object> attributes;

	private int modifiedAttributeCount;

	private Map<String, Object> modifiedAttributes;

	private Map<String, Object> resources;

	private DataNode parent;

	/**
	 * Is the node modified. The node is modified if any attribute has been
	 * modified or any child node has been modified since the last time
	 * {@link #clearModified()} was called.
	 * 
	 * @return true if this node or any child nodes are modified, false otherwise.
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * Clear the modified state for this node and all child nodes.
	 */
	public void clearModified() {
		if( !modified ) return;

		boolean atomic = !isTransactionActive();
		if( atomic ) startTransaction();
		transaction.add( new ClearModifiedAction( this ) );

		// Clear the modified flag of any attribute nodes.
		if( attributes != null ) {
			for( Object child : attributes.values() ) {
				if( child instanceof DataNode ) {
					DataNode childNode = (DataNode)child;
					if( childNode.isModified() ) {
						childNode.setTransaction( transaction );
						childNode.clearModified();
					}
				}
			}
		}

		if( atomic ) transaction.commit();
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

		boolean atomic = !isTransactionActive();
		if( atomic ) startTransaction();
		if( newValue instanceof DataNode ) isolateNode( (DataNode)newValue );
		transaction.add( new SetAttributeAction( this, name, oldValue, newValue ) );
		if( atomic ) transaction.commit();
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
	public DataNode getParent() {
		return parent;
	}

	/**
	 * Get tree path of this node. Element zero is the root node and the last
	 * element is this node.
	 * 
	 * @return The tree path of this node.
	 */
	public DataNode[] getTreePath() {
		return getTreePath( null );
	}

	/**
	 * Get tree path of this node. Element zero is the root node and the last
	 * element is this node.
	 * 
	 * @return The tree path of this node.
	 */
	public DataNode[] getTreePath( DataNode stop ) {
		int count = 0;
		DataNode parent = this;
		while( parent != stop ) {
			count++;
			parent = parent.getParent();
		}

		if( stop != null ) count++;

		parent = this;
		DataNode[] path = new DataNode[count];
		for( int index = count - 1; index > -1; index-- ) {
			path[index] = parent;
			parent = parent.getParent();
		}

		return path;
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

	/**
	 * Note: This method is not thread safe for performance reasons.
	 */
	public final boolean isTransactionActive() {
		return transaction != null || ( parent != null && parent.isTransactionActive() );
	}

	/**
	 * Note: This method is not thread safe for performance reasons.
	 */
	public final Transaction startTransaction() {
		if( !isTransactionActive() ) setTransaction( new Transaction() );

		Transaction transaction = getTransaction();
		transaction.incrementDepth();
		return transaction;
	}

	public final Transaction getTransaction() {
		if( transaction == null && parent != null ) return parent.getTransaction();
		return transaction;
	}

	public void setTransaction( Transaction transaction ) {
		if( ObjectUtil.areEqual( this.transaction, transaction ) ) return;
		if( transaction != null && this.transaction != null ) throw new RuntimeException( "Only one transaction can be active at a time." );
		this.transaction = transaction;
	}

	public void addDataListener( DataListener listener ) {
		listeners.add( listener );
	}

	public void removeDataListener( DataListener listener ) {
		listeners.remove( listener );
	}

	protected void attributeNodeModified( boolean modified ) {
		if( modified ) {
			modifiedAttributeCount++;
		} else {
			modifiedAttributeCount--;

			// The reason for the following line is that doClearModifed() is 
			// processed by transactions before processing child and parent nodes.
			if( modifiedAttributeCount < 0 ) modifiedAttributeCount = 0;
		}

		Log.write( Log.WARN, "Modified attribute count( " + toString() + "): " + modifiedAttributeCount );
		updateModifiedFlag();
	}

	protected void dispatchEvent( DataEvent event ) {
		if( event instanceof DataAttributeEvent ) {
			fireDataAttributeChanged( (DataAttributeEvent)event );
		} else if( event instanceof MetaAttributeEvent ) {
			fireMetaAttributeChanged( (MetaAttributeEvent)event );
		} else {
			fireDataChanged( event );
		}
	}

	protected void doClearModified() {
		modifiedAttributes = null;
		modifiedAttributeCount = 0;
		updateModifiedFlag();
	}

	protected void updateModifiedFlag() {
		modified = modifiedAttributeCount != 0;
	}

	void setParent( DataNode parent ) {
		this.parent = parent;
	}

	/**
	 * This method removes the specified node from any parent nodes.
	 */
	void isolateNode( DataNode node ) {
		if( transaction == null ) throw new RuntimeException( "DataNode.isolateNode() should not be called without a transaction." );

		DataNode parent = node.getParent();
		if( parent == null ) return;

		if( parent.attributes != null ) {
			// Because Map.containsValue() traverses the map it only decreases performance.
			String key = null;
			Iterator<Map.Entry<String, Object>> iterator = parent.attributes.entrySet().iterator();
			while( iterator.hasNext() ) {
				Map.Entry<String, Object> entry = iterator.next();
				if( entry.getValue().equals( node ) ) {
					key = entry.getKey();
					break;
				}
			}

			if( key != null ) {
				parent.setTransaction( transaction );
				parent.setAttribute( key, null );
			}
		} else if( parent instanceof DataList ) {
			parent.setTransaction( transaction );
			( (DataList<?>)parent ).remove( node );
		}
	}

	private void doSetAttribute( String name, Object oldValue, Object newValue ) {
		// Create the attribute map if necessary.
		if( attributes == null ) attributes = new ConcurrentHashMap<String, Object>();

		// Set the attribute value.
		if( newValue == null ) {
			attributes.remove( name );
		} else {
			attributes.put( name, newValue );
		}

		// Handle data nodes in attributes.
		if( oldValue instanceof DataNode ) ( (DataNode)oldValue ).setParent( null );
		if( newValue instanceof DataNode ) ( (DataNode)newValue ).setParent( this );

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

	private static class ClearModifiedAction extends Action {

		public ClearModifiedAction( DataNode data ) {
			super( data );
		}

		@Override
		protected ActionResult process() {
			ActionResult result = new ActionResult( this );

			getData().doClearModified();

			return result;
		}
	}

	private static class SetAttributeAction extends Action {

		private String name;

		private Object oldValue;

		private Object newValue;

		public SetAttributeAction( DataNode data, String name, Object oldValue, Object newValue ) {
			super( data );
			this.name = name;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		@Override
		protected ActionResult process() {
			ActionResult result = new ActionResult( this );

			getData().doSetAttribute( name, oldValue, newValue );

			DataEvent.Type type = DataEvent.Type.MODIFY;
			type = oldValue == null ? DataEvent.Type.INSERT : type;
			type = newValue == null ? DataEvent.Type.REMOVE : type;
			result.addEvent( new DataAttributeEvent( type, getData(), name, oldValue, newValue ) );

			return result;
		}

	}

}
