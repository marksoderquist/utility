package com.parallelsymmetry.swing;

import java.awt.Graphics2D;

import junit.framework.TestCase;

import com.parallelsymmetry.swing.AbstractIcon;
import com.parallelsymmetry.util.Accessor;

public class AbstractIconTest extends TestCase {

	public void testMove() throws Exception {
		MockIcon icon = new MockIcon();
		assertEquals( 0.0, Accessor.getField( icon, "movex" ) );
		assertEquals( 0.0, Accessor.getField( icon, "movey" ) );
		icon.move( 2, 3 );
		assertEquals( 2.0, Accessor.getField( icon, "movex" ) );
		assertEquals( 3.0, Accessor.getField( icon, "movey" ) );
		icon.move();
		assertEquals( 0.0, Accessor.getField( icon, "movex" ) );
		assertEquals( 0.0, Accessor.getField( icon, "movey" ) );
	}

	public void testSpin() throws Exception {
		MockIcon icon = new MockIcon();
		assertEquals( 0.0, Accessor.getField( icon, "angle" ) );
		icon.spin( 45 );
		assertEquals( 45.0, Accessor.getField( icon, "angle" ) );
		icon.spin();
		assertEquals( 0.0, Accessor.getField( icon, "angle" ) );
	}

	public void testHome() throws Exception {
		MockIcon icon = new MockIcon();
		assertEquals( 0.0, Accessor.getField( icon, "movex" ) );
		assertEquals( 0.0, Accessor.getField( icon, "movey" ) );
		assertEquals( 0.0, Accessor.getField( icon, "angle" ) );
		icon.spin( 2, 3, 45 );
		assertEquals( -1.54, (Double)Accessor.getField( icon, "movex" ), 0.01 );
		assertEquals( 2.29, (Double)Accessor.getField( icon, "movey" ), 0.01 );
		assertEquals( 45.0, Accessor.getField( icon, "angle" ) );
		icon.home();
		assertEquals( 0.0, Accessor.getField( icon, "movex" ) );
		assertEquals( 0.0, Accessor.getField( icon, "movey" ) );
		assertEquals( 0.0, Accessor.getField( icon, "angle" ) );
	}

	private static class MockIcon extends AbstractIcon {

		@Override
		public void render( Graphics2D graphics ) {}

	}

}
