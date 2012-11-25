package com.parallelsymmetry.utility.event;

public interface EventListener<T extends Event> {

	void eventOccurred( T event );

}
