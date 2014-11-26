package com.parallelsymmetry.utility.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import com.parallelsymmetry.utility.log.Log;

public abstract class XActionHandler implements ActionListener {

	private boolean enabled;

	private boolean selected;

	private Collection<XAction> actions;

	public XActionHandler() {
		this( false );
	}

	public XActionHandler( boolean enabled ) {
		this( enabled, false );
	}

	public XActionHandler( boolean enabled, boolean selected ) {
		this.enabled = enabled;
		this.selected = selected;
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
		if( this.enabled == enabled ) return;

		this.enabled = enabled;

		fireEnabledChanged( enabled );
	}

	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * This method may be called from any thread. It ensures that the action is
	 * selected or unselected on the event dispatch thread.
	 * 
	 * @param selected
	 */
	public void setSelected( boolean selected ) {
		if( this.selected == selected ) return;

		this.selected = selected;

		fireSelectedChanged( this.selected );
	}

	public void addActionCallback( XAction action ) {
		actions.add( action );
	}

	public void removeActionCallback( XAction action ) {
		actions.remove( action );
	}

	private final void fireEnabledChanged( boolean enabled ) {
		for( XAction action : actions ) {
			action.updateEnabledState();
		}
	}

	private final void fireSelectedChanged( boolean selected ) {
		for( XAction action : actions ) {
			action.updateSelectedState();
		}
	}

}
