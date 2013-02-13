package com.parallelsymmetry.utility.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataList<T extends DataNode> extends DataNode implements List<T> {

	private List<T> children;

	private boolean selfModified;

	private boolean treeModified;

	private Map<DataNode, DataEvent.Action> addRemoveChildren;

	private int modifiedChildCount;

	public DataList() {}

	public DataList( T[] children ) {
		for( T child : children ) {
			add( child );
		}
		setModified( false );
	}

	public DataList( Collection<T> children ) {
		for( T child : children ) {
			add( child );
		}
		setModified( false );
	}

	public boolean isSelfModified() {
		return selfModified;
	}

	public boolean isTreeModified() {
		return treeModified;
	}

	public int getModifiedChildCount() {
		return modifiedChildCount;
	}

	@Override
	public T get( int index ) {
		if( children == null ) throw new ArrayIndexOutOfBoundsException( index );
		return (T)children.get( index );
	}

	@Override
	public int indexOf( Object object ) {
		if( children == null ) return -1;
		return children.indexOf( object );
	}

	@Override
	public int lastIndexOf( Object object ) {
		if( children == null ) return -1;
		return children.lastIndexOf( object );
	}

	@Override
	public boolean contains( Object object ) {
		return children == null ? false : children.contains( object );
	}

	@Override
	public boolean containsAll( Collection<?> collection ) {
		return children == null ? false : children.containsAll( collection );
	}

	@Override
	public boolean add( T element ) {
		if( element == null ) return false;
		add( Integer.MAX_VALUE, element );
		return true;
	}

	@Override
	public void add( int index, T element ) {
		if( element == null ) return;
		if( element instanceof DataNode ) checkForCircularReference( (DataNode)element );

		Transaction.create();
		Transaction.submit( new InsertChildOperation<T>( this, index, element ) );
		Transaction.commit();
	}

	@Override
	public boolean addAll( Collection<? extends T> collection ) {
		return addAll( Integer.MAX_VALUE, collection );
	}

	@Override
	public boolean addAll( int index, Collection<? extends T> collection ) {
		if( collection == null ) return false;

		// Figure out if nodes need to be added.
		List<T> children = new ArrayList<T>();
		for( T node : collection ) {
			if( !contains( node ) ) children.add( node );
		}
		if( children.size() == 0 ) return false;

		// Add the nodes.
		Transaction.create();
		for( T node : children ) {
			add( index, node );
			if( index < Integer.MAX_VALUE ) index++;
		}
		Transaction.commit();

		return true;
	}

	@Override
	public T set( int index, T element ) {
		if( index < 0 || index >= size() ) throw new ArrayIndexOutOfBoundsException( index );
		if( element == null ) throw new NullPointerException();
		if( element instanceof DataNode ) checkForCircularReference( (DataNode)element );

		T result = get( index );

		Transaction.create();

		remove( index );
		add( index, element );

		Transaction.commit();

		return result;
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public boolean remove( Object object ) {
		if( object == null || !( object instanceof DataNode ) || !contains( object ) ) return false;

		Transaction.create();
		Transaction.submit( new RemoveChildOperation<T>( this, (T)object ) );
		Transaction.commit();

		return true;
	}

	@Override
	public T remove( int index ) {
		if( index < 0 || index >= size() ) throw new ArrayIndexOutOfBoundsException( index );
		T child = children.get( index );

		Transaction.create();
		Transaction.submit( new RemoveChildOperation<T>( this, child ) );
		Transaction.commit();

		return child;
	}

	@Override
	public boolean removeAll( Collection<?> collection ) {
		if( collection == null ) return false;

		Transaction.create();
		int count = 0;
		for( Object node : collection ) {
			if( !( node instanceof DataNode ) ) continue;
			if( remove( node ) ) count++;
		}
		Transaction.commit();

		return count > 0;
	}

	@Override
	public boolean retainAll( Collection<?> c ) {
		throw new UnsupportedOperationException( "DataList.retainAll() is not supported." );
	}

	@Override
	public int size() {
		return children == null ? 0 : children.size();
	}

	@Override
	public void clear() {
		if( children == null ) return;
		removeAll( this );
	}

	@Override
	public boolean isEmpty() {
		return children == null;
	}

	@Override
	public Object[] toArray() {
		if( children == null ) return new Object[0];
		return children.toArray();
	}

	@Override
	public <S> S[] toArray( S[] array ) {
		if( children == null ) return array;
		return children.toArray( array );
	}

	@Override
	public Iterator<T> iterator() {
		if( children == null ) return new CopyOnWriteArrayList<T>().iterator();
		return children.iterator();
	}

	@Override
	public ListIterator<T> listIterator() {
		if( children == null ) return new CopyOnWriteArrayList<T>().listIterator();
		return children.listIterator();
	}

	@Override
	public ListIterator<T> listIterator( int index ) {
		if( children == null ) return new CopyOnWriteArrayList<T>().listIterator( index );
		return children.listIterator( index );
	}

	@Override
	public List<T> subList( int fromIndex, int toIndex ) {
		if( children == null ) return new CopyOnWriteArrayList<T>();
		return children.subList( fromIndex, toIndex );
	}

	//	@Override
	//	public boolean equals( Object object ) {
	//		return equalsUsingAttributesAndChildren( object );
	//	}

	public boolean equalsUsingChildren( Object object ) {
		if( !( object instanceof DataList<?> ) ) return false;

		DataList<?> that = (DataList<?>)object;

		List<? extends DataNode> thisChildren = this.children;
		List<? extends DataNode> thatChildren = that.children;

		if( thisChildren == null && thatChildren == null ) return true;
		if( thisChildren == null && thatChildren != null ) return false;
		if( thisChildren != null && thatChildren == null ) return false;

		if( thisChildren.size() != thatChildren.size() ) return false;
		int count = thisChildren.size();
		for( int index = 0; index < count; index++ ) {
			DataNode thisChild = thisChildren.get( index );
			DataNode thatChild = thatChildren.get( index );

			if( !thisChild.equalsUsingAttributes( thatChild ) ) return false;

			if( thisChild instanceof DataList<?> ) {
				if( !( thatChild instanceof DataList<?> ) ) return false;
				if( !( (DataList<?>)thisChild ).equalsUsingChildren( thatChild ) ) return false;
			}
		}

		return true;
	}

	public boolean equalsUsingAttributesAndChildren( Object object ) {
		return equalsUsingAttributes( object ) & equalsUsingChildren( object );
	}

	@Override
	protected void applyMetaValue( String name, Object oldValue, Object newValue ) {
		if( MODIFIED.equals( name ) ) {
			boolean value = (Boolean)newValue;
			if( value == false ) {
				addRemoveChildren = null;
				modifiedChildCount = 0;
			}
			super.applyMetaValue( name, oldValue, newValue );
		}
	}

	@Override
	void unmodify() {
		if( !modified ) return;

		Transaction.create();

		super.unmodify();

		// Clear the modified flag of any child nodes.
		if( children != null ) {
			for( Object child : children ) {
				if( child instanceof DataNode ) {
					DataNode childNode = (DataNode)child;
					if( childNode.isModified() ) childNode.unmodify();
				}
			}
		}

		Transaction.commit();
	}

	@Override
	void updateModifiedFlag() {
		super.updateModifiedFlag();

		selfModified = modified;
		treeModified = modifiedChildCount != 0 | ( addRemoveChildren != null && addRemoveChildren.size() > 0 );

		modified = selfModified | treeModified;
	}

	@Override
	void dispatchEvent( DataEvent event ) {
		super.dispatchEvent( event );

		if( event.getType() == DataEvent.Type.DATA_CHILD ) {
			switch( event.getAction() ) {
				case INSERT: {
					fireChildInsertedEvent( (DataChildEvent)event );
					return;
				}
				case REMOVE: {
					fireChildRemovedEvent( (DataChildEvent)event );
					return;
				}
			}
		}
	}

	/*
	 * Similar logic is found in DataNode.dataNodeChildModified().
	 */
	void listNodeChildModified( boolean modified ) {
		if( modified ) {
			modifiedChildCount++;
		} else {
			modifiedChildCount--;

			// The reason for the following line is that doUnmodify() is 
			// processed by transactions before processing child and parent nodes.
			if( modifiedChildCount < 0 ) modifiedChildCount = 0;
		}

		updateModifiedFlag();
	}

	void doAddChild( int index, T child ) {
		if( children == null ) children = new CopyOnWriteArrayList<T>();

		if( index > children.size() ) index = children.size();

		children.add( index, child );
		child.addParent( this );

		if( addRemoveChildren == null ) {
			addRemoveChildren = new ConcurrentHashMap<DataNode, DataEvent.Action>();
			addRemoveChildren.put( child, DataEvent.Action.INSERT );
		} else {
			if( addRemoveChildren.get( child ) == DataEvent.Action.REMOVE ) {
				addRemoveChildren.remove( child );
				if( addRemoveChildren.size() == 0 ) addRemoveChildren = null;
			} else {
				addRemoveChildren.put( child, DataEvent.Action.INSERT );
			}
		}

		updateModifiedFlag();
	}

	void doRemoveChild( T child ) {
		children.remove( child );
		child.removeParent( this );

		if( addRemoveChildren == null ) {
			addRemoveChildren = new ConcurrentHashMap<DataNode, DataEvent.Action>();
			addRemoveChildren.put( child, DataEvent.Action.REMOVE );
		} else {
			if( addRemoveChildren.get( child ) == DataEvent.Action.INSERT ) {
				addRemoveChildren.remove( child );
				if( addRemoveChildren.size() == 0 ) addRemoveChildren = null;
			} else {
				addRemoveChildren.put( child, DataEvent.Action.REMOVE );
			}
		}

		if( children.size() == 0 ) children = null;

		updateModifiedFlag();
	}

	private void fireChildInsertedEvent( DataChildEvent event ) {
		for( DataListener listener : listeners ) {
			listener.childInserted( event );
		}
	}

	private void fireChildRemovedEvent( DataChildEvent event ) {
		for( DataListener listener : listeners ) {
			listener.childRemoved( event );
		}
	}

}
