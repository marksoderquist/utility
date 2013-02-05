package com.parallelsymmetry.utility.data;

class ModifyOperation extends Operation {

	public ModifyOperation( DataNode data ) {
		super( data );
	}

	@Override
	protected OperationResult process() {
		OperationResult result = new OperationResult( this );

		getData().doModify();

		return result;
	}

}