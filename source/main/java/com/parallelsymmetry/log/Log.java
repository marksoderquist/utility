package com.parallelsymmetry.log;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * Provides a facade to the standard Java logging architecture. This facade
 * simply provides convenience methods for the developer.
 * 
 * @author Mark Soderquist
 */
public class Log {

	public static final Level NONE = new CustomLevel( "NONE", Level.OFF.intValue() );

	public static final Level ERROR = new CustomLevel( "ERROR", Level.SEVERE.intValue() );

	public static final Level WARN = new CustomLevel( "WARN", Level.WARNING.intValue() );

	public static final Level INFO = Level.INFO;

	public static final Level TRACE = new CustomLevel( "TRACE", Level.CONFIG.intValue() );

	public static final Level DEBUG = new CustomLevel( "DEBUG", Level.FINE.intValue() );

	public static final Level ALL = Level.ALL;

	public static final Level DEFAULT_LOG_LEVEL = INFO;

	public static final Handler DEFAULT_HANDLER = new DefaultHandler( System.out, System.err );

	public static final Formatter DEFAULT_FORMATTER = new DefaultFormatter();

	public static final String DEFAULT_LOGGER_NAME = Logger.GLOBAL_LOGGER_NAME;

	private static Map<Logger, Handler> defaultHandlers = new HashMap<Logger, Handler>();

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
		return defaultHandlers.get( getNamedLogger( name ) ).getLevel();
		//return getNamedLogger( name ).getLevel();
	}

	public static final void setLevel( String name, Level level ) {
		defaultHandlers.get( getNamedLogger( name ) ).setLevel( level == null ? DEFAULT_LOG_LEVEL : level );
		//getNamedLogger( name ).setLevel( level == null ? DEFAULT_LOG_LEVEL : level );
	}

	public static final void addHandler( Handler handler ) {
		addHandler( DEFAULT_LOGGER_NAME, handler );
	}

	public static final void removeHandler( Handler handler ) {
		removeHandler( DEFAULT_LOGGER_NAME, handler );
	}

	public static final void addHandler( String name, Handler handler ) {
		Logger.getLogger( name ).addHandler( handler );
	}

	public static final void removeHandler( String name, Handler handler ) {
		Logger.getLogger( name ).removeHandler( handler );
	}

	public static final Handler getDefaultHandler() {
		return getDefaultHandler( DEFAULT_LOGGER_NAME );
	}

	public static final Handler getDefaultHandler( String name ) {
		return defaultHandlers.get( getNamedLogger( name ) );
	}

	public static final void setDefaultHandler( Handler handler ) {
		setDefaultHandler( DEFAULT_LOGGER_NAME, handler );
	}

	public static final void setDefaultHandler( String name, Handler handler ) {
		Logger logger = getNamedLogger( name );
		Handler oldHandler = defaultHandlers.get( logger );
		logger.removeHandler( oldHandler );

		handler.setLevel( oldHandler.getLevel() );

		defaultHandlers.put( logger, handler );
		logger.addHandler( handler );
	}

	public static final void write() {
		write( INFO, "" );
	}

	public static final void writeToLogger( String name ) {
		writeToLogger( name, INFO, "" );
	}

	public static final void write( Level level ) {
		write( level, "" );
	}

	public static final void writeTolLogger( String name, Level level ) {
		writeToLogger( name, level, "" );
	}

	public static final void write( Object... message ) {
		write( INFO, message );
	}

	public static final void writeToLogger( String name, Object... message ) {
		writeToLogger( name, INFO, message );
	}

	public static final void write( Level level, Object... message ) {
		write( level, null, message );
	}

	public static final void writeToLogger( String name, Level level, Object... message ) {
		writeToLogger( name, level, null, message );
	}

	public static final void write( Throwable throwable ) {
		write( throwable, (Object[])null );
	}

	public static final void writeToLogger( String name, Throwable throwable ) {
		writeToLogger( name, throwable, (Object[])null );
	}

	public static final void write( Throwable throwable, Object... message ) {
		write( ERROR, throwable, message );
	}

	public static final void writeToLogger( String name, Throwable throwable, Object... message ) {
		writeToLogger( name, ERROR, throwable, message );
	}

	public static final void write( Level level, Throwable throwable ) {
		write( level, throwable, (Object[])null );
	}

	public static final void writeToLogger( String name, Level level, Throwable throwable ) {
		writeToLogger( name, level, throwable, (Object[])null );
	}

	public static final void write( Level level, Throwable throwable, Object... message ) {
		writeToLogger( null, level, throwable, message );
	}

	public static final void writeToLogger( String name, Level level, Throwable throwable, Object... message ) {
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
		writeToLogger( name == null ? DEFAULT_LOGGER_NAME : name, record );
	}

	public static final void write( LogRecord record ) {
		writeToLogger( DEFAULT_LOGGER_NAME, record );
	}

	/**
	 * Records written to named loggers are not written to the default logger.
	 * 
	 * @param name
	 * @param record
	 */
	public static final void writeToLogger( String name, LogRecord record ) {
		getNamedLogger( name ).log( record );
	}

	public static final Level parseLevel( String string ) {
		if( string == null ) return INFO;

		string = string.toLowerCase();
		if( NONE.getName().toLowerCase().equals( string ) ) {
			return NONE;
		} else if( ERROR.getName().toLowerCase().equals( string ) ) {
			return ERROR;
		} else if( WARN.getName().toLowerCase().equals( string ) ) {
			return WARN;
		} else if( INFO.getName().toLowerCase().equals( string ) ) {
			return INFO;
		} else if( TRACE.getName().toLowerCase().equals( string ) ) {
			return TRACE;
		} else if( DEBUG.getName().toLowerCase().equals( string ) ) {
			return DEBUG;
		} else if( ALL.getName().toLowerCase().equals( string ) ) {
			return ALL;
		}

		return INFO;
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

	private static final Logger getNamedLogger( String name ) {
		Logger logger = Logger.getLogger( name );

		synchronized( defaultHandlers ) {
			if( !defaultHandlers.keySet().contains( logger ) ) {
				logger.setUseParentHandlers( false );
				logger.setLevel( ALL );

				Handler handler = new DefaultHandler( System.out, System.err );
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
			StackTraceElement frame = elements[ index ];
			String clazz = frame.getClassName();
			if( clazz.equals( Log.class.getName() ) ) {
				break;
			}
			index++;
		}

		while( index < elements.length ) {
			StackTraceElement frame = elements[ index ];
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

	private static class DefaultHandler extends Handler {

		private StreamHandler outputHandler;

		private StreamHandler errorHandler;

		public DefaultHandler( OutputStream stream ) {
			this( stream, stream );
		}

		public DefaultHandler( OutputStream stream, OutputStream error ) {
			outputHandler = new StreamHandler( stream, new DefaultFormatter() );
			errorHandler = new StreamHandler( error, new DefaultFormatter() );
		}

		@Override
		public void setLevel( Level level ) {
			outputHandler.setLevel( level );
			errorHandler.setLevel( level );
			super.setLevel( level );
		}

		@Override
		public synchronized Level getLevel() {
			return super.getLevel();
		}

		@Override
		public void publish( LogRecord record ) {
			int recordLevel = record.getLevel().intValue();
			if( recordLevel < Log.NONE.intValue() && recordLevel > Log.INFO.intValue() ) {
				errorHandler.publish( record );
			} else {
				outputHandler.publish( record );
			}
			flush();
		}

		@Override
		public void close() {
			outputHandler.close();
			errorHandler.close();
		}

		@Override
		public void flush() {
			outputHandler.flush();
			errorHandler.flush();
		}

		@Override
		public void setEncoding( String encoding ) throws SecurityException, UnsupportedEncodingException {
			outputHandler.setEncoding( encoding );
			errorHandler.setEncoding( encoding );
			super.setEncoding( encoding );
		}

		@Override
		public String getEncoding() {
			return super.getEncoding();
		}

		@Override
		public void setErrorManager( ErrorManager manager ) {
			outputHandler.setErrorManager( manager );
			errorHandler.setErrorManager( manager );
			super.setErrorManager( manager );
		}

		@Override
		public ErrorManager getErrorManager() {
			return super.getErrorManager();
		}

		@Override
		public void setFilter( Filter filter ) throws SecurityException {
			outputHandler.setFilter( filter );
			errorHandler.setFilter( filter );
			super.setFilter( filter );
		}

		@Override
		public Filter getFilter() {
			return super.getFilter();
		}

		@Override
		public void setFormatter( Formatter formatter ) throws SecurityException {
			outputHandler.setFormatter( formatter );
			errorHandler.setFormatter( formatter );
			super.setFormatter( formatter );
		}

		@Override
		public Formatter getFormatter() {
			return super.getFormatter();
		}

		@Override
		public boolean isLoggable( LogRecord record ) {
			return super.isLoggable( record );
		}

		@Override
		protected void reportError( String message, Exception exception, int code ) {
			super.reportError( message, exception, code );
		}

	}

	private static class DefaultFormatter extends Formatter {

		@Override
		public String format( LogRecord record ) {
			Throwable thrown = record.getThrown();
			StringBuffer buffer = new StringBuffer();

			if( record.getMessage() != null ) {
				buffer.append( record.getMessage() );
				buffer.append( "\n" );
			}

			if( thrown != null ) {
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter( stringWriter );
				thrown.printStackTrace( printWriter );
				printWriter.close();
				buffer.append( stringWriter.toString() );
			}

			return buffer.toString();
		}

	}

}
