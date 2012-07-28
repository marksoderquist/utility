package com.parallelsymmetry.escape.utility.setting;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.mock.MockWritableSettingProvider;

public class SettingsEventTest extends TestCase {

	public void testSettingChanged() {
		Settings settings = new Settings();
		MockWritableSettingProvider provider = new MockWritableSettingProvider( "changed.events" );
		SettingWatcher rootWatcher = new SettingWatcher();
		SettingWatcher nodeWatcher = new SettingWatcher();
		SettingWatcher valueWatcher = new SettingWatcher();

		settings.addProvider( provider );
		settings.addSettingListener( "/", rootWatcher );
		settings.addSettingListener( "/test/value", nodeWatcher );
		settings.addSettingListener( "/test/value/b", valueWatcher );

		settings.put( "/test/a", "1" );
		settings.put( "/test/a", null );
		settings.put( "/test/value/b", "2" );
		settings.put( "/test/value/b", null );

		assertEquals( 4, rootWatcher.events.size() );
		assertEvent( rootWatcher.events.get( 0 ), "/test/a", null, "1" );
		assertEvent( rootWatcher.events.get( 1 ), "/test/a", "1", null );
		assertEvent( rootWatcher.events.get( 2 ), "/test/value/b", null, "2" );
		assertEvent( rootWatcher.events.get( 3 ), "/test/value/b", "2", null );

		assertEquals( 2, nodeWatcher.events.size() );
		assertEvent( nodeWatcher.events.get( 0 ), "/test/value/b", null, "2" );
		assertEvent( nodeWatcher.events.get( 1 ), "/test/value/b", "2", null );

		assertEquals( 2, valueWatcher.events.size() );
		assertEvent( valueWatcher.events.get( 0 ), "/test/value/b", null, "2" );
		assertEvent( valueWatcher.events.get( 1 ), "/test/value/b", "2", null );
	}

	private void assertEvent( SettingEvent event, String path, String oldValue, String newValue ) {
		assertEquals( path, event.getFullPath() );
		assertEquals( oldValue, event.getOldValue() );
		assertEquals( newValue, event.getNewValue() );
	}

	private static class SettingWatcher implements SettingListener {

		List<SettingEvent> events = new ArrayList<SettingEvent>();

		@Override
		public void settingChanged( SettingEvent event ) {
			events.add( event );
		}

	}

}
