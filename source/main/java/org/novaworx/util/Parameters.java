/*
 * Copyright 2006 Mark Soderquist. All rights reserved.
 */
package org.novaworx.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parameters {

	private String[] commands;

	private Map<String, String> values;

	private List<String> files;

	private Parameters( String[] commands, Map<String, String> values, List<String> files ) {
		this.commands = commands;
		this.values = values;
		this.files = files;
	}

	public static final Parameters parse( String[] commands ) {
		return parse( commands, new String[ 0 ] );
	}

	public static final Parameters parse( String[] commands, String... flags ) {
		Map<String, String> values = new HashMap<String, String>();
		List<String> files = new ArrayList<String>();
		for( int index = 0; index < flags.length; index++ ) {
			if( !flags[ index ].startsWith( "-" ) ) {
				flags[ index ] = "-" + flags[ index ];
			}
		}
		Arrays.sort( flags );

		int index = 0;
		boolean flagTerminatorFound = false;
		for( ; index < commands.length; index++ ) {
			String parameter = commands[ index ];
			String next = index + 1 < commands.length ? commands[ index + 1 ] : null;
			if( parameter.startsWith( "-" ) ) {
				if( "--".equals( parameter ) ) {
					flagTerminatorFound = true;
					index++;
					break;
				} else if( Arrays.binarySearch( flags, parameter ) >= 0 || next == null || next.startsWith( "-" ) ) {
					// Flag
					values.put( parameter.substring( 1 ), "true" );
				} else {
					// Value
					values.put( parameter.substring( 1 ), next );
					index++;
				}
			} else {
				// File
				files.add( parameter );
			}
		}

		if( flagTerminatorFound ) {
			for( ; index < commands.length; index++ ) {
				String parameter = commands[ index ];
				files.add( parameter );
			}
		}

		return new Parameters( Arrays.copyOf( commands, commands.length ), values, files );
	}

	public String[] getCommands() {
		return commands;
	}

	public boolean isSet( String key ) {
		return get( key ) != null;
	}

	public String get( String key ) {
		return values.get( key );
	}

	public List<String> getFiles() {
		return Collections.unmodifiableList( files );
	}

}
