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
	
	private boolean selfModified;
	
	private boolean treeModified;

	public DataList() {}

	private Map<DataNode, DataEvent.Type> addRemoveChildren;

	private int modifiedChildCount;

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
	
	public boolean isSelfModified() {
		return selfModified;
	}
	
	public boolean isTreeModified() {
		return treeModified;
	}

	@Override
	public void clearModified() {
		if( !modified ) return;

		boolean atomic = !isTransactionActive();
		if( atomic ) startTransaction();

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

		if( atomic ) transaction.commit();
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

		boolean atomic = !isTransactionActive();
		if( atomic ) startTransaction();
		if( element instanceof DataNode ) isolateNode( (DataNode)element );
		transaction.add( new AddChildAction<T>( this, index, element ) );
		if( atomic ) transaction.commit();
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
		if( index < 0 || index >= size() ) throw new ArrayIndexOutOfBoundsException( index );
		if( element == null ) throw new NullPointerException();

		boolean atomic = !isTransactionActive();
		if( atomic ) startTransaction();
		if( element instanceof DataNode ) isolateNode( (DataNode)element );
		T result = get( index );
		transaction.add( new RemoveChildAction<T>( this, result ) );
		transaction.add( new AddChildAction<T>( this, index, element ) );
		if( atomic ) transaction.commit();

		return result;
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public boolean remove( Object object ) {
		if( object == null || !( object instanceof DataNode ) || !contains( object ) ) return false;

		boolean atomic = !isTransactionActive();
		if( atomic ) startTransaction();
		transaction.add( new RemoveChildAction<T>( this, (T)object ) );
		if( atomic ) transaction.commit();

		return true;
	}

	@Override
	public T remove( int index ) {
		T child = children.get( index );
		remove( child );
		return child;
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

	public int getModifiedChildCount() {
		return modifiedChildCount;
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

	protected void childNodeModified( boolean modified ) {
		if( modified ) {
			modifiedChildCount++;
		} else {
			modifiedChildCount--;

			// The reason for the following line is that doClearModifed() is 
			// processed by transactions before processing child and parent nodes.
			if( modifiedChildCount < 0 ) modifiedChildCount = 0;
		}

		updateModifiedFlag();
	}

	protected void updateModifiedFlag() {
		super.updateModifiedFlag();
		selfModified = modified;
		
		int addRemoveChildCount = addRemoveChildren == null ? 0 : addRemoveChildren.size();
		treeModified = modifiedChildCount != 0 | addRemoveChildCount != 0;
		
		modified = selfModified | treeModified;
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

	protected void doClearModified() {
		addRemoveChildren = null;
		modifiedChildCount = 0;
		super.doClearModified();
	}

	private void doAddChild( int index, T child ) {
		if( children == null ) children = new CopyOnWriteArrayList<T>();

		children.add( index, child );
		child.setParent( this );

		if( addRemoveChildren == null ) {
			addRemoveChildren = new ConcurrentHashMap<DataNode, DataEvent.Type>();
			addRemoveChildren.put( child, DataEvent.Type.INSERT );
		} else {
			if( addRemoveChildren.get( child ) == DataEvent.Type.REMOVE ) {
				addRemoveChildren.remove( child );
				if( addRemoveChildren.size() == 0 ) addRemoveChildren = null;
			} else {
				addRemoveChildren.put( child, DataEvent.Type.INSERT );
			}
		}

		updateModifiedFlag();
	}

	private void doRemoveChild( T child ) {
		children.remove( child );
		child.setParent( null );

		if( addRemoveChildren == null ) {
			addRemoveChildren = new ConcurrentHashMap<DataNode, DataEvent.Type>();
			addRemoveChildren.put( child, DataEvent.Type.REMOVE );
		} else {
			if( addRemoveChildren.get( child ) == DataEvent.Type.INSERT ) {
				addRemoveChildren.remove( child );
				if( addRemoveChildren.size() == 0 ) addRemoveChildren = null;
			} else {
				addRemoveChildren.put( child, DataEvent.Type.REMOVE );
			}
		}

		if( children.size() == 0 ) children = null;

		updateModifiedFlag();
	}

	private static class AddChildAction<T extends DataNode> extends Action {

		private DataList<T> list;

		private int index;

		private T child;

		public AddChildAction( DataList<T> list, int index, T child ) {
			super( list );
			this.list = list;
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

	private static class RemoveChildAction<T extends DataNode> extends Action {

		private DataList<T> list;

		private T child;

		public RemoveChildAction( DataList<T> list, T child ) {
			super( list );
			this.list = list;
			this.child = child;
		}

		@Override
		protected ActionResult process() {
			ActionResult result = new ActionResult( this );

			int index = list.children.indexOf( child );
			list.doRemoveChild( child );
			result.addEvent( new DataChildEvent( DataEvent.Type.REMOVE, list, index, child ) );

			return result;
		}

	}

}
