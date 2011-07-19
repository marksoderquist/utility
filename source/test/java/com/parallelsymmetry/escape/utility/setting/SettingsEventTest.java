package com.parallelsymmetry.escape.utility.setting;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class SettingsEventTest extends TestCase {

	public void testSettingChanged() {
		Settings settings = new Settings();
		MockWritableSettingProvider provider = new MockWritableSettingProvider( "changed.events" );
		SettingWatcher watcher = new SettingWatcher();

		settings.addProvider( provider );
		settings.addSettingListener( watcher );

		settings.put( "/test/value/a", "1" );
		settings.put( "/test/value/a", null );
		
		assertEvent( watcher.events.get(0), "/test/value/a", null, "1" );
		assertEvent( watcher.events.get(1), "/test/value/a", "1", null );
		assertEquals( 2, watcher.events.size() );
	}
	
	private void assertEvent( SettingEvent event, String path, String oldValue, String newValue ) {
		assertEquals( path, event.getKey() );
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
