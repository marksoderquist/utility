package com.parallelsymmetry.utility;

public interface Controllable {

	void start() throws ControllableException;

	boolean isRunning();

	void stop() throws ControllableException;

}
