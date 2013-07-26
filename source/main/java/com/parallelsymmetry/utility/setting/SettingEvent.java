package com.parallelsymmetry.utility.setting;

public class SettingEvent {

	private String fullPath;

	private String nodePath;

	private String key;

	private String oldValue;

	private String newValue;

	/**
	 * Create a SettingEvent.
	 * 
	 * @param path The full path to the setting that changed including the key.
	 * @param oldValue The old value of the setting.
	 * @param newValue The new value of the setting.
	 */
	public SettingEvent( String path, String oldValue, String newValue ) {
		this.fullPath = path;
		this.oldValue = oldValue;
		this.newValue = newValue;

		this.nodePath = Settings.getParentPath( path );
		this.key = Settings.getSettingKey( path );
	}

	/**
	 * Get the full path of the setting that changed.
	 * 
	 * @return
	 */
	public String getFullPath() {
		return fullPath;
	}

	/**
	 * Get the containing node path to the setting that changed.
	 * 
	 * @return
	 */
	public String getNodePath() {
		return nodePath;
	}

	/**
	 * Get the key to the setting that changed.
	 * 
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Get the old value of the setting.
	 * 
	 * @return
	 */
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * Get the new value of the setting.
	 * 
	 * @return
	 */
	public String getNewValue() {
		return newValue;
	}

}
