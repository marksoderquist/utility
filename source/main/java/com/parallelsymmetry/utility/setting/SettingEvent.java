package com.parallelsymmetry.utility.setting;

public class SettingEvent {

	private String fullPath;

	private String nodePath;

	private String key;

	private String oldValue;

	private String newValue;

	public SettingEvent( String path, String oldValue, String newValue ) {
		this.fullPath = path;
		this.oldValue = oldValue;
		this.newValue = newValue;

		this.nodePath = Settings.getParentPath( path );
		this.key = Settings.getSettingKey( path );
	}

	public String getFullPath() {
		return fullPath;
	}

	public String getNodePath() {
		return nodePath;
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
