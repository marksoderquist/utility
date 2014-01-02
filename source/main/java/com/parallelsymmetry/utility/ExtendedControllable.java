package com.parallelsymmetry.utility;

import java.util.concurrent.TimeUnit;

public interface ExtendedControllable extends Controllable {

	void startAndWait() throws ControllableException, InterruptedException;

	void startAndWait( long timeout, TimeUnit unit ) throws ControllableException, InterruptedException;

	void stopAndWait() throws InterruptedException;

	void stopAndWait( long timeout, TimeUnit unit ) throws ControllableException, InterruptedException;

	void restart() throws ControllableException, InterruptedException;

	void restart( long timeout, TimeUnit unit ) throws ControllableException, InterruptedException;

}
