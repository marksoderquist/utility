package com.parallelsymmetry.escape.utility.data;


abstract class Action {
	
	private DataNode data;
	
	public Action( DataNode data ) {
		this.data = data;
	}
	
	public DataNode getData() {
		return data;
	}
	
	abstract ActionResult process();
	
}