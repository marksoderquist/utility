package com.parallelsymmetry.escape.utility.ui;

import java.util.Map;

import javax.swing.Action;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.Accessor;

public class ActionLibraryTest extends TestCase {

	private ActionLibrary library = new ActionLibrary( new IconLibrary() );

	public void testCreate() throws Exception {
		assertNotNull( library );
	}

	public void testGetAction() throws Exception {
		assertNull( library.getAction( null ) );
		assertNotNull( library.getAction( "null" ) );

		Map<String, ActionDeque> actions = Accessor.getField( library, "actions" );
		for( String key : actions.keySet() ) {
			checkAction( key );
		}
	}

	private void checkAction( String key ) {
		assertEquals( key, library.getAction( key ).getValue( Action.ACTION_COMMAND_KEY ) );
	}

}
