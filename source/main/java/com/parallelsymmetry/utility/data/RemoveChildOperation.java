package com.parallelsymmetry.utility.data;

class RemoveChildOperation<T extends DataNode> extends Operation {

	private DataList<T> list;

	private T child;

	public RemoveChildOperation( DataList<T> list, T child ) {
		super( list );
		this.list = list;
		this.child = child;
	}

	@Override
	protected OperationResult process() {
		OperationResult result = new OperationResult( this );

		int index = list.indexOf( child );
		list.doRemoveChild( child );
		result.addEvent( new DataChildEvent( DataEvent.Action.REMOVE, list, list, index, child ) );

		return result;
	}

}