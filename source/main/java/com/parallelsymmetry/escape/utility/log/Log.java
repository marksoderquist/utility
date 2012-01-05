package com.parallelsymmetry.escape.utility.log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.parallelsymmetry.escape.utility.Parameters;
import com.parallelsymmetry.escape.utility.agent.Worker;

/**
 * Provides a facade to the standard Java logging architecture. This facade
 * simply provides convenience methods for the developer.
 * 
 * @author Mark Soderquist
 */
public class Log {

	private static SortedSet<CustomLevel> known = new ConcurrentSkipListSet<CustomLevel>();

	public static final Level NONE = new CustomLevel( "NONE", Integer.MAX_VALUE );

	public static final Level ERROR = new CustomLevel( "ERROR", 1000 );

	public static final Level WARN = new CustomLevel( "WARN", 900 );

	public static final Level INFO = new CustomLevel( "INFO", 800 );

	public static final Level TRACE = new CustomLevel( "TRACE", 700 );

	public static final Level DEBUG = new CustomLevel( "DEBUG", 600 );

	public static final Level DETAIL = new CustomLevel( "DETAIL", 500 );

	public static final Level ALL = new CustomLevel( "ALL", Integer.MIN_VALUE );

	public static final Level DEFAULT_LOG_LEVEL = INFO;

	public static final Handler DEFAULT_HANDLER = new DefaultHandler( System.out );

	public static final Formatter DEFAULT_FORMATTER = new DefaultFormatter();

	public static final String DEFAULT_LOGGER_NAME = Logger.GLOBAL_LOGGER_NAME;

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

	public static final String DEFAULT_LOG_FILE_NAME = "program.log";

	private static Map<Logger, Handler> defaultHandlers = new HashMap<Logger, Handler>();

	private static boolean showTag = true;

	private static boolean showDate;

	private static boolean showColor;

	private static boolean showPrefix;

	private static LogDaemon daemon;

	static {
		Logger defaultLogger = Logger.getLogger( DEFAULT_LOGGER_NAME );
		defaultLogger.setLevel( Level.ALL );
		defaultLogger.setUseParentHandlers( false );
		defaultLogger.addHandler( DEFAULT_HANDLER );

		DEFAULT_HANDLER.setLevel( DEFAULT_LOG_LEVEL );
		defaultHandlers.put( defaultLogger, DEFAULT_HANDLER );
	}

	/**
	 * Initialize the log framework with values from a parameters object.
	 * 
	 * @param parameters
	 * @return
	 */
	public static final void init( Parameters parameters ) {
		if( parameters.isSet( LogFlag.LOG_TAG ) ) Log.setShowTag( parameters.isTrue( LogFlag.LOG_TAG ) );
		if( parameters.isSet( LogFlag.LOG_COLOR ) ) Log.setShowColor( parameters.isTrue( LogFlag.LOG_COLOR ) );
		if( parameters.isSet( LogFlag.LOG_PREFIX ) ) Log.setShowPrefix( parameters.isTrue( LogFlag.LOG_PREFIX ) );
		if( parameters.isSet( LogFlag.LOG_LEVEL ) ) Log.setLevel( Log.parseLevel( parameters.get( LogFlag.LOG_LEVEL ) ) );

		if( parameters.isSet( LogFlag.LOG_FILE ) ) {
			try {
				String pattern = parameters.get( LogFlag.LOG_FILE );
				if( parameters.isTrue( LogFlag.LOG_FILE ) ) pattern = DEFAULT_LOG_FILE_NAME;
				FileHandler handler = new FileHandler( pattern, parameters.isTrue( LogFlag.LOG_FILE_APPEND ) );
				handler.setLevel( Log.INFO );
				if( parameters.isSet( LogFlag.LOG_FILE_LEVEL ) ) handler.setLevel( Log.parseLevel( parameters.get( LogFlag.LOG_FILE_LEVEL ) ) );
				handler.setFormatter( new DefaultFormatter() );
				addHandler( handler );
			} catch( IOException exception ) {
				Log.write( exception );
			}
		}
	}

	/**
	 * Determine if the specified log level is active.
	 */
	public static final boolean isActive( Level level ) {
		return getLevel().intValue() <= level.intValue();
	}

	/**
	 * Get the log level of the default handler.
	 * 
	 * @return The default handler log level.
	 */
	public static final Level getLevel() {
		return getLevel( DEFAULT_LOGGER_NAME );
	}

	/**
	 * This log level affects only the default handler.
	 * 
	 * @param level
	 */
	public static final void setLevel( Level level ) {
		setLevel( DEFAULT_LOGGER_NAME, level );
	}

	public static final Level getLevel( String name ) {
		return defaultHandlers.get( getLogger( name ) ).getLevel();
	}

	public static final void setLevel( String name, Level level ) {
		if( level == null ) return;
		defaultHandlers.get( getLogger( name ) ).setLevel( level );
	}

	public static final boolean isShowTag() {
		return showTag;
	}

	public static final void setShowTag( boolean showTag ) {
		Log.showTag = showTag;
	}

	public static final boolean isShowDate() {
		return showDate;
	}

	public static final void setShowDate( boolean showDate ) {
		Log.showDate = showDate;
	}

	public static final boolean isShowColor() {
		return showColor;
	}

	public static final void setShowColor( boolean showColor ) {
		Log.showColor = showColor;
	}

	public static final boolean isShowPrefix() {
		return showPrefix;
	}

	public static final void setShowPrefix( boolean showPrefix ) {
		Log.showPrefix = showPrefix;
	}

	public static final void addHandler( Handler handler ) {
		addHandler( DEFAULT_LOGGER_NAME, handler );
	}

	public static final void removeHandler( Handler handler ) {
		removeHandler( DEFAULT_LOGGER_NAME, handler );
	}

	public static final void addHandler( String name, Handler handler ) {
		getLogger( name ).addHandler( handler );
	}

	public static final void removeHandler( String name, Handler handler ) {
		getLogger( name ).removeHandler( handler );
	}

	public static final Handler getDefaultHandler() {
		return getDefaultHandler( DEFAULT_LOGGER_NAME );
	}

	public static final Handler getDefaultHandler( String name ) {
		return defaultHandlers.get( getLogger( name ) );
	}

	public static final void setDefaultHandler( Handler handler ) {
		setDefaultHandler( DEFAULT_LOGGER_NAME, handler );
	}

	public static final void setDefaultHandler( String name, Handler handler ) {
		Logger logger = getLogger( name );
		Handler oldHandler = defaultHandlers.get( logger );
		logger.removeHandler( oldHandler );

		handler.setLevel( oldHandler.getLevel() );

		defaultHandlers.put( logger, handler );
		logger.addHandler( handler );
	}

	public static final void write() {
		write( INFO, "" );
	}

	public static final void writeTo( String name ) {
		writeTo( name, INFO, "" );
	}

	public static final void write( Level level ) {
		write( level, "" );
	}

	public static final void writeTo( String name, Level level ) {
		writeTo( name, level, "" );
	}

	public static final void write( Object... message ) {
		write( INFO, message );
	}

	public static final void writeTo( String name, Object... message ) {
		writeTo( name, INFO, message );
	}

	public static final void write( Level level, Object... message ) {
		write( level, null, message );
	}

	public static final void writeTo( String name, Level level, Object... message ) {
		writeTo( name, level, null, message );
	}

	public static final void write( Throwable throwable ) {
		write( throwable, (Object[])null );
	}

	public static final void writeTo( String name, Throwable throwable ) {
		writeTo( name, throwable, (Object[])null );
	}

	public static final void write( Throwable throwable, Object... message ) {
		write( ERROR, throwable, message );
	}

	public static final void writeTo( String name, Throwable throwable, Object... message ) {
		writeTo( name, ERROR, throwable, message );
	}

	public static final void write( Level level, Throwable throwable ) {
		write( level, throwable, (Object[])null );
	}

	public static final void writeTo( String name, Level level, Throwable throwable ) {
		writeTo( name, level, throwable, (Object[])null );
	}

	public static final void write( Level level, Throwable throwable, Object... message ) {
		writeTo( null, level, throwable, message );
	}

	public static final void writeTo( String name, Level level, Throwable throwable, Object... message ) {
		StringBuilder builder = null;
		if( message != null ) {
			builder = new StringBuilder();
			for( Object object : message ) {
				builder.append( object == null ? "null" : object.toString() );
			}
		}

		LogRecord record = new LogRecord( level == null ? DEFAULT_LOG_LEVEL : level, builder == null ? null : builder.toString() );
		if( throwable != null ) record.setThrown( throwable );
		StackTraceElement caller = getCaller();
		record.setSourceClassName( caller.getClassName() );
		record.setSourceMethodName( caller.getMethodName() );
		writeTo( name, record );
	}

	public static final void write( LogRecord record ) {
		writeTo( DEFAULT_LOGGER_NAME, record );
	}

	/**
	 * Records written to named loggers are not written to the default logger.
	 * 
	 * @param name
	 * @param record
	 */
	public static final void writeTo( String name, LogRecord record ) {
		if( isDaemon() ) {
			daemon.submit( name, record );
		} else {
			doWriteTo( name, record );
		}
	}

	public static final Level getLevel( int value ) {
		List<CustomLevel> levels = new ArrayList<CustomLevel>( known );
		Collections.sort( levels );

		Level level = null;

		int count = levels.size();
		for( int index = 0; index < count; index++ ) {
			// FIXME Finish this method.
		}

		return level;
	}

	public static final SortedSet<? extends Level> getLevels() {
		return new ConcurrentSkipListSet<CustomLevel>( known );
	}

	public static final Level parseLevel( int value ) {
		List<CustomLevel> levels = new ArrayList<CustomLevel>( known );
		Collections.sort( levels );

		Level level = null;

		int count = levels.size();
		for( int index = 0; index < count; index++ ) {
			Level check = levels.get( index );
			if( check.intValue() > value ) break;
			level = check;
		}

		return level;
	}

	public static final Level parseLevel( String string ) {
		if( string == null ) return null;

		string = string.toUpperCase();

		for( Level level : known ) {
			if( level.getName().equals( string ) ) return level;
		}

		return null;
	}

	public static final void writeSystemProperties() {
		Set<String> keySet = System.getProperties().stringPropertyNames();
		List<String> keys = new ArrayList<String>( keySet.size() );
		keys.addAll( keySet );
		Collections.sort( keys );
		for( String key : keys ) {
			write( key + " = " + System.getProperty( key ) );
		}
	}

	public synchronized static final boolean isDaemon() {
		return daemon != null && daemon.isRunning();
	}

	public synchronized static final void startDaemon() {
		if( isDaemon() ) return;
		daemon = new LogDaemon();
		daemon.start();
	}

	public synchronized static final void stopDaemon() {
		if( !isDaemon() ) return;
		daemon.stop();
		daemon = null;
	}

	private static final StackTraceElement getCaller() {
		StackTraceElement elements[] = Thread.currentThread().getStackTrace();

		int index = 0;
		while( index < elements.length ) {
			StackTraceElement frame = elements[index];
			String clazz = frame.getClassName();
			if( clazz.equals( Log.class.getName() ) ) {
				break;
			}
			index++;
		}

		while( index < elements.length ) {
			StackTraceElement frame = elements[index];
			String clazz = frame.getClassName();
			if( !clazz.equals( Log.class.getName() ) ) {
				return frame;
			}
			index++;
		}

		return null;
	}

	private static final Logger getLogger( String name ) {
		Logger logger = Logger.getLogger( name );

		synchronized( defaultHandlers ) {
			// Ensure a default handler exists for the logger.
			if( defaultHandlers.get( logger ) == null ) {
				logger.setUseParentHandlers( false );
				logger.setLevel( ALL );

				Handler handler = new DefaultHandler( System.out );
				logger.addHandler( handler );
				handler.setLevel( INFO );

				defaultHandlers.put( logger, handler );
			}
		}

		return logger;
	}

	private static final void doWriteTo( String name, LogRecord record ) {
		getLogger( name == null ? DEFAULT_LOGGER_NAME : name ).log( record );
	}

	private static class LogDaemon extends Worker {

		private BlockingQueue<LogRequest> queue = new LinkedBlockingQueue<LogRequest>();

		public LogDaemon() {
			super( "Log Daemon", true );
			setInterruptOnStop( true );
		}

		@Override
		public void run() {
			while( shouldExecute() ) {
				try {
					LogRequest request = queue.take();
					doWriteTo( request.getName(), request.getRecord() );
				} catch( InterruptedException exception ) {
					// Intentionally ignore exception.
				}
			}
		}

		public void submit( String name, LogRecord record ) {
			queue.offer( new LogRequest( name, record ) );
		}

		private class LogRequest {

			private String name;

			private LogRecord record;

			public LogRequest( String name, LogRecord record ) {
				this.name = name;
				this.record = record;
			}

			public String getName() {
				return name;
			}

			public LogRecord getRecord() {
				return record;
			}

		}

	}

	private static class CustomLevel extends Level implements Comparable<CustomLevel> {

		private static final long serialVersionUID = -7853455775674488102L;

		protected CustomLevel( String name, int value ) {
			super( name, value );
			synchronized( CustomLevel.class ) {
				known.add( this );
			}
		}

		@Override
		public int compareTo( CustomLevel that ) {
			int thisValue = this.intValue();
			int thatValue = that.intValue();
			return ( thisValue < thatValue ? -1 : ( thisValue == thatValue ? 0 : 1 ) );
		}

	}

}
