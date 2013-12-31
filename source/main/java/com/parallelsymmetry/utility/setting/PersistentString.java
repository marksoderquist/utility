package com.parallelsymmetry.utility.setting;


public class PersistentString implements Persistent {

	private String string;

	public PersistentString() {}

	public PersistentString( String string ) {
		this.string = string;
	}

	@Override
	public void loadSettings( Settings settings ) {
		string = settings.get( "string", null );
	}

	@Override
	public void saveSettings( Settings settings ) {
		settings.put( "string", string );
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public int hashCode() {
		return string == null ? 0 : string.hashCode();
	}

	@Override
	public boolean equals( Object object ) {
		if( !( object instanceof PersistentString ) ) return false;
		PersistentString that = (PersistentString)object;

		if( this.string == null ) return that.string == null;
		return this.string.equals( that.string );
	}

}
