package com.parallelsymmetry.utility.log;

import com.parallelsymmetry.utility.Parameters;
import com.parallelsymmetry.utility.agent.Worker;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;
import java.util.logging.Formatter;

/**
 * Provides a facade to the standard Java logging architecture. This facade
 * simply provides convenience methods for the developer.
 * 
 * @author Mark Soderquist
 */
public class Log {

	static SortedSet<CustomLevel> known = new ConcurrentSkipListSet<CustomLevel>();

	public static final Level NONE = new CustomLevel( "NONE", Level.OFF.intValue(), "", "", "" );

	public static final Level HELP = new CustomLevel( "HELP", 20000, "", "", "" );

	public static final Level DEVEL = new CustomLevel( "DEVEL", 10000, "[V]", "\u001b[1m\u001b[35m", "=" );

	public static final Level ERROR = new CustomLevel( "ERROR", Level.SEVERE.intValue(), "[E]", "\u001b[1m\u001b[31m", "*" );

	public static final Level WARN = new CustomLevel( "WARN", Level.WARNING.intValue(), "[W]", "\u001b[1m\u001b[33m", "-" );

	public static final Level INFO = new CustomLevel( "INFO", Level.INFO.intValue(), "[I]", "\u001b[37m", " " );

	public static final Level TRACE = new CustomLevel( "TRACE", Level.CONFIG.intValue(), "[T]", "\u001b[36m", "  " );

	public static final Level DEBUG = new CustomLevel( "DEBUG", Level.FINE.intValue(), "[D]", "\u001b[32m", "   " );

	public static final Level DETAIL = new CustomLevel( "DETAIL", Level.FINER.intValue(), "[L]", "\u001b[1m\u001b[30m", "    " );

	public static final Level ALL = new CustomLevel( "ALL", Level.ALL.intValue(), "", "", "" );

	public static final Level DEFAULT_LOG_LEVEL = INFO;

	public static final Handler DEFAULT_HANDLER = new DefaultHandler( System.out );

	public static final Formatter DEFAULT_FORMATTER = new DefaultFormatter();

	public static final String DEFAULT_LOGGER_NAME = Logger.GLOBAL_LOGGER_NAME;

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
	public static final void config( Parameters parameters ) {
		if( parameters.isSet( LogFlag.LOG_TAG ) ) Log.setShowTag( parameters.isTrue( LogFlag.LOG_TAG ) );
		if( parameters.isSet( LogFlag.LOG_DATE ) ) Log.setShowDate( parameters.isTrue( LogFlag.LOG_DATE ) );
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

				DefaultFormatter formatter = new DefaultFormatter();
				formatter.setShowDate( true );
				handler.setFormatter( formatter );
				Log.addHandler( handler );
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
		oldHandler.setLevel( Log.ALL );
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
		if( caller != null ) {
			record.setSourceClassName( caller.getClassName() );
			record.setSourceMethodName( caller.getMethodName() );
		}

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

//		synchronized( defaultHandlers ) {
//			// Ensure a default handler exists for the logger.
//			if( defaultHandlers.get( logger ) == null ) {
//				logger.setUseParentHandlers( false );
//				logger.setLevel( ALL );
//
//				Handler handler = new DefaultHandler( System.out );
//				logger.addHandler( handler );
//				handler.setLevel( INFO );
//
//				defaultHandlers.put( logger, handler );
//			}
//		}

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

}
