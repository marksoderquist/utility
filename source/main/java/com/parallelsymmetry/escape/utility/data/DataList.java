package com.parallelsymmetry.escape.utility.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class DataList<T extends DataNode> extends DataNode implements List<T> {

	private List<T> children;

	private int modifiedChildCount;

	public DataList() {}

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
	public int size() {
		return children == null ? 0 : children.size();
	}

	@Override
	public boolean isEmpty() {
		return children == null;
	}

	@Override
	public boolean contains( Object object ) {
		return children == null ? false : children.contains( object );
	}

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S> S[] toArray( S[] a ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add( T child ) {
		submitAction( new AddChildAction<T>( this, size(), child ) );
		return true;
	}

	@Override
	public boolean remove( Object o ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll( Collection<?> c ) {
		// TODO Auto-generated method stub
		return false;
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
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public T get( int index ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T set( int index, T element ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add( int index, T element ) {
		// TODO Auto-generated method stub

	}

	@Override
	public T remove( int index ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf( Object o ) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf( Object o ) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<T> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<T> listIterator( int index ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> subList( int fromIndex, int toIndex ) {
		// TODO Auto-generated method stub
		return null;
	}

	private void doAddChild( int index, T child ) {
		isolateNode( child );
		child.setParent( this );

		if( children == null ) children = new CopyOnWriteArrayList<T>();
		children.add( index, child );
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

			DataEvent.Type type = DataEvent.Type.MODIFY;
			result.addEvent( new DataChildEvent( DataEvent.Type.INSERT, getData(), index, child ) );

			// getTransaction().nodeModified( this );

			return result;
		}

	}

}
