package com.parallelsymmetry.utility.ui;

import java.awt.event.ActionEvent;

import javax.swing.Action;

public class XActionWrapper extends XActionHandler {

	private Action action;

	public XActionWrapper( Action action ) {
		if( action == null ) throw new NullPointerException( "Action cannot be null." );
		this.action = action;
	}

	@Override
	public void actionPerformed( ActionEvent event ) {
		action.actionPerformed( event );
	}

}
