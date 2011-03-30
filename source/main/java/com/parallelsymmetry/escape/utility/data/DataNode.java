package com.parallelsymmetry.escape.utility.data;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.parallelsymmetry.escape.utility.ObjectUtil;
import com.parallelsymmetry.escape.utility.log.Log;

public abstract class DataNode {

	public static final String MODIFIED = "modified";

	private static final Object NULL = new Object();

	private boolean modified;

	private Map<String, Object> attributes;

	private int modifiedAttributeCount;

	private int modifiedChildCount;

	private Map<String, Object> modifiedAttributes;

	private Map<String, Object> resources;

	private Transaction transaction;

	private DataNode parent;

	private Set<DataListener> listeners = new CopyOnWriteArraySet<DataListener>();

	public boolean isModified() {
		return modified;
	}

	public void clearModified() {
		if( !modified ) return;

		boolean autoCommit = !isTransactionActive();
		if( autoCommit ) startTransaction();

		transaction.add( new ClearModifiedAction( this ) );

		if( autoCommit ) transaction.commit();
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

		boolean autoCommit = !isTransactionActive();
		if( autoCommit ) startTransaction();

		// If the new value is a data node it must be isolated.
		if( newValue instanceof DataNode ) isolateNode( (DataNode)newValue );

		// Submit the change action.
		transaction.add( new SetAttributeAction( this, name, oldValue, newValue ) );

		if( autoCommit ) transaction.commit();
	}

	public int getModifiedAttributeCount() {
		return modifiedAttributeCount;
	}

	public DataNode getParent() {
		return parent;
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
			if( resources.size() == 0 ) resources = null;
		} else {
			if( resources == null ) resources = new ConcurrentHashMap<String, Object>();
			resources.put( key, value );
		}
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public Transaction startTransaction() {
		setTransaction( new Transaction() );
		return transaction;
	}

	public void setTransaction( Transaction transaction ) {
		if( ObjectUtil.areEqual( this.transaction, transaction ) ) return;
		if( transaction != null && this.transaction != null ) throw new RuntimeException( "Only one transaction can be active at a time." );
		this.transaction = transaction;
	}

	public boolean isTransactionActive() {
		return transaction != null;
	}

	public void addDataListener( DataListener listener ) {
		listeners.add( listener );
	}

	public void removeDataListener( DataListener listener ) {
		listeners.remove( listener );
	}

	//	protected void submitAction( Action action ) {
	//		if( isTransactionActive() ) {
	//			getTransaction().add( action );
	//			return;
	//		}
	//
	//		Transaction transaction = startTransaction();
	//		submitAction( action );
	//		transaction.commit();
	//	}

	protected void attributeModified( boolean modified ) {
		if( modified ) {
			modifiedAttributeCount++;
		} else {
			modifiedAttributeCount--;
			if( modifiedAttributeCount < 0 ) throw new RuntimeException( "Modified attribute count less than zero." );
		}

		updateModifiedFlag();
	}

	protected void childModified( boolean modified ) {
		if( modified ) {
			modifiedChildCount++;
		} else {
			modifiedChildCount--;
			if( modifiedChildCount < 0 ) throw new RuntimeException( "Modified child count less than zero." );
		}

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

	void setParent( DataNode parent ) {
		this.parent = parent;
	}

	/**
	 * This method removes the specified node from any parent nodes.
	 */
	@SuppressWarnings( "unchecked" )
	void isolateNode( DataNode node ) {
		if( transaction == null ) throw new RuntimeException( "DataNode.isolateNode() should not be called without a transaction." );
		DataNode parent = node.getParent();

		if( parent == null ) return;

		if( parent.attributes != null && parent.attributes.containsValue( node ) ) {
			parent.setTransaction( transaction );

			// If the node is an attribute.
			Iterator<Map.Entry<String, Object>> iterator = parent.attributes.entrySet().iterator();
			while( iterator.hasNext() ) {
				Map.Entry<String, Object> entry = iterator.next();
				if( entry.getValue().equals( node ) ) {
					Log.write( Log.WARN, "Found entry: " + entry.getKey() );
					parent.setAttribute( entry.getKey(), null );
					break;
				}
			}
		} else if( parent instanceof DataList ) {
			parent.setTransaction( getTransaction() );
			( (DataList<DataNode>)parent ).remove( node );
		}

		//getTransaction().nodeModified( parent );
	}

	private void updateModifiedFlag() {
		modified = modifiedAttributeCount != 0 | modifiedChildCount != 0;
	}

	private void doClearModified() {
		this.modified = false;
		modifiedAttributes = null;
		modifiedAttributeCount = 0;
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

		// Update the modified flag.
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

			Log.write( Log.WARN, "Set attribute: " + name + " from " + oldValue + " to " + newValue );
			getData().doSetAttribute( name, oldValue, newValue );

			DataEvent.Type type = DataEvent.Type.MODIFY;
			type = oldValue == null ? DataEvent.Type.INSERT : type;
			type = newValue == null ? DataEvent.Type.REMOVE : type;
			result.addEvent( new DataAttributeEvent( type, getData(), name, oldValue, newValue ) );

			return result;
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

}
