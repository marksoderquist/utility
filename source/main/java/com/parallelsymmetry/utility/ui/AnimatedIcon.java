package com.parallelsymmetry.utility.ui;

import javax.swing.Icon;

public interface AnimatedIcon extends Icon {
	
	public int getFrameCount();
	
	public void incrementFrame();
	
	public void setFrame( int frame );

}
