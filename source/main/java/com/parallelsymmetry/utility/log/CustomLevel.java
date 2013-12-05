package com.parallelsymmetry.utility.log;

import java.util.logging.Level;

class CustomLevel extends Level implements Comparable<CustomLevel> {

	private static final long serialVersionUID = -7853455775674488102L;

	private String tag;

	private String ansiColor;

	private String prefix;

	protected CustomLevel( String name, int value, String tag, String ansiColor, String prefix ) {
		super( name, value );
		synchronized( CustomLevel.class ) {
			Log.known.add( this );
		}
		this.tag = tag == null ? "" : tag;
		this.ansiColor = ansiColor == null ? "" : ansiColor;
		this.prefix = prefix == null ? "" : prefix;
	}

	@Override
	public int compareTo( CustomLevel that ) {
		int thisValue = this.intValue();
		int thatValue = that.intValue();
		return ( thisValue < thatValue ? -1 : ( thisValue == thatValue ? 0 : 1 ) );
	}

	public String getTag() {
		return tag;
	}

	public String getAnsiColor() {
		return ansiColor;
	}

	public String getPrefix() {
		return prefix;
	}

}
