package com.parallelsymmetry.escape.utility.ui;

import java.awt.BasicStroke;
import java.awt.Color;

public class BrokenIcon extends BaseIcon {

	public void render() {
		double penWidth = K;
		double near = penWidth / 2 + ZB;
		double far = 1 - penWidth / 2 - ZB;

		Path path = new Path();
		path.moveTo( near, near );
		path.lineTo( far, far );
		path.moveTo( near, far );
		path.lineTo( far, near );
		draw( path, new Color( 196, 0, 0 ), new BasicStroke( (float)penWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
	}

	public static void main( String[] parameters ) {
		Icons.proof( new BrokenIcon() );
	}

}
