package com.parallelsymmetry.utility.data;

class InsertChildOperation<T extends DataNode> extends Operation {

	private DataList<T> list;

	private int index;

	private T child;

	public InsertChildOperation( DataList<T> list, int index, T child ) {
		super( list );
		this.list = list;
		this.index = index;
		this.child = child;
	}

	@Override
	protected OperationResult process() {
		OperationResult result = new OperationResult( this );

		if( index == Integer.MAX_VALUE ) index = list.size();
		list.doAddChild( index, child );
		result.addEvent( new DataChildEvent( DataEvent.Action.INSERT, list, list, index, child ) );

		return result;
	}

}