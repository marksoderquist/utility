package com.parallelsymmetry.escape.utility.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Provides a facade to the standard Java logging architecture. This facade
 * simply provides convenience methods for the developer.
 * 
 * @author Mark Soderquist
 */
public class Log {

	private static final int ERROR_VALUE = 1000;

	private static final int WARN_VALUE = 900;

	private static final int INFO_VALUE = 800;

	private static final int TRACE_VALUE = 700;

	private static final int DEBUG_VALUE = 600;

	public static final Level NONE = new CustomLevel( "NONE", Integer.MAX_VALUE );

	public static final Level ERROR = new CustomLevel( "ERROR", ERROR_VALUE );

	public static final Level WARN = new CustomLevel( "WARN", WARN_VALUE );

	public static final Level INFO = new CustomLevel( "INFO", INFO_VALUE );

	public static final Level TRACE = new CustomLevel( "TRACE", TRACE_VALUE );

	public static final Level DEBUG = new CustomLevel( "DEBUG", DEBUG_VALUE );

	public static final Level ALL = new CustomLevel( "ALL", Integer.MIN_VALUE );

	public static final Level DEFAULT_LOG_LEVEL = INFO;

	public static final Handler DEFAULT_HANDLER = new DefaultHandler( System.out );

	public static final Formatter DEFAULT_FORMATTER = new DefaultFormatter();

	public static final String DEFAULT_LOGGER_NAME = Logger.GLOBAL_LOGGER_NAME;

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

	private static Map<Logger, Handler> defaultHandlers = new HashMap<Logger, Handler>();
	
	private static boolean showTag;

	private static boolean showDate;

	private static boolean showColor;

	private static boolean showPrefix;

	static {
		Logger defaultLogger = Logger.getLogger( DEFAULT_LOGGER_NAME );
		defaultLogger.setLevel( Level.ALL );
		defaultLogger.setUseParentHandlers( false );
		defaultLogger.addHandler( DEFAULT_HANDLER );

		DEFAULT_HANDLER.setLevel( DEFAULT_LOG_LEVEL );
		defaultHandlers.put( defaultLogger, DEFAULT_HANDLER );
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
				if( object != null ) builder.append( object.toString() );
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
		getLogger( name == null ? DEFAULT_LOGGER_NAME : name ).log( record );
	}

	public static final Level parseLevel( String string ) {
		if( string == null ) return null;

		string = string.toUpperCase();
		if( NONE.getName().equals( string ) ) {
			return NONE;
		} else if( ERROR.getName().equals( string ) ) {
			return ERROR;
		} else if( WARN.getName().equals( string ) ) {
			return WARN;
		} else if( INFO.getName().equals( string ) ) {
			return INFO;
		} else if( TRACE.getName().equals( string ) ) {
			return TRACE;
		} else if( DEBUG.getName().equals( string ) ) {
			return DEBUG;
		} else if( ALL.getName().equals( string ) ) {
			return ALL;
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

	private static StackTraceElement getCaller() {
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

	private static class CustomLevel extends Level {

		private static final long serialVersionUID = -7853455775674488102L;

		protected CustomLevel( String name, int value ) {
			super( name, value );
		}

	}

}
