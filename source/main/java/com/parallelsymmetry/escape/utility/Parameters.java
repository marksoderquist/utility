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
 * <h2>Single Value Parameters</h2>Single value parameters start with
 * &quot;-&quot; and may be followed by an optional value. If the parameter is
 * not followed by a value but instead is followed by another parameter, the
 * value for the parameter is set to &quot;true&quot;. <blockquote> Examples:
 * <table border="1" cellspacing="0">
 * <tr>
 * <th align="left"><code>-test</code></th>
 * <td>Sets the <code>test</code> parameter to <code>true</code>.</td>
 * </tr>
 * <tr>
 * <th align="left"><code>-level debug</code></th>
 * <td>Sets the <code>level</code> parameter to <code>debug</code>.</td>
 * </tr>
 * <tr>
 * <th align="left"><code>-test -level debug</code></th>
 * <td>Sets the <code>test</code> parameter to <code>true</code> and the
 * <code>level</code> parameter to <code>debug</code>.</td>
 * </tr>
 * </table>
 * </blockquote>
 * <p>
 * <h2>Multiple Value Parameters</h2>Multiple value parameters start with
 * &quot;--&quot; and may be followed by an optional list of values. If the
 * parameter is not followed by a value but instead is followed by another
 * parameter, the value for the parameter is set to &quot;true&quot;.
 * <blockquote> Examples:
 * <table border="1" cellspacing="0">
 * <tr>
 * <th align="left"><code>--levels</code></th>
 * <td>Sets the <code>levels</code> parameter to <code>true</code>.</td>
 * </tr>
 * <tr>
 * <th align="left"><code>--levels debug info warn</code></th>
 * <td>Sets the <code>levels</code> parameter to the list
 * <code>[debug, info, warn]</code>.</td>
 * </tr>
 * </table>
 * </blockquote>
 * <h2>Files</h2> File names are specified after all parameters have been
 * specified. A file name is a non-parameter value or value after the terminator
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
 * <td>Sets the <code>test</code> parameter to <code>true</code> and adds the
 * file <code>apple.txt</code> to the list of files.</td>
 * </tr>
 * <tr>
 * <th align="left"><code>-level debug apple.txt</code></th>
 * <td>Sets the <code>level</code> parameter to <code>debug</code> and adds the
 * file <code>apple.txt</code> to the list of files.</td>
 * </tr>
 * <tr>
 * <th align="left"><code>apple.txt -level debug</code></th>
 * <td>Add the files <code>apple.txt</code>, <code>-level</code>, and
 * <code>debug</code> to the list of files.</td>
 * </tr>
 * </table>
 * </blockquote>
 * <p>
 * <h2>Validation</h2> The Parameters class can validate command line parameters
 * by passing a set of valid parameters to the parse() method. If a parameter is
 * specified in the command line that does not match the valid set an
 * InvalidParameterException is thrown.
 * <p>
 * 
 * @author Mark Soderquist
 */
public class Parameters {

	public static final String SINGLE = "-";

	public static final String MULTIPLE = "--";

	private String[] commands;

	private Map<String, List<String>> values;

	private List<File> files;

	private Parameters( String[] commands, Map<String, List<String>> values, List<File> files ) {
		this.commands = Arrays.copyOf( commands, commands.length );
		this.values = values;
		this.files = files;
	}

	public static final Parameters parse( String[] commands ) {
		return parse( commands, (Set<String>)null );
	}

	public static final Parameters parse( String[] commands, String... validParameters ) {
		return parse( commands, new HashSet<String>( Arrays.asList( validParameters ) ) );
	}

	public static final Parameters parse( String[] commands, Set<String> validParameters ) {
		Map<String, List<String>> values = new HashMap<String, List<String>>();
		List<File> files = new ArrayList<File>();

		boolean terminated = false;

		for( int index = 0; index < commands.length; index++ ) {
			String command = commands[index];

			if( command == null ) throw new IllegalArgumentException( "Null command at index: " + index );

			if( MULTIPLE.equals( command ) ) {
				terminated = true;
			} else if( !terminated && command.startsWith( MULTIPLE ) ) {
				String parameter = command.substring( 2 );

				if( validParameters != null && !validParameters.contains( parameter ) ) throw new InvalidParameterException( "Unknown parameter: " + MULTIPLE + parameter );

				List<String> valueList = new ArrayList<String>();
				while( ( commands.length > index + 1 ) && ( !commands[index + 1].startsWith( SINGLE ) ) ) {
					String value = commands[index + 1];
					if( value.startsWith( "\\-" ) ) value = value.substring( 1 );
					valueList.add( value );
					index++;
				}
				if( valueList.size() == 0 ) valueList.add( "true" );

				values.put( parameter, valueList );
			} else if( !terminated && command.startsWith( SINGLE ) ) {
				String parameter = command.substring( 1 );

				if( validParameters != null && !validParameters.contains( parameter ) ) throw new InvalidParameterException( "Unknown parameter: " + SINGLE + parameter );

				List<String> valueList = new ArrayList<String>();
				if( ( commands.length > index + 1 ) && ( !commands[index + 1].startsWith( SINGLE ) ) ) {
					valueList.add( commands[index + 1] );
					index++;
				}
				if( valueList.size() == 0 ) valueList.add( "true" );

				values.put( parameter, valueList );
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
		List<String> values = this.values.get( name );
		return values == null ? null : values.get( 0 );
	}

	public String get( String name, String defaultValue ) {
		String value = get( name );
		return value != null ? value : defaultValue;
	}

	/**
	 * Returns the parameter value as a boolean. The boolean returned represents
	 * the value true if the parameter value is not null and is equal, ignoring
	 * case, to the string "true".
	 * 
	 * @param name
	 * @return
	 */
	public boolean isSet( String name ) {
		return Boolean.parseBoolean( get( name ) );
	}

	/**
	 * Returns the parameter value as a boolean if it was defined on the command
	 * line. The boolean returned represents the value true if the parameter value
	 * is not null and is equal, ignoring case, to the string "true". If the
	 * parameter was not specified on the command line then the default value is
	 * returned.
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public boolean isSet( String name, boolean defaultValue ) {
		String value = get( name );
		return value == null ? defaultValue : Boolean.parseBoolean( value );
	}

	public List<String> getValues( String name ) {
		return values.get( name );
	}

	/**
	 * Returns whether the parameter was specified on the command line.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isSpecified( String name ) {
		return !( get( name ) == null );
	}

	public List<File> getFiles() {
		return files;
	}

	public Set<String> getNames() {
		return values.keySet();
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
