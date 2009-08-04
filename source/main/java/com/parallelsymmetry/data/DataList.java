package com.parallelsymmetry.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.parallelsymmetry.util.ObjectUtil;

public class DataList<T extends DataNode> extends DataNode implements Collection<T>, Iterable<T> {

	private int previousChildrenHashcode;

	List<T> children;

	public DataList() {}

	public DataList( T[] children ) {
		for( T child : children ) {
			add( child );
		}
		setModified( false );
	}

	public void setModified( boolean modified ) {
		if( modified == isModified() ) return;

		this.modified = modified;

		if( modified == false ) {
			previousAttributesHashcode = getAttributesHashcode();
			previousChildrenHashcode = getChildrenHashcode();
		}

		fireDataChanged( new DataEvent( DataEvent.Type.METADATA, this ) );
		processModifiedFlag( modified );
	}

	/**
	 * Get a child at a specified index.
	 * 
	 * @param index
	 * @return
	 */
	public T get( int index ) {
		if( children == null ) throw new ArrayIndexOutOfBoundsException( index );
		return (T)children.get( index );
	}

	/**
	 * Get the index of a specified child.
	 * 
	 * @param node
	 * @return The index of the child.
	 */
	public int getIndex( T node ) {
		if( children == null ) return -1;
		return children.indexOf( node );
	}

	/**
	 * Add a node at the end of the child list.
	 * 
	 * @param node
	 * @return The added node.
	 */
	public boolean add( T node ) {
		return add( children == null ? 0 : children.size(), node );
	}

	/**
	 * Add a node after the node at the specified index.
	 * 
	 * @param index
	 * @param node
	 * @return The added node.
	 */
	public boolean add( int index, T node ) {
		if( node == null ) return false;

		if( isTransactionActive() ) {
			getTransaction().add( new AddChildStep<T>( this, node, index ) );
		} else {
			startTransaction();
			add( index, node );
			commitTransaction();
		}
		return true;
	}

	@Override
	public boolean addAll( Collection<? extends T> collection ) {
		if( collection == null ) return false;

		int count = 0;
		int index = size();
		boolean needTransaction = !isTransactionActive();

		if( needTransaction ) startTransaction();
		for( T node : collection ) {
			if( contains( node ) ) continue;
			getTransaction().add( new AddChildStep<T>( this, node, index++ ) );
			count++;
		}
		if( needTransaction ) commitTransaction();

		return count > 0;
	}

	/**
	 * Test if the node contains a specific child node.
	 * 
	 * @param child
	 * @return True if the node contains the child node, false otherwise.
	 */
	public boolean contains( Object child ) {
		if( children == null ) return false;
		return children.contains( child );
	}

	@Override
	public boolean containsAll( Collection<?> collection ) {
		if( children == null ) return false;
		return children.containsAll( collection );
	}

	/**
	 * Remove a child node.
	 * 
	 * @param node
	 * @return The removed node.
	 */
	public boolean remove( T node ) {
		if( node == null ) return false;

		if( isTransactionActive() ) {
			getTransaction().add( new RemoveChildStep<T>( this, node ) );
		} else {
			startTransaction();
			remove( node );
			commitTransaction();
		}

		return true;
	}

	@Override
	public boolean remove( Object node ) {
		if( !( node instanceof DataNode ) ) return false;
		return remove( (DataNode)node );
	}

	/**
	 * Remove the node at the specified index.
	 * 
	 * @param index
	 * @return The removed node.
	 */
	public void remove( int index ) {
		if( children == null ) throw new ArrayIndexOutOfBoundsException( index );
		remove( children.get( index ) );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public boolean removeAll( Collection<?> collection ) {
		if( collection == null ) return false;

		int count = 0;
		boolean needsTransaction = !isTransactionActive();
		if( needsTransaction ) startTransaction();
		for( Object node : collection ) {
			if( !( node instanceof DataNode ) ) continue;
			getTransaction().add( new RemoveChildStep<T>( this, (T)node ) );
			count++;
		}
		if( needsTransaction ) commitTransaction();

		return count > 0;
	}

	@Override
	public boolean retainAll( Collection<?> c ) {
		throw new UnsupportedOperationException( "DataList.retainAll() is not supported." );
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Get the number of child nodes.
	 * 
	 * @return
	 */
	public int size() {
		if( children == null ) return 0;
		return children.size();
	}

	@Override
	public void clear() {
		if( children == null ) return;
		removeAll( this );
	}

	@Override
	public Object[] toArray() {
		if( children == null ) return new Object[0];
		return children.toArray();
	}

	/**
	 * Get the node children as an array.
	 * 
	 * @param array
	 * @return
	 */
	@Override
	public <S> S[] toArray( S[] array ) {
		if( children == null ) return array;
		return children.toArray( array );
	}

	/**
	 * Get an iterator over the children.
	 * 
	 * @return
	 */
	public Iterator<T> iterator() {
		if( children == null ) return new CopyOnWriteArrayList<T>().iterator();
		return children.iterator();
	}

	@Override
	public int hashCode() {
		return getAttributesHashcode() + getChildrenHashcode();
	}

	@Override
	public boolean equals( Object object ) {
		return equalsUsingAttributesAndChildren( object );
	}

	protected boolean equalsUsingChildren( Object object ) {
		if( !( object instanceof DataList ) ) return false;

		DataList<?> that = (DataList<?>)object;
		return ObjectUtil.areEqual( this.children, that.children );
	}

	protected boolean equalsUsingAttributesAndChildren( Object object ) {
		return equalsUsingAttributes( object ) & equalsUsingChildren( object );
	}

	/**
	 * Are any of the descendants of this node modified.
	 * 
	 * @return The tree modified flag.
	 */
	protected boolean isTreeModified() {
		return super.isTreeModified() || previousChildrenHashcode != getChildrenHashcode();
	}

	protected void unmodifyChildren() {
		super.unmodifyChildren();
		if( children != null ) {
			for( DataNode child : children ) {
				child.setModified( false );
			}
		}
	}

	protected void fireChildAdded( DataChildEvent event ) {
		if( listeners != null ) {
			for( DataListener listener : listeners ) {
				listener.childAdded( event );
			}
		}
		if( getParent() instanceof DataList<?> ) ( (DataList<?>)getParent() ).fireChildAdded( event );
	}

	protected void fireChildRemoved( DataChildEvent event ) {
		if( listeners != null ) {
			for( DataListener listener : listeners ) {
				listener.childRemoved( event );
			}
		}
		if( getParent() instanceof DataList<?> ) ( (DataList<?>)getParent() ).fireChildRemoved( event );
	}

	/**
	 * The children list should not be created until absolutely necessary.
	 */
	private void ensureChildren() {
		if( children == null ) children = new CopyOnWriteArrayList<T>();
	}

	private int getChildrenHashcode() {
		if( children == null ) return 0;
		return children.hashCode();
	}

	private boolean handleAddChild( int index, T node ) {
		if( node == null ) return false;

		isolateNode( node );
		node.setParent( this );

		ensureChildren();
		if( index < 0 ) index = 0;
		if( index > children.size() ) index = children.size();
		children.add( index, node );

		fireChildAdded( new DataChildEvent( DataEvent.Type.INSERT, this, node, index ) );

		return true;
	}

	private boolean handleRemoveChild( T node ) {
		if( node == null || children == null ) return false;
		if( !children.contains( node ) ) return false;

		int index = children.indexOf( node );
		children.remove( node );
		node.setParent( null );
		if( children.size() == 0 ) children = null;

		fireChildRemoved( new DataChildEvent( DataEvent.Type.REMOVE, this, node, index ) );

		return true;
	}

	private static class AddChildStep<T extends DataNode> implements Action {

		private DataList<T> parent;

		private T child;

		private int index;

		public AddChildStep( DataList<T> parent, T child, int index ) {
			this.parent = parent;
			this.child = child;
			this.index = index;
		}

		public boolean commit() {
			return parent.handleAddChild( index, child );
		}

	}

	private static class RemoveChildStep<T extends DataNode> implements Action {

		private DataList<T> parent;

		private T child;

		public RemoveChildStep( DataList<T> parent, T child ) {
			this.parent = parent;
			this.child = child;
		}

		public boolean commit() {
			return parent.handleRemoveChild( child );
		}

	}

}
