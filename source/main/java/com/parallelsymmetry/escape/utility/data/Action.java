package com.parallelsymmetry.escape.utility.data;

import com.parallelsymmetry.escape.utility.JavaUtil;

public abstract class Action {

	private DataNode data;

	public Action( DataNode data ) {
		this.data = data;
	}

	public DataNode getData() {
		return data;
	}

	public String toString() {
		String name = JavaUtil.getClassName( getClass().getName() );
		return name + "[" + System.identityHashCode( this ) + "]";
	}

	protected abstract ActionResult process();

}
