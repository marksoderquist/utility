package com.parallelsymmetry.utility.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Timer;

public abstract class AnimatedIcon extends BaseIcon implements ActionListener {

	private static final int DEFAULT_DELAY = 50;

	private JComponent component;

	private Timer timer;

	public AnimatedIcon() {
		super();
	}

	public AnimatedIcon( int size ) {
		super( size );
	}

	public abstract int getFrameCount();

	public abstract void incrementFrame();

	public abstract void setFrame( int frame );

	public void startAnimation( JComponent component ) {
		startAnimation( component, DEFAULT_DELAY );
	}

	public synchronized void startAnimation( JComponent component, int delay ) {
		this.component = component;
		if( timer != null ) stopAnimation();
		timer = new Timer( delay, this );
		timer.start();
	}

	public synchronized void stopAnimation() {
		if( timer != null ) timer.stop();
	}

	@Override
	public void actionPerformed( ActionEvent event ) {
		incrementFrame();
		if( component != null ) component.repaint();
	}

}
