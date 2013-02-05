package com.parallelsymmetry.utility.data;

class UnmodifyOperation extends Operation {

	public UnmodifyOperation( DataNode data ) {
		super( data );
	}

	@Override
	protected OperationResult process() {
		OperationResult result = new OperationResult( this );

		getData().doUnmodify();

		return result;
	}
}