package com.parallelsymmetry.escape.utility.setting;

public class SettingEvent {

	private String key;

	private String oldValue;

	private String newValue;

	public SettingEvent( String key, String oldValue, String newValue ) {
		this.key = key;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getKey() {
		return key;
	}

	public String getOldValue() {
		return oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

}
