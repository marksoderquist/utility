package com.parallelsymmetry.utility.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MockSettingListener implements SettingListener {

	private List<SettingEvent> events;

	public MockSettingListener() {
		reset();
	}

	@Override
	public void settingChanged( SettingEvent event ) {
		events.add( event );
	}

	public List<SettingEvent> getEvents() {
		return new ArrayList<>( events );
	}

	public void reset() {
		events = new CopyOnWriteArrayList<>();
	}

}
