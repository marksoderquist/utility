package com.parallelsymmetry.escape.utility.agent;

import com.parallelsymmetry.escape.utility.TripLock;

public abstract class Worker extends Agent implements Runnable {

	private boolean daemon;

	private Exception exception;

	private WorkerRunner runner;

	private boolean interruptOnStop;

	private final TripLock startlock = new TripLock();

	public Worker() {
		this( null, false );
	}

	public Worker( boolean daemon ) {
		this( null, daemon );
	}

	public Worker( String name ) {
		this( name, false );
	}

	public Worker( String name, boolean daemon ) {
		super( name );
		this.daemon = daemon;
		this.runner = new WorkerRunner();
	}
	
	public void trip() {
		this.runner.trip();
	}

	public boolean isWorking() {
		return this.runner.isWorking();
	}

	public boolean isExecutable() {
		return this.runner.isExecutable();
	}

	public boolean isWorkerThread() {
		return runner != null && runner.isRunnerThread();
	}

	public boolean isInterruptOnStop() {
		return interruptOnStop;
	}

	public void setInterruptOnStop( boolean flag ) {
		this.interruptOnStop = flag;
	}

	@Override
	protected final void startAgent() throws Exception {
		runner.start();
		startlock.hold();
		if( exception != null ) throw exception;
	}

	@Override
	protected final void stopAgent() throws Exception {
		runner.stop();
		if( exception != null ) throw exception;
	}

	/**
	 * Subclasses should override this method with code that does any setup work.
	 * 
	 * @throws Exception
	 */
	protected void startWorker() throws Exception {}

	/**
	 * Subclasses should override this method with code that terminates or closes
	 * any blocking operations. This method could also be used to stop other
	 * workers.
	 * <p>
	 * For example: If the worker thread is blocked on an InputStream.read()
	 * operation, this method should call the InputStream.close() method.
	 * 
	 * @throws Exception
	 */
	protected void stopWorker() throws Exception {}

	private class WorkerRunner implements Runnable {

		private Thread thread;

		private volatile boolean execute;

		public synchronized boolean isExecutable() {
			return execute;
		}
		
		public synchronized boolean isWorking() {
			return thread != null && thread.isAlive();
		}

		public boolean isRunnerThread() {
			return Thread.currentThread() == thread;
		}

		public void start() {
			execute = true;

			try {
				Worker.this.startWorker();
			} catch( Exception exception ) {
				Worker.this.exception = exception;
				return;
			}

			startlock.reset();
			thread = new Thread( this, getName() );
			thread.setPriority( Thread.NORM_PRIORITY );
			thread.setDaemon( Worker.this.daemon );
			thread.start();
		}

		public void run() {
			startlock.trip();
			Worker.this.run();
		}
		
		public void trip() {
			this.execute = false;
		}

		public void stop() {
			trip();
			
			try {
				Worker.this.stopWorker();
			} catch( Exception exception ) {
				Worker.this.exception = exception;
				return;
			}

			if( interruptOnStop ) thread.interrupt();

			if( Worker.this.daemon ) return;

			try {
				thread.join();
			} catch( InterruptedException exception ) {
				// Intentionally ignore exception.
			}
		}

	}

}
