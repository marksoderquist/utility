package com.parallelsymmetry.escape.utility;

import java.util.concurrent.TimeUnit;

public interface Controllable {

	boolean isRunning();

	void start() throws ControllableException;

	void startAndWait() throws ControllableException, InterruptedException;

	void startAndWait( long timeout, TimeUnit unit ) throws ControllableException, InterruptedException;

	void restart() throws ControllableException, InterruptedException;

	void restart( long timeout, TimeUnit unit ) throws ControllableException, InterruptedException;

	void stop() throws ControllableException;

	void stopAndWait() throws InterruptedException;

	void stopAndWait( long timeout, TimeUnit unit ) throws ControllableException, InterruptedException;

}
