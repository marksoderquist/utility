package com.parallelsymmetry.escape.utility.data;


public abstract class Action {
	
	private DataNode data;
	
	public Action( DataNode data ) {
		this.data = data;
	}
	
	public DataNode getData() {
		return data;
	}
	
	protected abstract ActionResult process();
	
}