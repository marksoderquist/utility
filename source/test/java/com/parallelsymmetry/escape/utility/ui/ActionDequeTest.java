package com.parallelsymmetry.escape.utility.ui;

import java.awt.event.ActionEvent;

import junit.framework.TestCase;

public class ActionDequeTest extends TestCase {

	public void testCreate() throws Exception {
		assertNotNull( new ActionDeque( "test", "Test" ) );
	}

	public void testAddHandlerWithNull() throws Exception {
		ActionDeque action = new ActionDeque( "test", "Test" );
		try {
			action.pushHandler( null );
		} catch( IllegalArgumentException exception ) {
			assertNotNull( exception );
			return;
		}
		fail( "IllegalArgumentException not thrown." );
	}

	public void testAddHandler() throws Exception {
		ActionDeque action = new ActionDeque( "test", "Test" );
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
		ActionDeque action = new ActionDeque( "test", "Test" );
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
		ActionDeque action = new ActionDeque( "test", "Test" );
		TestActionHandler handler = new TestActionHandler();
		action.pushHandler( handler );
		handler.setEnabled( true );
		SwingUtil.swingWait();
		assertTrue( action.isEnabled() );
	}

	public void testActionForceDisabled() throws Exception {
		ActionDeque action = new ActionDeque( "test", "Test" );
		TestActionHandler handler = new TestActionHandler();
		action.pushHandler( handler );
		handler.setEnabled( false );
		SwingUtil.swingWait();
		assertFalse( action.isEnabled() );
	}

	public void testSetEnabled() throws Exception {
		ActionDeque action = new ActionDeque( "test", "Test" );
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
		ActionDeque action = new ActionDeque( "test", "Test" );
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

	public void testGetShortcutDisplayText() {
		assertEquals( "Ctl+A", ActionDeque.getShortcutDisplayText( "c-a" ) );
		assertEquals( "Alt+A", ActionDeque.getShortcutDisplayText( "a-a" ) );
		assertEquals( "Shift+A", ActionDeque.getShortcutDisplayText( "s-a" ) );

		assertEquals( "Ctl+Shift+A", ActionDeque.getShortcutDisplayText( "cs-a" ) );
		assertEquals( "Alt+Shift+A", ActionDeque.getShortcutDisplayText( "as-a" ) );
		assertEquals( "Ctl+Alt+A", ActionDeque.getShortcutDisplayText( "ca-a" ) );

		assertEquals( "F1", ActionDeque.getShortcutDisplayText( "f1" ) );
		assertEquals( "Ctl+F1", ActionDeque.getShortcutDisplayText( "c-f1" ) );

		assertEquals( "A A", ActionDeque.getShortcutDisplayText( "a a" ) );
		assertEquals( "Ctl+K L", ActionDeque.getShortcutDisplayText( "c-k l" ) );
		assertEquals( "Ctl+K Alt+L M", ActionDeque.getShortcutDisplayText( "c-k a-l m" ) );
	}

	private static class TestActionHandler extends ActionHandler {

		private ActionEvent event;

		public void actionPerformed( ActionEvent event ) {
			this.event = event;
		}

		public ActionEvent getEvent() {
			return event;
		}

	}

}
