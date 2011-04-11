package com.parallelsymmetry.escape.utility.ui;

import java.awt.KeyboardFocusManager;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Icon;

import com.parallelsymmetry.escape.utility.Bundles;

public class ActionLibrary {

	public static final String ACTIONS = "actions";

	private IconLibrary icons;

	private Map<String, ActionDeque> actions;

	private Map<String, ActionDeque> actionsByShortcut;

	public ActionLibrary( IconLibrary icons ) {
		this.icons = icons;
		actions = new ConcurrentHashMap<String, ActionDeque>();
		actionsByShortcut = new ConcurrentHashMap<String, ActionDeque>();

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

		ActionShortcutWatcher actionShortcutWatcher = new ActionShortcutWatcher( this );
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventPostProcessor( actionShortcutWatcher );
	}

	public ActionDeque getAction( String key ) {
		if( key == null ) return null;

		ActionDeque action = actions.get( key );
		if( action != null ) return action;

		action = addAction( key );

		return action;
	}

	public ActionDeque getActionByShortcut( String shortcut ) {
		return actionsByShortcut.get( shortcut );
	}

	public Set<String> getShortcuts() {
		return Collections.unmodifiableSet( actionsByShortcut.keySet() );
	}

	/**
	 * Create an action using the default locale to resolve the name, mnemonic,
	 * and shortcut.
	 * 
	 * @param key
	 * @return
	 */
	public ActionDeque addAction( String key ) {
		ActionDeque action = actions.get( key );
		if( action != null ) return action;

		String name = Bundles.getString( ACTIONS, key );
		String mnemonic = Bundles.getString( ACTIONS, key + ".mnemonic", "-1", false );
		String shortcut = Bundles.getString( ACTIONS, key + ".shortcut", false );
		String display = Bundles.getString( ACTIONS, key + ".display", false );
		String icon = Bundles.getString( ACTIONS, key + ".icon", false );
		if( name == null ) name = key;

		return addAction( key, name, icons.getIcon( icon == null ? key : icon ), Integer.parseInt( mnemonic ), shortcut, display );
	}

	/**
	 * Create an action using the specified parameters. It is preferred to use the
	 * form of this method with only the key argument so that the action name,
	 * mnemonic, and shortcut may be resolved from the locale.
	 * 
	 * @param key
	 * @param name
	 * @param icon
	 * @param mnemonic
	 * @param shortcut
	 * @return
	 */
	public ActionDeque addAction( String key, String name, Icon icon, int mnemonic, String shortcut, String display ) {
		ActionDeque action = actions.get( key );
		if( action != null ) return action;

		action = new ActionDeque( key, name, icon, mnemonic, shortcut, display );
		if( shortcut != null ) actionsByShortcut.put( shortcut, action );
		actions.put( key, action );

		return action;
	}

	public ActionDeque removeAction( String key ) {
		ActionDeque action = actions.get( key );
		actions.remove( key );
		return action;
	}

}
