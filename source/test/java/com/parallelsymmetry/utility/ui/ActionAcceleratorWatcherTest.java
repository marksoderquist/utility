package com.parallelsymmetry.utility.ui;

import com.parallelsymmetry.utility.Accessor;
import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ActionAcceleratorWatcherTest extends BaseTestCase {

	@Test
	public void testMatch() throws Exception {
		List<String> accelerators = new ArrayList<>();
		ActionAcceleratorWatcher watcher = new ActionAcceleratorWatcher( new ActionLibrary( new IconLibrary() ) );

		assertEquals( ActionAcceleratorWatcher.Match.NONE, Accessor.callMethod( watcher, "match", List.class, accelerators, String.class, null ) );
		assertEquals( ActionAcceleratorWatcher.Match.NONE, Accessor.callMethod( watcher, "match", List.class, accelerators, String.class, "c-o" ) );

		accelerators.add( "c-n a" );
		accelerators.add( "c-n b" );
		assertEquals( ActionAcceleratorWatcher.Match.PARTIAL, Accessor.callMethod( watcher, "match", List.class, accelerators, String.class, "c-n" ) );
		assertEquals( ActionAcceleratorWatcher.Match.EXACT, Accessor.callMethod( watcher, "match", List.class, accelerators, String.class, "c-n a" ) );
	}

	@Test
	public void testStartsWith() throws Exception {
		ActionAcceleratorWatcher watcher = new ActionAcceleratorWatcher( new ActionLibrary( new IconLibrary() ) );

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
