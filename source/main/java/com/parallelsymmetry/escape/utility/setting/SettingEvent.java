package com.parallelsymmetry.escape.utility.setting;

public class SettingEvent {

	private String path;

	private String oldValue;

	private String newValue;

	public SettingEvent( String path, String oldValue, String newValue ) {
		this.path = path;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getPath() {
		return path;
	}

	public String getOldValue() {
		return oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

}
