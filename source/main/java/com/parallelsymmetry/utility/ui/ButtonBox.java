package com.parallelsymmetry.utility.ui;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;

public class ButtonBox extends Box {

	private static final long serialVersionUID = -8568143005404794963L;

	private static final int DEFAULT_BUTTON_GAP = 5;

	public ButtonBox( Component... components ) {
		this( DEFAULT_BUTTON_GAP, components );
	}

	public ButtonBox( int gap, Component... components ) {
		super( BoxLayout.LINE_AXIS );

		add( Box.createHorizontalGlue() );
		int count = components.length;
		for( int index = 0; index < count; index++ ) {
			if( index > 0 ) add( Box.createHorizontalStrut( gap ) );
			add( components[index] );
		}
		add( Box.createHorizontalGlue() );
	}

}
