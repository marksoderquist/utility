package com.parallelsymmetry.escape.utility.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class DataList<T extends DataNode> extends DataNode implements List<T> {

	private List<T> children;

	public DataList() {}

	private Map<DataNode, DataEvent.Type> modifiedChildren;

	public DataList( T[] children ) {
		for( T child : children ) {
			add( child );
		}
		clearModified();
	}

	public DataList( Collection<T> children ) {
		for( T child : children ) {
			add( child );
		}
		clearModified();
	}

	@Override
	public void clearModified() {
		if( !modified ) return;

		boolean autoCommit = !isTransactionActive();
		if( autoCommit ) startTransaction();

		super.clearModified();

		// Clear the modified flag of any child nodes.
		if( children != null ) {
			for( Object child : children ) {
				if( child instanceof DataNode ) {
					DataNode childNode = (DataNode)child;
					if( childNode.isModified() ) {
						childNode.setTransaction( transaction );
						childNode.clearModified();
					}
				}
			}
		}

		if( autoCommit ) transaction.commit();
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
		if( element == null || contains( element ) ) return false;
		add( size(), element );
		return true;
	}

	@Override
	public void add( int index, T element ) {
		if( element == null || contains( element ) ) return;

		boolean autoCommit = !isTransactionActive();
		if( autoCommit ) startTransaction();
		if( element instanceof DataNode ) isolateNode( (DataNode)element );
		transaction.add( new AddChildAction<T>( this, index, element ) );
		if( autoCommit ) transaction.commit();
	}

	@Override
	public boolean addAll( Collection<? extends T> c ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll( int index, Collection<? extends T> c ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T set( int index, T element ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove( Object o ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T remove( int index ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAll( Collection<?> c ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll( Collection<?> c ) {
		// TODO Auto-generated method stub
		return false;
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

	protected void updateModifiedFlag() {
		super.updateModifiedFlag();
		modified = modified | modifiedChildren.size() != 0;
	}

	protected void dispatchEvent( DataEvent event ) {
		if( event instanceof DataChildEvent ) {
			switch( event.getType() ) {
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
		super.dispatchEvent( event );
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

	private void doAddChild( int index, T child ) {
		if( children == null ) children = new CopyOnWriteArrayList<T>();

		children.add( index, child );
		child.setParent( this );

		if( modifiedChildren == null ) modifiedChildren = new ConcurrentHashMap<DataNode, DataEvent.Type>();
		modifiedChildren.put( child, DataEvent.Type.INSERT );

		updateModifiedFlag();
	}

	private static class AddChildAction<T extends DataNode> extends Action {

		private DataList<T> list;

		private int index;

		private T child;

		public AddChildAction( DataList<T> data, int index, T child ) {
			super( data );
			this.list = data;
			this.index = index;
			this.child = child;
		}

		@Override
		protected ActionResult process() {
			ActionResult result = new ActionResult( this );

			list.doAddChild( index, child );
			result.addEvent( new DataChildEvent( DataEvent.Type.INSERT, getData(), index, child ) );

			return result;
		}

	}

}
