package com.parallelsymmetry.escape.utility.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

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

	public void setEnabled( boolean enabled ) {
		if( this.enabled == enabled ) return;

		this.enabled = enabled;

		fireEnabledChanged( enabled );
	}

	public void addActionCallback( XAction action ) {
		actions.add( action );
	}

	public void removeActionCallback( XAction action ) {
		actions.remove( action );
	}

	private final void fireEnabledChanged( boolean enabled ) {
		for( XAction action : actions ) {
			action.handleEnabledChanged( this, enabled );
		}
	}

}
