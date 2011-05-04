package com.parallelsymmetry.escape.utility.ui;

import java.awt.KeyboardFocusManager;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Icon;

import com.parallelsymmetry.escape.utility.Bundles;

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

		// Create default actions.
		addAction( "new" );
		addAction( "open" );
		addAction( "save" );
		addAction( "save.as" );
		addAction( "save.copy.as" );
		addAction( "close" );
		addAction( "exit" );

		addAction( "undo" );
		addAction( "redo" );
		addAction( "cut" );
		addAction( "copy" );
		addAction( "paste" );
		addAction( "delete" );
		addAction( "indent" );
		addAction( "unindent" );
		addAction( "properties" );

		addAction( "workarea.new" );
		addAction( "workarea.open" );
		addAction( "view.new" );

		addAction( "view.pane.select.default" );

		addAction( "view.pane.split.horizontal" );
		addAction( "view.pane.split.vertical" );

		addAction( "view.pane.merge.north" );
		addAction( "view.pane.merge.south" );
		addAction( "view.pane.merge.east" );
		addAction( "view.pane.merge.west" );

		addAction( "preferences" );
		addAction( "system.properties" );
		addAction( "module.organizer" );
		addAction( "updates" );
		addAction( "about" );

		addAction( "preferences.reset" );
		addAction( "worker.manager" );
		addAction( "restart" );

		ActionAcceleratorWatcher actionAcceleratorWatcher = new ActionAcceleratorWatcher( this );
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventPostProcessor( actionAcceleratorWatcher );
	}

	public XAction getAction( String key ) {
		if( key == null ) return null;

		XAction action = actions.get( key );
		if( action != null ) return action;

		action = addAction( key );

		return action;
	}

	public XAction getActionByAccelerator( String accelerator ) {
		return actionsByAccelerator.get( accelerator );
	}

	public Set<String> getAccelerators() {
		return Collections.unmodifiableSet( actionsByAccelerator.keySet() );
	}

	/**
	 * Create an action using the default locale to resolve the name, mnemonic,
	 * and accelerator.
	 * 
	 * @param key
	 * @return
	 */
	public XAction addAction( String key ) {
		XAction action = actions.get( key );
		if( action != null ) return action;

		String name = Bundles.getString( bundlePath, key );
		String mnemonic = Bundles.getString( bundlePath, key + ".mnemonic", "-1", false );
		String accelerator = Bundles.getString( bundlePath, key + ".accelerator", false );
		String display = Bundles.getString( bundlePath, key + ".display", false );
		String icon = Bundles.getString( bundlePath, key + ".icon", false );
		if( name == null ) name = key;

		return addAction( key, name, icons.getIcon( icon == null ? key : icon ), Integer.parseInt( mnemonic ), accelerator, display );
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
	public XAction addAction( String key, String name, Icon icon, int mnemonic, String accelerator, String display ) {
		XAction action = actions.get( key );
		if( action != null ) return action;

		action = new XAction( key, name, icon, mnemonic, accelerator, display );
		if( accelerator != null ) actionsByAccelerator.put( accelerator, action );
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
			String name = Bundles.getString( bundlePath, key );
			String mnemonic = Bundles.getString( bundlePath, key + ".mnemonic", "-1", false );
			String accelerator = Bundles.getString( bundlePath, key + ".accelerator", false );
			String display = Bundles.getString( bundlePath, key + ".display", false );
			String icon = Bundles.getString( bundlePath, key + ".icon", false );

			actions.get( key ).setValues( name, icons.getIcon( icon == null ? key : icon ), Integer.parseInt( mnemonic ), accelerator, display );
		}
	}

}
