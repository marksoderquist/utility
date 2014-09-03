package com.parallelsymmetry.utility;

import java.util.logging.Level;

import com.parallelsymmetry.utility.log.Log;

public class ConsoleLogger {

	private Process process;

	public ConsoleLogger( Process process ) {
		this.process = process;
	}

	public void start() {
		LineLogger outLogger = new LineLogger( Log.INFO );
		LineLogger errLogger = new LineLogger( Log.ERROR );

		LineOutputStream outOutputStream = new LineOutputStream();
		LineOutputStream errOutputStream = new LineOutputStream();

		outOutputStream.addLineListener( outLogger );
		errOutputStream.addLineListener( errLogger );

		new IoPump( process.getInputStream(), outOutputStream ).start();
		new IoPump( process.getErrorStream(), errOutputStream ).start();
	}

	private class LineLogger implements LineListener {

		private Level level;

		public LineLogger( Level level ) {
			this.level = level;
		}

		public void line( String line ) {
			Log.write( level, line );
		}

	}

}
