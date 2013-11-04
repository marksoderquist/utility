package com.parallelsymmetry.utility.ui;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Icon;

import com.parallelsymmetry.utility.Bundles;

public class ActionLibrary {

	public static final String DEFAULT_ACTION_BUNDLE_PATH = "actions";

	private IconLibrary icons;

	private Map<String, XAction> actions;

	private Map<String, XAction> actionsByAccelerator;

	private String bundlePath;

	public ActionLibrary( IconLibrary icons ) {
		this( icons, DEFAULT_ACTION_BUNDLE_PATH );
	}

	public ActionLibrary( IconLibrary icons, String bundlePath ) {
		this.icons = icons;
		this.bundlePath = bundlePath;
		actions = new ConcurrentHashMap<String, XAction>();
		actionsByAccelerator = new ConcurrentHashMap<String, XAction>();
	}

	public XAction getAction( String key ) {
		if( key == null ) return null;

		XAction action = actions.get( key );
		if( action != null ) return action;

		action = addAction( getClass().getClassLoader(), key );

		return action;
	}

	public Set<String> getAccelerators() {
		return Collections.unmodifiableSet( actionsByAccelerator.keySet() );
	}

	public XAction getActionByAccelerator( String accelerator ) {
		return actionsByAccelerator.get( accelerator );
	}

	/**
	 * Create an action using the default locale to resolve the name, mnemonic,
	 * and accelerator.
	 * 
	 * @param key
	 * @return
	 */
	public XAction addAction( ClassLoader loader, String key ) {
		XAction action = actions.get( key );
		if( action != null ) return action;

		// The following six lines should match refresh().
		String name = Bundles.getString( loader, bundlePath, key );
		String mnemonic = Bundles.getString( loader, bundlePath, key + ".mnemonic", String.valueOf( XAction.NONE ), false );
		String accelerator = Bundles.getString( loader, bundlePath, key + ".accelerator", false );
		String display = Bundles.getString( loader, bundlePath, key + ".display", false );
		String icon = Bundles.getString( loader, bundlePath, key + ".icon", false );
		if( name == null ) name = key;

		return addAction( loader, key, name, icons.getIcon( icon == null ? key : icon ), Integer.parseInt( mnemonic ), accelerator, display );
	}

	/**
	 * Create an action using the specified parameters. It is preferred to use the
	 * form of this method with only the key argument so that the action name,
	 * mnemonic, and accelerator may be resolved from the locale.
	 * 
	 * @param key
	 * @param name
	 * @param icon
	 * @param mnemonic
	 * @param accelerator
	 * @return
	 */
	public XAction addAction( ClassLoader loader, String key, String name, Icon icon, int mnemonic, String accelerator, String display ) {
		XAction action = actions.get( key );
		if( action != null ) return action;

		action = new XAction( key, name, icon, mnemonic, accelerator, display );
		if( accelerator != null ) actionsByAccelerator.put( accelerator, action );
		action.putValue( "class.loader", loader );
		actions.put( key, action );

		return action;
	}

	public XAction removeAction( String key ) {
		XAction action = actions.get( key );
		actions.remove( key );
		return action;
	}

	public void refresh() {
		// Go through the actions and change the names, accelerators, etc.
		for( String key : actions.keySet() ) {
			XAction action = actions.get( key );
			ClassLoader loader = (ClassLoader)action.getValue( "class.loader" );

			// The following six lines should match addAction().
			String name = Bundles.getString( loader, bundlePath, key );
			String mnemonic = Bundles.getString( loader, bundlePath, key + ".mnemonic", String.valueOf( XAction.NONE ), false );
			String accelerator = Bundles.getString( loader, bundlePath, key + ".accelerator", false );
			String display = Bundles.getString( loader, bundlePath, key + ".display", false );
			String icon = Bundles.getString( loader, bundlePath, key + ".icon", false );
			if( name == null ) name = key;

			action.setValues( name, icons.getIcon( icon == null ? key : icon ), Integer.parseInt( mnemonic ), accelerator, display );
		}
	}

}
