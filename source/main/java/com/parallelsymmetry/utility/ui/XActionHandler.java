package com.parallelsymmetry.utility.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import com.parallelsymmetry.utility.log.Log;

public abstract class XActionHandler implements ActionListener {

	private boolean enabled;

	private Collection<XAction> actions;

	public XActionHandler() {
		this( false );
	}

	public XActionHandler( boolean enabled ) {
		this.enabled = enabled;
		actions = new CopyOnWriteArraySet<XAction>();
	}

	/*
	 * This is simply here to help the source completion feature of IDEs.
	 */
	@Override
	public abstract void actionPerformed( ActionEvent event );

	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * This method may be called from any thread. It ensures that the action is
	 * enabled or disabled on the event dispatch thread.
	 * 
	 * @param enabled
	 */
	public void setEnabled( boolean enabled ) {
		track = getClass().getName().endsWith( "SaveActionHandler" ) && this.enabled != enabled && !enabled;
		if( track ) Log.write( Log.DEVEL, "SaveActionHandler should be disabled" );

		// FIXME This line keeps the action from being disabled because it was already disabled.
		if( this.enabled == enabled ) return;

		this.enabled = enabled;

		fireEnabledChanged( enabled );
		track = false;
	}

	public void addActionCallback( XAction action ) {
		actions.add( action );
	}

	public void removeActionCallback( XAction action ) {
		actions.remove( action );
	}

	private boolean track;

	private final void fireEnabledChanged( boolean enabled ) {
		for( XAction action : actions ) {
			if( track ) Log.write( Log.DEVEL, "Sending enabled changed event: ", enabled );
			action.updateEnabledState();
		}
	}

}
