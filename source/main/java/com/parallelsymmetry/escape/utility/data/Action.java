package com.parallelsymmetry.escape.utility.data;


abstract class Action {
	
	private DataObject data;
	
	public Action( DataObject data ) {
		this.data = data;
	}
	
	public DataObject getData() {
		return data;
	}
	
	abstract ActionResult process();
	
}