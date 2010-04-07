package com.parallelsymmetry.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.parallelsymmetry.util.ObjectUtil;

public class DataList<T extends DataNode> extends DataNode implements Collection<T>, List<T> {

	private int currentChildrenHashcode;

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
	@Override
	public int indexOf( Object node ) {
		if( children == null ) return -1;
		return children.indexOf( node );
	}

	@Override
	public int lastIndexOf( Object object ) {
		if( children == null ) return -1;
		return children.lastIndexOf( object );
	}

	/**
	 * Add a node at the end of the child list.
	 * 
	 * @param node
	 * @return The added node.
	 */
	public boolean add( T node ) {
		return addNode( Integer.MAX_VALUE, node );
	}

	public void add( int index, T node ) {
		addNode( index, node );
	}

	@Override
	public boolean addAll( Collection<? extends T> collection ) {
		return addAll( size(), collection );
	}

	@Override
	public boolean addAll( int index, Collection<? extends T> collection ) {
		if( collection == null ) return false;

		int count = 0;
		boolean needTransaction = !isTransactionActive();

		if( needTransaction ) startTransaction();
		for( T node : collection ) {
			if( contains( node ) ) continue;
			getTransaction().add( new AddChildAction<T>( this, node, index++ ) );
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

	@Override
	public T set( int index, T element ) {
		throw new RuntimeException( "The DataList.set(index, node) method is not implemented yet." );
	}

	@Override
	public List<T> subList( int fromIndex, int toIndex ) {
		if( children == null ) return new ArrayList<T>();
		return children.subList( fromIndex, toIndex );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean remove( Object node ) {
		if( !( node instanceof DataNode ) ) return false;
		return removeNode( (T)node );
	}

	/**
	 * Remove the node at the specified index.
	 * 
	 * @param index
	 * @return The removed node.
	 */
	public T remove( int index ) {
		if( children == null ) throw new ArrayIndexOutOfBoundsException( index );
		T node = children.get( index );
		removeNode( node );
		return node;
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
			getTransaction().add( new RemoveChildAction<T>( this, (T)node ) );
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
		if( children == null ) return new ArrayList<T>().iterator();
		return children.iterator();
	}

	@Override
	public ListIterator<T> listIterator() {
		if( children == null ) return new ArrayList<T>().listIterator();
		return children.listIterator();
	}

	@Override
	public ListIterator<T> listIterator( int index ) {
		if( children == null ) return new ArrayList<T>().listIterator( index );
		return children.listIterator( index );
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
		if( !( object instanceof DataList<?> ) ) return false;

		DataList<?> that = (DataList<?>)object;
		return ObjectUtil.areEqual( this.children, that.children );
	}

	protected boolean equalsUsingAttributesAndChildren( Object object ) {
		return equalsUsingAttributes( object ) & equalsUsingChildren( object );
	}

	@Override
	protected void updateCurrentState() {
		currentChildrenHashcode = calcChildrenHashcode();
		super.updateCurrentState();
	}

	/**
	 * Are any of the descendants of this node modified.
	 * 
	 * @return The tree modified flag.
	 */
	protected boolean isTreeModified() {
		return super.isTreeModified() || currentChildrenHashcode != previousChildrenHashcode;
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
	 * Add a node after the node at the specified index.
	 * 
	 * @param index
	 * @param node
	 * @return The added node.
	 */
	private boolean addNode( int index, T node ) {
		if( node == null ) return false;

		if( isTransactionActive() ) {
			getTransaction().add( new AddChildAction<T>( this, node, index ) );
		} else {
			startTransaction();
			add( index, node );
			commitTransaction();
		}

		return true;
	}

	/**
	 * Remove a child node.
	 * 
	 * @param node
	 * @return The removed node.
	 */
	private boolean removeNode( T node ) {
		if( node == null ) return false;

		if( isTransactionActive() ) {
			getTransaction().add( new RemoveChildAction<T>( this, node ) );
		} else {
			startTransaction();
			remove( node );
			commitTransaction();
		}

		return true;
	}

	/**
	 * The children list should not be created until absolutely necessary.
	 */
	private void ensureChildren() {
		if( children == null ) children = new CopyOnWriteArrayList<T>();
	}

	private int getChildrenHashcode() {
		return currentChildrenHashcode;
	}

	private int calcChildrenHashcode() {
		if( children == null ) return 0;
		return children.hashCode();
	}

	private static class AddChildAction<T extends DataNode> extends Action {

		private DataList<T> parent;

		private T child;

		private int index;

		private DataChildEvent event;

		public AddChildAction( DataList<T> parent, T child, int index ) {
			this.parent = parent;
			this.child = child;
			this.index = index;
		}

		@Override
		public boolean commit() {
			if( child == null ) return false;

			parent.isolateNode( child );
			child.setParent( parent );

			parent.ensureChildren();
			if( index < 0 ) index = 0;
			if( index > parent.children.size() ) index = parent.children.size();
			parent.children.add( index, child );

			event = new DataChildEvent( DataEvent.Type.INSERT, parent, child, index );

			parent.getTransaction().nodeModified( parent );

			return true;
		}

		@Override
		public void fireEvents() {
			if( event != null ) parent.fireChildAdded( event );
		}

	}

	private static class RemoveChildAction<T extends DataNode> extends Action {

		private DataList<T> parent;

		private T child;

		private DataChildEvent event;

		public RemoveChildAction( DataList<T> parent, T child ) {
			this.parent = parent;
			this.child = child;
		}

		@Override
		public boolean commit() {
			if( child == null || parent.children == null ) return false;
			if( !parent.children.contains( child ) ) return false;

			int index = parent.children.indexOf( child );
			parent.children.remove( child );
			child.setParent( null );
			if( parent.children.size() == 0 ) parent.children = null;

			event = new DataChildEvent( DataEvent.Type.REMOVE, parent, child, index );

			parent.getTransaction().nodeModified( parent );

			return true;
		}

		@Override
		public void fireEvents() {
			if( event != null ) parent.fireChildRemoved( event );
		}

	}

}
