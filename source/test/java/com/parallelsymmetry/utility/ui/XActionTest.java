package com.parallelsymmetry.utility.ui;

import java.awt.event.ActionEvent;

import com.parallelsymmetry.utility.ui.SwingUtil;
import com.parallelsymmetry.utility.ui.XAction;
import com.parallelsymmetry.utility.ui.XActionHandler;

import junit.framework.TestCase;

public class XActionTest extends TestCase {

	public void testCreate() throws Exception {
		assertNotNull( new XAction( "test", "Test" ) );
	}

	public void testAddHandlerWithNull() throws Exception {
		XAction action = new XAction( "test", "Test" );
		try {
			action.pushHandler( null );
		} catch( IllegalArgumentException exception ) {
			assertNotNull( exception );
			return;
		}
		fail( "IllegalArgumentException not thrown." );
	}

	public void testAddHandler() throws Exception {
		XAction action = new XAction( "test", "Test" );
		TestActionHandler handler = new TestActionHandler();
		action.pushHandler( handler );
		handler.setEnabled( true );

		SwingUtil.swingWait();

		ActionEvent event = new ActionEvent( this, 0, "other" );
		action.performAction( event );

		assertNotNull( handler.getEvent() );
		assertEquals( "test", handler.getEvent().getActionCommand() );
	}

	public void testAddTwoHandlers() throws Exception {
		XAction action = new XAction( "test", "Test" );
		TestActionHandler handler1 = new TestActionHandler();
		TestActionHandler handler2 = new TestActionHandler();
		action.pushHandler( handler2 );
		action.pushHandler( handler1 );
		handler2.setEnabled( true );
		handler1.setEnabled( true );

		SwingUtil.swingWait();

		ActionEvent event = new ActionEvent( this, 0, "other" );
		action.performAction( event );

		assertNull( handler2.getEvent() );
		assertNotNull( handler1.getEvent() );
		assertEquals( "test", handler1.getEvent().getActionCommand() );
		assertNotSame( "other", handler1.getEvent().getActionCommand() );
	}

	public void testActionEnable() throws Exception {
		XAction action = new XAction( "test", "Test" );
		TestActionHandler handler = new TestActionHandler();
		action.pushHandler( handler );
		handler.setEnabled( true );
		SwingUtil.swingWait();
		assertTrue( action.isEnabled() );
	}

	public void testActionForceDisabled() throws Exception {
		XAction action = new XAction( "test", "Test" );
		TestActionHandler handler = new TestActionHandler();
		action.pushHandler( handler );
		handler.setEnabled( false );
		SwingUtil.swingWait();
		assertFalse( action.isEnabled() );
	}

	public void testSetEnabled() throws Exception {
		XAction action = new XAction( "test", "Test" );
		TestActionHandler handler = new TestActionHandler();
		action.pushHandler( handler );

		handler.setEnabled( false );
		SwingUtil.swingWait();
		assertFalse( action.isEnabled() );
		handler.setEnabled( true );
		SwingUtil.swingWait();
		assertTrue( action.isEnabled() );
	}

	public void testSetEnabledWithTwoHandlers() throws Exception {
		XAction action = new XAction( "test", "Test" );
		TestActionHandler handler1 = new TestActionHandler();
		TestActionHandler handler2 = new TestActionHandler();
		action.pushHandler( handler2 );
		action.pushHandler( handler1 );
		SwingUtil.swingWait();

		assertFalse( action.isEnabled() );
		handler2.setEnabled( true );
		SwingUtil.swingWait();
		assertFalse( action.isEnabled() );
		handler2.setEnabled( false );
		SwingUtil.swingWait();
		assertFalse( action.isEnabled() );
		handler1.setEnabled( true );
		SwingUtil.swingWait();
		assertTrue( action.isEnabled() );
		handler1.setEnabled( false );
		SwingUtil.swingWait();
		assertFalse( action.isEnabled() );
	}

	public void testGetAcceleratorDisplayText() {
		assertEquals( XAction.DEFAULT_ALT_PREFIX + "+A", XAction.getAcceleratorDisplayText( "a-a" ) );
		assertEquals( XAction.DEFAULT_CTRL_PREFIX + "+A", XAction.getAcceleratorDisplayText( "c-a" ) );
		assertEquals( XAction.DEFAULT_META_PREFIX + "+A", XAction.getAcceleratorDisplayText( "m-a" ) );
		assertEquals( XAction.DEFAULT_SHIFT_PREFIX + "+A", XAction.getAcceleratorDisplayText( "s-a" ) );

		assertEquals( XAction.DEFAULT_ALT_PREFIX + "+" + XAction.DEFAULT_SHIFT_PREFIX + "+A", XAction.getAcceleratorDisplayText( "as-a" ) );
		assertEquals( XAction.DEFAULT_CTRL_PREFIX + "+" + XAction.DEFAULT_ALT_PREFIX + "+A", XAction.getAcceleratorDisplayText( "ca-a" ) );
		assertEquals( XAction.DEFAULT_CTRL_PREFIX + "+" + XAction.DEFAULT_SHIFT_PREFIX + "+A", XAction.getAcceleratorDisplayText( "cs-a" ) );
		assertEquals( XAction.DEFAULT_META_PREFIX + "+" + XAction.DEFAULT_SHIFT_PREFIX + "+A", XAction.getAcceleratorDisplayText( "ms-a" ) );

		assertEquals( "F1", XAction.getAcceleratorDisplayText( "f1" ) );
		assertEquals( XAction.DEFAULT_CTRL_PREFIX + "+F1", XAction.getAcceleratorDisplayText( "c-f1" ) );

		assertEquals( "A A", XAction.getAcceleratorDisplayText( "a a" ) );
		assertEquals( XAction.DEFAULT_CTRL_PREFIX + "+K L", XAction.getAcceleratorDisplayText( "c-k l" ) );
		assertEquals( XAction.DEFAULT_CTRL_PREFIX + "+K " + XAction.DEFAULT_ALT_PREFIX + "+L M", XAction.getAcceleratorDisplayText( "c-k a-l m" ) );
	}

	private static class TestActionHandler extends XActionHandler {

		private ActionEvent event;

		@Override
		public void actionPerformed( ActionEvent event ) {
			this.event = event;
		}

		public ActionEvent getEvent() {
			return event;
		}

	}

}
