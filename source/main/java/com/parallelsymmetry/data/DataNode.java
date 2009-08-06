package com.parallelsymmetry.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import com.parallelsymmetry.util.ObjectUtil;

public class DataNode implements Comparable<DataNode> {

	public static final String MODIFIED = DataNode.class.getName() + ":modified";

	protected boolean modified;

	protected int previousAttributesHashcode;

	protected boolean previousCheckModified;

	protected Map<String, Object> attributes;

	protected Map<String, Object> metadata;

	protected Set<DataListener> listeners;

	private DataNode parent;

	private int modifiedChildren;

	private Map<String, Object> resources;

	private Transaction transaction;

	public DataNode() {}

	/**
	 * Is the node or any children modified.
	 * 
	 * @return The modified flag.
	 */
	public boolean isModified() {
		// NOTE: This method must return quickly, it is called often.
		return modified || isSelfModified() || isTreeModified();
	}

	public void setModified( boolean modified ) {
		if( modified == isModified() ) return;

		this.modified = modified;

		if( modified == false ) {
			previousAttributesHashcode = getAttributesHashcode();
		}

		fireDataChanged( new DataEvent( DataEvent.Type.METADATA, this ) );
		processModifiedFlag( modified );
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
	 * Get an attribute from the node.
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	public <T> T getAttribute( String key ) {
		if( attributes == null ) return null;
		return (T)attributes.get( key );
	}

	/**
	 * Set an attribute in the node. Setting or removing an attribute will modify
	 * the node. Attributes are removed by setting the value to null.
	 * 
	 * @param key
	 * @param value
	 */
	public void setAttribute( String key, Object value ) {
		if( key == value ) throw new RuntimeException( "The attribute map cannot allow the key and value to be the same." );
		submitAction( new SetAttributeAction( this, key, value ) );
	}

	/**
	 * Get a metadata attribute from the node.
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	public <T> T getMetadata( String key ) {
		if( metadata == null ) return null;
		return (T)metadata.get( key );
	}

	/**
	 * Set a metadata attribute in the node. Setting or removing a metadata
	 * attribute will not modify the node. Metadata attributes are removed by
	 * setting the value to null.
	 * 
	 * @param key
	 * @param value
	 */
	public void setMetadata( String key, Object value ) {
		if( key == value ) throw new RuntimeException( "The metadata map cannot allow the key and value to be the same." );

		submitAction( new SetMetadataStep( this, key, value ) );
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
	 * Get a resource from the node.
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
	 * Set a resource in the node. Setting or removing a resource will not modify
	 * the node. A resource is removed by setting the value to null.
	 * 
	 * @param value
	 */
	public void setResource( String key, Object value ) {
		if( value == null ) {
			if( resources == null ) return;
			resources.remove( key );
		} else {
			ensureResources();
			resources.put( key, value );
		}
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
	 * Copy all attributes and resources from the specified node.
	 * 
	 * @param node
	 */
	public void copy( DataNode node ) {
		for( String key : getAttributeKeys() ) {
			setAttribute( key, node.getAttribute( key ) );
		}
		for( String key : getResourceKeys() ) {
			setResource( key, node.getResource( key ) );
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
			if( getResource( key ) == null ) setResource( key, node.getResource( key ) );
		}
	}

	public void addDataListener( DataListener listener ) {
		if( listeners == null ) listeners = new CopyOnWriteArraySet<DataListener>();
		listeners.add( listener );
	}

	public void removeDataListener( DataListener listener ) {
		if( listeners == null ) return;
		listeners.remove( listener );
		if( listeners.size() == 0 ) listeners = null;
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
	public final void startTransaction() {
		if( !isTransactionActive() ) transaction = new Transaction();
		getTransaction().incrementDepth();
	}

	/**
	 * Note: This method is not thread safe for performance reasons.
	 */
	public final void commitTransaction() {
		int depth = getTransaction().decrementDepth();
		if( depth > 0 ) return;

		boolean changed = false;
		if( transaction != null ) {
			for( Action activity : transaction ) {
				if( !activity.commit() ) continue;
				changed = true;
			}
		}

		transaction = null;

		if( changed ) {
			fireDataChanged( new DataEvent( DataEvent.Type.CHANGE, this ) );
			processModifiedFlag( isModified() );
		}
	}

	/**
	 * Note: This method is not thread safe for performance reasons.
	 */
	public final void rollbackTransaction() {
		transaction = null;
	}

	@Override
	public int compareTo( DataNode object ) {
		if( object == null ) return 1;
		return toString().compareTo( object.toString() );
	}

	@Override
	public int hashCode() {
		return getClass().hashCode() + getAttributesHashcode();
	}

	@Override
	public boolean equals( Object object ) {
		return equalsUsingAttributes( object );
	}

	/**
	 * Compares the object using the class and attributes for equality testing.
	 */
	protected boolean equalsUsingAttributes( Object object ) {
		if( !( object instanceof DataNode ) ) return false;

		DataNode that = (DataNode)object;

		return this.getClass() == that.getClass() && ObjectUtil.areEqual( this.attributes, that.attributes );
	}

	/**
	 * Is the node itself modified, ignoring any descendants.
	 * 
	 * @return The self modified flag.
	 */
	protected boolean isSelfModified() {
		return previousAttributesHashcode != getAttributesHashcode();
	}

	/**
	 * Are any of the descendants of this node modified.
	 * 
	 * @return The tree modified flag.
	 */
	protected boolean isTreeModified() {
		return modifiedChildren != 0;
	}

	protected void childModified( boolean modified ) {
		if( modified ) {
			modifiedChildren++;
		} else {
			if( modifiedChildren > 0 ) modifiedChildren--;
		}

		if( !isTransactionActive() ) processModifiedFlag( isModified() );
	}

	protected void updateModifiedFlag() {
		processModifiedFlag( isModified() );
	}

	protected void processModifiedFlag( boolean modified ) {
		boolean changed = previousCheckModified != modified;
		if( changed ) {
			previousCheckModified = modified;

			// If the current modified flag is false notify the children.
			if( !modified ) unmodifyChildren();

			// Notify the parent if there is one.
			if( parent != null ) parent.childModified( modified );
		}

		// Fire events.
		if( modified ) {
			fireMetadataChanged( new DataMetadataEvent( this, MODIFIED, false, true ) );
		} else {
			fireMetadataChanged( new DataMetadataEvent( this, MODIFIED, true, false ) );
		}
	}

	protected void unmodifyChildren() {
		if( attributes != null ) {
			for( Object child : attributes.values() ) {
				if( child instanceof DataNode ) {
					( (DataNode)child ).setModified( false );
				}
			}
		}
	}

	protected int getAttributesHashcode() {
		return attributes == null ? 0 : attributes.hashCode();
	}

	/**
	 * Set the parent node.
	 * 
	 * @param node
	 */
	protected final void setParent( DataNode node ) {
		this.parent = node;
	}

	/**
	 * This method removes the specified node from any parent nodes.
	 */
	@SuppressWarnings( "unchecked" )
	protected final boolean isolateNode( DataNode node ) {
		DataNode parent = node.getParent();

		if( parent == null ) return true;

		if( parent.attributes != null && parent.attributes.containsValue( node ) ) {
			// If the node is an attribute.
			Iterator<Map.Entry<String, Object>> iterator = parent.attributes.entrySet().iterator();
			while( iterator.hasNext() ) {
				Map.Entry<String, Object> entry = iterator.next();
				if( entry.getValue().equals( node ) ) {
					iterator.remove();
					break;
				}
			}
			//
		} else if( parent instanceof DataList && ( (DataList)parent ).children != null ) {
			// If the node is a child.
			( (DataList<DataNode>)parent ).remove( node );
		}

		return true;
	}

	protected final Transaction getTransaction() {
		if( transaction == null && parent != null ) return parent.getTransaction();
		return transaction;
	}

	protected final boolean submitAction( Action action ) {
		if( isTransactionActive() ) {
			getTransaction().add( action );
			return true;
		}

		startTransaction();
		submitAction( action );
		commitTransaction();
		return false;
	}

	protected final void triggerChangedEvent() {
		fireDataChanged( new DataEvent( DataEvent.Type.CHANGE, this ) );
	}

	protected final void fireDataChanged( DataEvent event ) {
		if( listeners != null ) {
			for( DataListener listener : listeners ) {
				listener.dataChanged( event );
			}
		}
		if( parent != null ) parent.fireDataChanged( event );
	}

	protected final void fireMetadataChanged( DataMetadataEvent event ) {
		if( listeners != null ) {
			for( DataListener listener : listeners ) {
				listener.metadataChanged( event );
			}
		}

		if( !MODIFIED.equals( event.getKey() ) && parent != null ) parent.fireMetadataChanged( event );
	}

	protected final void fireAttributeModified( DataAttributeEvent event ) {
		if( listeners != null ) {
			for( DataListener listener : listeners ) {
				listener.attributeChanged( event );
			}
		}

		if( parent != null ) parent.fireAttributeModified( event );
	}

	/**
	 * The attribute map should not be created until absolutely necessary.
	 */
	private final void ensureAttributes() {
		if( attributes == null ) attributes = new ConcurrentHashMap<String, Object>();
	}

	/**
	 * The metadata map should not be created until absolutely necessary.
	 */
	private final void ensureMetadata() {
		if( metadata == null ) metadata = new ConcurrentHashMap<String, Object>();
	}

	/**
	 * The resource map should not be created until absolutely necessary.
	 */
	private final void ensureResources() {
		if( resources == null ) resources = new ConcurrentHashMap<String, Object>();
	}

	/**
	 * Handle setting an attribute.
	 * 
	 * @param key
	 * @param value
	 * @return True if the attribute value changed, false otherwise.
	 */
	private final boolean handleSetAttribute( String key, Object value ) {
		Object oldValue = null;
		if( value == null ) {
			if( attributes == null ) return false;
			oldValue = attributes.remove( key );
			if( oldValue instanceof DataNode ) ( (DataNode)oldValue ).setParent( null );
			if( attributes.size() == 0 ) attributes = null;
		} else {
			if( value instanceof DataNode ) isolateNode( (DataNode)value );
			ensureAttributes();
			oldValue = attributes.get( key );
			if( value.equals( oldValue ) ) return false;
			attributes.put( key, value );
			if( value instanceof DataNode ) ( (DataNode)value ).setParent( this );
		}

		fireAttributeModified( new DataAttributeEvent( this, key, oldValue, value ) );

		return true;
	}

	/**
	 * Handle setting a metadata attribute.
	 * 
	 * @param key
	 * @param value
	 * @return True if the attribute value changed, false otherwise.
	 */
	private final boolean handleSetMetadata( String key, Object value ) {
		Object oldValue = null;
		if( value == null ) {
			if( metadata == null ) return false;
			oldValue = metadata.remove( key );
			if( metadata.size() == 0 ) metadata = null;
		} else {
			ensureMetadata();
			oldValue = metadata.get( key );
			if( value.equals( oldValue ) ) return false;
			metadata.put( key, value );
		}

		fireMetadataChanged( new DataMetadataEvent( this, key, oldValue, value ) );

		return true;
	}

	protected interface Action {

		/**
		 * Entry point to execute transaction actions.
		 * 
		 * @return True if the action was successful, false otherwise.
		 */
		public boolean commit();

	}

	protected static final class Transaction extends CopyOnWriteArrayList<Action> {

		private static final long serialVersionUID = 1487819772538317960L;

		private AtomicInteger depth = new AtomicInteger();

		public int getDepth() {
			return depth.get();
		}

		public int incrementDepth() {
			return depth.incrementAndGet();
		}

		public int decrementDepth() {
			return depth.decrementAndGet();
		}

	}

	static final class SetAttributeAction implements Action {

		private DataNode node;

		private String key;

		private Object value;

		public SetAttributeAction( DataNode node, String key, Object value ) {
			this.node = node;
			this.key = key;
			this.value = value;
		}

		public boolean commit() {
			return node.handleSetAttribute( key, value );
		}

	}

	static final class SetMetadataStep implements Action {

		private DataNode node;

		private String key;

		private Object value;

		public SetMetadataStep( DataNode node, String key, Object value ) {
			this.node = node;
			this.key = key;
			this.value = value;
		}

		public boolean commit() {
			return node.handleSetMetadata( key, value );
		}

	}

}
