package com.parallelsymmetry.utility.ui;

import java.util.Map;

import javax.swing.Action;

import junit.framework.TestCase;

import com.parallelsymmetry.utility.Accessor;
import com.parallelsymmetry.utility.ui.ActionLibrary;
import com.parallelsymmetry.utility.ui.IconLibrary;
import com.parallelsymmetry.utility.ui.XAction;

public class ActionLibraryTest extends TestCase {

	private ActionLibrary library = new ActionLibrary( new IconLibrary() );

	public void testCreate() throws Exception {
		assertNotNull( library );
	}

	public void testGetAction() throws Exception {
		assertNull( library.getAction( null ) );
		assertNotNull( library.getAction( "null" ) );

		Map<String, XAction> actions = Accessor.getField( library, "actions" );
		for( String key : actions.keySet() ) {
			checkAction( key );
		}
	}

	private void checkAction( String key ) {
		assertEquals( key, library.getAction( key ).getValue( Action.ACTION_COMMAND_KEY ) );
	}

}
