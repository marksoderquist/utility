package com.parallelsymmetry.utility.ui;

import com.parallelsymmetry.utility.Accessor;
import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ActionLibraryTest extends BaseTestCase {

	private final ActionLibrary library = new ActionLibrary( new IconLibrary() );

	@Test
	public void testCreate() {
		assertNotNull( library );
	}

	@Test
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
