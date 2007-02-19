package org.novaworx.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
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

	public static final Level CONFIG = Level.CONFIG;

	public static final Level DEBUG = new CustomLevel( "DEBUG", Level.FINE.intValue() );

	public static final Level ALL = Level.ALL;

	private static final String name = "clearview";

	static {
		Logger.getLogger( name ).setUseParentHandlers( false );

		Handler handler = new CustomHandler( System.out, new CustomFormatter() );
		handler.setLevel( Level.ALL );
		Logger.getLogger( name ).addHandler( handler );
	}

	public static final void setLevel( Level level ) {
		Logger.getLogger( name ).setLevel( level );
	}

	public static final void addHandler( Handler handler ) {
		Logger.getLogger( name ).addHandler( handler );
	}

	public static final void write() {
		write( INFO, "", null );
	}

	public static final void write( Level level ) {
		write( level, "", null );
	}

	public static final void write( String message ) {
		write( INFO, message, null );
	}

	public static final void write( Level level, String message ) {
		write( level, message, null );
	}

	public static final void write( Throwable throwable ) {
		write( (String)null, throwable );
	}

	public static final void write( String message, Throwable throwable ) {
		write( ERROR, message, throwable );
	}

	public static final void write( Level level, Throwable throwable ) {
		write( level, null, throwable );
	}

	public static final void write( Level level, String message, Throwable throwable ) {
		LogRecord record = new LogRecord( level, message );
		if( throwable != null ) record.setThrown( throwable );
		StackTraceElement caller = getCaller();
		record.setSourceClassName( caller.getClassName() );
		record.setSourceMethodName( caller.getMethodName() );
		Logger.getLogger( name ).log( record );
	}

	public static final Level parseLevel( String string ) {
		if( string == null ) return INFO;

		string = string.toLowerCase();
		if( "none".equals( string ) ) {
			return NONE;
		} else if( "error".equals( string ) ) {
			return ERROR;
		} else if( "warn".equals( string ) ) {
			return WARN;
		} else if( "info".equals( string ) ) {
			return INFO;
		} else if( "config".equals( string ) ) {
			return CONFIG;
		} else if( "debug".equals( string ) ) {
			return DEBUG;
		} else if( "all".equals( string ) ) {
			return ALL;
		}

		return INFO;
	}

	private static StackTraceElement getCaller() {
		StackTraceElement elements[] = ( new Throwable() ).getStackTrace();

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

	private static class CustomHandler extends StreamHandler {

		public CustomHandler( OutputStream stream, Formatter formatter ) {
			super( stream, formatter );
		}

		public void publish( LogRecord record ) {
			super.publish( record );
			flush();
		}

		public void close() {
			flush();
		}
	}

	private static class CustomFormatter extends Formatter {

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
