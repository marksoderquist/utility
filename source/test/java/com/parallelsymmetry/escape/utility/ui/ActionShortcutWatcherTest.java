package com.parallelsymmetry.escape.utility.ui;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.Accessor;

public class ActionShortcutWatcherTest extends TestCase {

	public void testMatch() throws Exception {
		List<String> shortcuts = new ArrayList<String>();
		ActionShortcutWatcher watcher = new ActionShortcutWatcher( new ActionLibrary( new IconLibrary() ) );

		assertEquals( ActionShortcutWatcher.Match.NONE, (ActionShortcutWatcher.Match)Accessor.callMethod( watcher, "match", List.class, shortcuts, String.class, null ) );
		assertEquals( ActionShortcutWatcher.Match.NONE, (ActionShortcutWatcher.Match)Accessor.callMethod( watcher, "match", List.class, shortcuts, String.class, "c-o" ) );

		shortcuts.add( "c-n a" );
		shortcuts.add( "c-n b" );
		assertEquals( ActionShortcutWatcher.Match.PARTIAL, (ActionShortcutWatcher.Match)Accessor.callMethod( watcher, "match", List.class, shortcuts, String.class, "c-n" ) );
		assertEquals( ActionShortcutWatcher.Match.EXACT, (ActionShortcutWatcher.Match)Accessor.callMethod( watcher, "match", List.class, shortcuts, String.class, "c-n a" ) );
	}

	public void testStartsWith() throws Exception {
		ActionShortcutWatcher watcher = new ActionShortcutWatcher( new ActionLibrary( new IconLibrary() ) );

		assertTrue( (Boolean)Accessor.callMethod( watcher, "sequenceStartsWith", String.class, null, String.class, null ) );
		assertFalse( (Boolean)Accessor.callMethod( watcher, "sequenceStartsWith", String.class, "c-m", String.class, null ) );
		assertFalse( (Boolean)Accessor.callMethod( watcher, "sequenceStartsWith", String.class, null, String.class, "c-n" ) );

		assertTrue( (Boolean)Accessor.callMethod( watcher, "sequenceStartsWith", "s-c a-e", "s-c" ) );
		assertFalse( (Boolean)Accessor.callMethod( watcher, "sequenceStartsWith", "s-c", "s-c a-e" ) );

		assertTrue( (Boolean)Accessor.callMethod( watcher, "sequenceStartsWith", "a-c   a-e", "a-c" ) );
		assertFalse( (Boolean)Accessor.callMethod( watcher, "sequenceStartsWith", "a-c", "a-c   a-e" ) );

		assertFalse( (Boolean)Accessor.callMethod( watcher, "sequenceStartsWith", "a-a a-b", "a-a a-c" ) );
	}
}
