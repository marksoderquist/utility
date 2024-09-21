package com.parallelsymmetry.utility.agent;

import com.parallelsymmetry.utility.BaseTestCase;
import com.parallelsymmetry.utility.ThreadUtil;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class WorkerTest extends BaseTestCase {

	@Test
	public void testStartStopCount() throws Exception {
		CountingWorker worker = new CountingWorker();
		assertFalse( worker.isWorking() );
		worker.startAndWait();
		assertTrue( worker.isWorking() );
		worker.stopAndWait();
		assertFalse( worker.isWorking() );
		assertEquals( 1, worker.getStartCount() );
		assertEquals( 1, worker.getStopCount() );
	}

	@Test
	public void testStartAndStop() throws Exception {
		Worker worker = new BlockingIOWorker();
		assertFalse( worker.isWorking() );
		worker.startAndWait();
		assertTrue( worker.isWorking() );
		worker.stopAndWait();
		assertFalse( worker.isWorking() );
	}

	@Test
	public void testRestart() throws Exception {
		Worker worker = new BlockingIOWorker();
		assertFalse( worker.isWorking() );
		worker.startAndWait();
		assertTrue( worker.isWorking(), "Worker not working after start." );
		worker.restart();
		assertTrue( worker.isWorking(), "Worker not working after restart." );
		worker.stopAndWait();
		assertFalse( worker.isWorking() );
	}

	@Test
	public void testFastRestarts() throws Exception {
		Worker worker = new BlockingIOWorker();
		assertFalse( worker.isWorking() );
		worker.startAndWait();
		assertTrue( worker.isWorking() );
		worker.restart();
		worker.restart();
		worker.restart();
		worker.restart();
		worker.restart();
		assertTrue( worker.isWorking() );
		worker.stopAndWait();
		assertFalse( worker.isWorking() );
	}

	@Getter
	private static class CountingWorker extends Worker {

		private int startCount;

		private int stopCount;

		@Override
		public void startWorker() {
			startCount++;
		}

		@Override
		public void stopWorker() {
			stopCount++;
		}

		@Override
		public void run() {
			while( shouldExecute() ) {
				ThreadUtil.pause( 1 );
			}
		}

	}

	private static class BlockingIOWorker extends Worker {

		InputStream input;

		@Override
		protected void startWorker() throws Exception {
			input = new TestInputStream();
		}

		@Override
		public void run() {
			try {
				input.read();
			} catch( IOException exception ) {
				// Intentionally ignore exception
			}
		}

		@Override
		protected void stopWorker() throws Exception {
			input.close();
		}

	}

	private static class TestInputStream extends InputStream {

		private boolean closed;

		@Override
		public synchronized int read() throws IOException {
			while( !closed ) {
				try {
					this.wait();
				} catch( InterruptedException exception ) {}
			}
			return -1;
		}

		@Override
		public synchronized void close() {
			closed = true;
			this.notifyAll();
		}

	}

}
