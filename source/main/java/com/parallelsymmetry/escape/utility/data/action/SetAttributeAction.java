package com.parallelsymmetry.escape.utility.data.action;

import com.parallelsymmetry.escape.utility.data.Action;
import com.parallelsymmetry.escape.utility.data.ActionResult;
import com.parallelsymmetry.escape.utility.data.DataNode;

public class SetAttributeAction extends Action {

	private String name;

	private Object value;

	public SetAttributeAction( DataNode data, String name, Object value ) {
		super( data );
		this.name = name;
		this.value = value;
	}

	@Override
	protected ActionResult process() {
		ActionResult result = new ActionResult( this );

		getData().doSetAttribute( name, value );

		return result;
	}

}
