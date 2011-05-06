package com.parallelsymmetry.escape.utility.ui;

import java.awt.BasicStroke;
import java.awt.Color;

public class BrokenIcon extends AbstractIcon {

	public void render() {
		int penWidth = DEFAULT_PEN_WIDTH * 4;
		int near = penWidth / 2 + 24;
		int far = DEFAULT_ICON_SIZE - penWidth / 2 - 24;

		Path path = new Path();
		path.moveTo( near, near );
		path.lineTo( far, far );
		path.moveTo( near, far );
		path.lineTo( far, near );
		draw( path, Color.RED.darker(), new BasicStroke( penWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
	}

	public static void main( String[] parameters ) {
		Icons.proof( new BrokenIcon() );
	}

}
