package com.parallelsymmetry.utility.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import com.parallelsymmetry.utility.DateUtil;
import com.parallelsymmetry.utility.TextUtil;

public class DefaultFormatter extends Formatter {

	private boolean showDate;

	@Override
	public String format( LogRecord record ) {
		if( record == null ) return null;

		CustomLevel level = (CustomLevel)record.getLevel();
		Throwable thrown = record.getThrown();
		StringBuilder buffer = new StringBuilder();

		// Determine the prefix.
		StringBuilder prefix = new StringBuilder();
		if( Log.isShowColor() ) {
			prefix.append( level.getAnsiColor() );
		}
		if( Log.isShowTag() ) {
			String tag = level.getTag();
			prefix.append( level.getTag() );
			prefix.append( "".equals( tag ) ? "" : " " );
		}
		if( ( showDate || Log.isShowDate() ) && record.getLevel().intValue() < Log.HELP.intValue() ) {
			prefix.append( DateUtil.format( new Date( record.getMillis() ), Log.DEFAULT_DATE_FORMAT ) );
			prefix.append( " " );
		}
		if( Log.isShowPrefix() ) {
			prefix.append( level.getPrefix() );
		}

		// Determine the suffix.
		StringBuilder suffix = new StringBuilder();
		if( Log.isShowColor() ) {
			suffix.append( "".equals( level.getAnsiColor() ) ? "" : "\u001b[0m" );
		}

		if( record.getMessage() != null ) {
			buffer.append( TextUtil.prepend( TextUtil.append( record.getMessage(), suffix.toString() ), prefix.toString() ) );
			buffer.append( "\n" );
		}

		if( thrown != null ) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter( stringWriter );
			thrown.printStackTrace( printWriter );
			printWriter.close();

			String stack = stringWriter.toString().trim();

			stack = TextUtil.prepend( stack, prefix.toString() );
			stack = TextUtil.append( stack, suffix.toString() );

			buffer.append( stack );
			buffer.append( "\n" );
		}

		return buffer.toString();
	}

	public void setShowDate( boolean showDate ) {
		this.showDate = showDate;
	}

	//	protected static final String getTag( Level level ) {
	//		return ( (CustomLevel)level ).getTag();
	//	}
	//
	//	protected static final String getTag1( Level level ) {
	//		int index = level.intValue() / 100;
	//
	//		if( index > 11 ) index = 11;
	//		if( index < 0 ) index = 0;
	//
	//		switch( index ) {
	//			case 200: {
	//				return "";
	//			}
	//			case 100: {
	//				return "[V]";
	//			}
	//			// ERROR
	//			case 10: {
	//				return "[E] ";
	//			}
	//			// WARN
	//			case 9: {
	//				return "[W] ";
	//			}
	//			// INFO
	//			case 8: {
	//				return "[I] ";
	//			}
	//			// TRACE
	//			case 7: {
	//				return "[T] ";
	//			}
	//			// DEBUG
	//			case 6: {
	//				return "[D] ";
	//			}
	//			// DETAIL
	//			case 5: {
	//				return "[L] ";
	//			}
	//		}
	//
	//		return "[?]";
	//	}
	//
	//	/**
	//	 * Add ANSI color tags to the specified message for the specified level.
	//	 */
	//	protected static final String getColorPrefix( Level level ) {
	//		int index = level.intValue() / 100;
	//
	//		if( index > 11 ) index = 11;
	//		if( index < 0 ) index = 0;
	//
	//		switch( index ) {
	//			case 11: {
	//				return "";
	//			}
	//			// ERROR
	//			case 10: {
	//				return "\u001b[1m\u001b[31m";
	//			}
	//			// WARN
	//			case 9: {
	//				return "\u001b[1m\u001b[33m";
	//			}
	//			// INFO
	//			case 8: {
	//				return "\u001b[0m";
	//			}
	//			// TRACE
	//			case 7: {
	//				return "\u001b[36m";
	//			}
	//			// DEBUG
	//			case 6: {
	//				return "\u001b[1m\u001b[30m";
	//			}
	//			// DETAIL
	//			case 5: {
	//				return "\u001b[1m\u001b[30m";
	//			}
	//		}
	//
	//		return "";
	//	}
	//
	//	protected static final String getColorSuffix( Level level ) {
	//		int index = level.intValue() / 100;
	//
	//		if( index > 11 ) index = 11;
	//		if( index < 0 ) index = 0;
	//
	//		return ( index > 5 && index < 11 ) ? "\u001b[0m" : "";
	//	}
	//
	//	protected static final String getPrefix( Level level ) {
	//		int index = level.intValue() / 100;
	//
	//		if( index > 11 ) index = 11;
	//		if( index < 0 ) index = 0;
	//
	//		switch( index ) {
	//			case 11: {
	//				return "";
	//			}
	//			// ERROR
	//			case 10: {
	//				return "*";
	//			}
	//			// WARN
	//			case 9: {
	//				return "-";
	//			}
	//			// INFO
	//			case 8: {
	//				return " ";
	//			}
	//			// TRACE
	//			case 7: {
	//				return "  ";
	//			}
	//			// DEBUG
	//			case 6: {
	//				return "   ";
	//			}
	//			// DETAIL
	//			case 5: {
	//				return "    ";
	//			}
	//		}
	//
	//		return "    ";
	//	}

}
