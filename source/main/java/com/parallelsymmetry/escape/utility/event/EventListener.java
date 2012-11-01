package com.parallelsymmetry.escape.utility.event;

public interface EventListener<T extends Event> {

	void eventOccurred( T event );

}
