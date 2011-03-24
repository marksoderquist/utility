package com.parallelsymmetry.escape.utility.data;

public class ClearModifiedAction extends Action {

	public ClearModifiedAction( DataNode data ) {
		super( data );
	}

	@Override
	protected ActionResult process() {
		ActionResult result = new ActionResult( this );

		getData().doSetModified( false );

		return result;
	}
}
