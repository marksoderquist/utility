package com.parallelsymmetry.escape.utility.setting;

public class SettingEvent {

	private String fullPath;
	
	private String nodePath;
	
	private String entryKey;

	private String oldValue;

	private String newValue;

	public SettingEvent( String path, String oldValue, String newValue ) {
		this.fullPath = path;
		this.oldValue = oldValue;
		this.newValue = newValue;
		
		this.nodePath = Settings.getParentPath( path );
		this.entryKey = Settings.getSettingKey( path );
	}
	
	public String getNodePath() {
		return nodePath;
	}

	public String getFullPath() {
		return fullPath;
	}
	
	public String getEntryKey() {
		return entryKey;
	}

	public String getOldValue() {
		return oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

}
