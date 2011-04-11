package com.parallelsymmetry.escape.utility.ui;

import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class ActionHandler implements ActionListener {

	private boolean enabled;

	private Collection<ActionDeque> actions;

	public ActionHandler() {
		actions = new CopyOnWriteArraySet<ActionDeque>();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled( boolean enabled ) {
		if( this.enabled == enabled ) return;

		this.enabled = enabled;

		fireEnabledChanged( enabled );
	}

	public void addActionCallback( ActionDeque action ) {
		actions.add( action );
	}

	public void removeActionCallback( ActionDeque action ) {
		actions.remove( action );
	}

	private final void fireEnabledChanged( boolean enabled ) {
		for( ActionDeque action : actions ) {
			action.handlerEnabledChanged( this, enabled );
		}
	}

}
