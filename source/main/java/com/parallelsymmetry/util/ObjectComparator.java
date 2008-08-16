package com.parallelsymmetry.util;

import java.util.Comparator;

public class ObjectComparator implements Comparator<Object> {

	@Override
	public int compare( Object o1, Object o2 ) {
		if( o1 == null & o2 == null ) return 0;
		if( o1 == null & o2 != null ) return -1;
		if( o1 != null & o2 == null ) return 1;
		return o1.toString().compareTo( o2.toString() );
	}

}
