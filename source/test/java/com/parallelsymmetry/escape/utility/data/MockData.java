package com.parallelsymmetry.escape.utility.data;

class MockData extends DataObject {

	public int getX() {
		Integer x = getAttribute( "x" );
		return x == null ? 0 : x;
	}

	public void setX( int x ) {
		setAttribute( "x", x == 0 ? null : x );
	}

	public int getY() {
		Integer y = getAttribute( "y" );
		return y == null ? 0 : y;
	}

	public void setY( int y ) {
		setAttribute( "y", y == 0 ? null : y );
	}

	public int getZ() {
		Integer z = getAttribute( "z" );
		return z == null ? 0 : z;
	}

	public void setZ( int z ) {
		setAttribute( "z", z == 0 ? null : z );
	}

}