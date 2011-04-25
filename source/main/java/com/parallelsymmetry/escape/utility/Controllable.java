package com.parallelsymmetry.escape.utility;

import java.util.concurrent.TimeUnit;

public interface Controllable {
	
	boolean isRunning();

	void start();

	void startAndWait() throws InterruptedException;

	void startAndWait( long timeout, TimeUnit unit ) throws InterruptedException;
	
	void restart() throws InterruptedException;
	
	void restart( long timeout, TimeUnit unit ) throws InterruptedException;

	void stop();

	void stopAndWait() throws InterruptedException;

	void stopAndWait( long timeout, TimeUnit unit ) throws InterruptedException;

}
