package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Parameters class is used to convert command line parameters into an
 * object.
 * <p>
 * <h2>Flags</h2>Flags start with the '-' character and may be followed by an
 * optional value. If the flag is not followed by a value but instead is
 * followed by another flag the value for the flag is set to "true".
 * <blockquote> Examples:
 * <table border="1" cellspacing="0">
 * <tr>
 * <th align="left"><code>-test</code></th>
 * <td>Sets the <code>test</code> flag to <code>true</code>.</td>
 * </tr>
 * <tr>
 * <th align="left"><code>-level debug</code></th>
 * <td>Sets the <code>level</code> flag to <code>debug</code>.</td>
 * </tr>
 * <tr>
 * <th align="left"><code>-test -level debug</code></th>
 * <td>Sets the <code>test</code> flag to <code>true</code> and the
 * <code>level</code> flag to <code>debug</code>.</td>
 * </tr>
 * </table>
 * </blockquote>
 * <h2>Files</h2> File names are specified after all flags have been specified.
 * A file name is a non-flagged value or value after the terminator
 * &quot;--&quot; string. Be aware that once the Parameters class thinks that it
 * has found a file name, all parameters after that are considered file names.
 * <blockquote> Examples:
 * <table border="1" cellspacing="0">
 * <tr>
 * <th align="left"><code>apple.txt</code></th>
 * <td>Adds the file <code>apple.txt</code> to the list of files.</td>
 * </tr>
 * <tr>
 * <th align="left"><code>-test -- apple.txt</code></th>
 * <td>Sets the <code>test</code> flag to <code>true</code> and adds the file
 * <code>apple.txt</code> to the list of files.</td>
 * </tr>
 * <tr>
 * <th align="left"><code>-level debug apple.txt</code></th>
 * <td>Sets the <code>level</code> flag to <code>debug</code> and adds the file
 * <code>apple.txt</code> to the list of files.</td>
 * </tr>
 * <tr>
 * <th align="left"><code>apple.txt -level debug</code></th>
 * <td>Add the files <code>apple.txt</code>, <code>-level</code>, and
 * <code>debug</code> to the list of files.</td>
 * </tr>
 * </table>
 * </blockquote>
 * <p>
 * <h2>Validation</h2> The Parameters class can validate command line flags by
 * passing a set of valid flags to the parse() method. If a flag is specified in
 * the command line that does not match the valid set an
 * InvalidParameterException is thrown.
 * <p>
 * 
 * @author Mark Soderquist
 */
public class Parameters {

	private static final String FLAG_PREFIX = "-";

	private static final String TERMINATOR = "--";

	private String[] commands;

	private Map<String, String[]> values;

	private List<File> files;

	private Parameters( String[] commands, Map<String, String[]> values, List<File> files ) {
		this.commands = Arrays.copyOf( commands, commands.length );
		this.values = values;
		this.files = files;
	}

	public static final Parameters parse( String[] commands ) {
		return parse( commands, (Set<String>)null );
	}

	public static final Parameters parse( String[] commands, String... flags ) {
		return parse( commands, new HashSet<String>( Arrays.asList( flags ) ) );
	}

	public static final Parameters parse( String[] commands, Set<String> flags ) {
		Map<String, String[]> values = new HashMap<String, String[]>();
		List<File> files = new ArrayList<File>();

		boolean terminated = false;

		for( int index = 0; index < commands.length; index++ ) {
			String command = commands[index];

			if( command == null ) throw new IllegalArgumentException( "Null command at index: " + index );

			if( TERMINATOR.equals( command ) ) {
				terminated = true;
			} else if( !terminated && command.startsWith( TERMINATOR ) ) {
				String flag = command.substring( 2 );

				if( flags != null && !flags.contains( flag ) ) throw new InvalidParameterException( "Unknown flag: " + TERMINATOR + flag );

				List<String> valueList = new ArrayList<String>();
				while( ( commands.length > index + 1 ) && ( !commands[index + 1].startsWith( FLAG_PREFIX ) ) ) {
					valueList.add( commands[index + 1] );
					index++;
				}
				if( valueList.size() == 0 ) valueList.add( "true" );

				values.put( flag, valueList.toArray( new String[valueList.size()] ) );
			} else if( !terminated && command.startsWith( FLAG_PREFIX ) ) {
				String flag = command.substring( 1 );

				if( flags != null && !flags.contains( flag ) ) throw new InvalidParameterException( "Unknown flag: " + FLAG_PREFIX + flag );

				List<String> valueList = new ArrayList<String>();
				if( ( commands.length > index + 1 ) && ( !commands[index + 1].startsWith( FLAG_PREFIX ) ) ) {
					valueList.add( commands[index + 1] );
					index++;
				}
				if( valueList.size() == 0 ) valueList.add( "true" );

				values.put( flag, valueList.toArray( new String[valueList.size()] ) );
			} else {
				terminated = true;
				files.add( new File( command ) );
			}

		}

		return new Parameters( Arrays.copyOf( commands, commands.length ), values, files );
	}

	public int size() {
		return values.size();
	}

	public String get( String name ) {
		String[] values = this.values.get( name );
		return values == null ? null : values[0];
	}

	public String get( String name, String defaultValue ) {
		String value = get( name );
		return value != null ? value : defaultValue;
	}

	public String[] getValues( String name ) {
		return values.get( name );
	}

	public boolean isSet( String name ) {
		String value = get( name );
		return !( value == null || "false".equals( value ) );
	}

	public List<File> getFiles() {
		return files;
	}

	public String[] getCommands() {
		return commands;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		for( String command : commands ) {
			builder.append( command );
			builder.append( " " );
		}

		return builder.toString().trim();
	}
}
