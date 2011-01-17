package com.parallelsymmetry.escape.utility;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

public class MavenVersion implements Comparable<MavenVersion> {

	private static final String SNAPSHOT = "snapshot";

	private String version;

	private String canonical;

	private ListItem items;

	private boolean snapshot;

	public MavenVersion( String version ) {
		parseVersion( version );
	}

	public final void parseVersion( String string ) {
		this.version = string;

		items = new ListItem();

		string = string.toLowerCase( Locale.ENGLISH );

		ListItem list = items;

		Stack<Item> stack = new Stack<Item>();
		stack.push( list );

		boolean isDigit = false;

		int startIndex = 0;

		for( int index = 0; index < string.length(); index++ ) {
			char c = string.charAt( index );

			if( c == '.' ) {
				if( index == startIndex ) {
					list.add( IntegerItem.ZERO );
				} else {
					list.add( parseItem( isDigit, string.substring( startIndex, index ) ) );
				}
				startIndex = index + 1;
			} else if( c == '-' ) {
				if( index == startIndex ) {
					list.add( IntegerItem.ZERO );
				} else {
					list.add( parseItem( isDigit, string.substring( startIndex, index ) ) );
				}
				startIndex = index + 1;

				if( isDigit ) {
					list.normalize();

					if( ( index + 1 < string.length() ) && Character.isDigit( string.charAt( index + 1 ) ) ) {
						list.add( list = new ListItem() );
						stack.push( list );
					}
				}
			} else if( Character.isDigit( c ) ) {
				if( !isDigit && index > startIndex ) {
					list.add( new StringItem( string.substring( startIndex, index ), true ) );
					startIndex = index;
				}

				isDigit = true;
			} else {
				if( isDigit && index > startIndex ) {
					list.add( parseItem( true, string.substring( startIndex, index ) ) );
					startIndex = index;
				}

				isDigit = false;
			}
		}

		if( string.length() > startIndex ) {
			list.add( parseItem( isDigit, string.substring( startIndex ) ) );
		}

		while( !stack.isEmpty() ) {
			list = (ListItem)stack.pop();
			list.normalize();
		}

		canonical = items.toString();
	}

	public final boolean isSnapshot() {
		return snapshot;
	}

	public int compareTo( MavenVersion version ) {
		return items.compareTo( version.items );
	}

	public String toString() {
		return version;
	}

	public boolean equals( Object object ) {
		return ( object instanceof MavenVersion ) && canonical.equals( ( (MavenVersion)object ).canonical );
	}

	public int hashCode() {
		return canonical.hashCode();
	}

	private Item parseItem( boolean isDigit, String buffer ) {
		if( SNAPSHOT.equals( buffer ) ) snapshot = true;
		return isDigit ? new IntegerItem( buffer ) : new StringItem( buffer, false );
	}

	private static interface Item {
		final int INTEGER_ITEM = 0;

		final int STRING_ITEM = 1;

		final int LIST_ITEM = 2;

		int compareTo( Item item );

		int getType();

		boolean isNull();
	}

	/**
	 * Represents a numeric item in the version item list.
	 */
	private static class IntegerItem implements Item {

		private static final BigInteger BigInteger_ZERO = new BigInteger( "0" );

		private final BigInteger value;

		public static final IntegerItem ZERO = new IntegerItem();

		private IntegerItem() {
			this.value = BigInteger_ZERO;
		}

		public IntegerItem( String string ) {
			this.value = new BigInteger( string );
		}

		public int getType() {
			return INTEGER_ITEM;
		}

		public boolean isNull() {
			return BigInteger_ZERO.equals( value );
		}

		public int compareTo( Item item ) {
			if( item == null ) {
				return BigInteger_ZERO.equals( value ) ? 0 : 1;
			}

			switch( item.getType() ) {
				case INTEGER_ITEM: {
					return value.compareTo( ( (IntegerItem)item ).value );
				}

				case STRING_ITEM: {
					return 1;
				}

				case LIST_ITEM: {
					return 1;
				}

				default: {
					throw new RuntimeException( "Invalid item: " + item.getClass() );
				}
			}
		}

		public String toString() {
			return value.toString();
		}
	}

	/**
	 * Represents a string in the version item list, usually a qualifier.
	 */
	private static class StringItem implements Item {

		private static final List<String> QUALIFIERS = Arrays.asList( new String[] { "alpha", "beta", "milestone", "rc", SNAPSHOT, "", "sp" } );

		private static final Map<String, String> ALIASES = new HashMap<String, String>();

		static {
			ALIASES.put( "ga", "" );
			ALIASES.put( "final", "" );
			ALIASES.put( "cr", "rc" );
			ALIASES.put( "patch", "sp" );
			ALIASES.put( "update", "sp" );
		}

		/**
		 * A comparable value for the empty-string qualifier. This one is used to
		 * determine if a given qualifier makes the version older than one without a
		 * qualifier, or more recent.
		 */
		private static final String RELEASE_VERSION_INDEX = String.valueOf( QUALIFIERS.indexOf( "" ) );

		private String value;

		public StringItem( String value, boolean followedByDigit ) {
			if( followedByDigit && value.length() == 1 ) {
				// a1 = alpha-1, b1 = beta-1, m1 = milestone-1, p1 = patch-1, u1 = update-1
				switch( value.charAt( 0 ) ) {
					case 'a': {
						value = "alpha";
						break;
					}
					case 'b': {
						value = "beta";
						break;
					}
					case 'm': {
						value = "milestone";
						break;
					}
					case 'p': {
						value = "patch";
						break;
					}
					case 'u': {
						value = "update";
						break;
					}
				}
			}

			this.value = ALIASES.get( value );
			if( this.value == null ) this.value = value;
		}

		public int getType() {
			return STRING_ITEM;
		}

		public boolean isNull() {
			return ( comparableQualifier( value ).compareTo( RELEASE_VERSION_INDEX ) == 0 );
		}

		/**
		 * Returns a comparable value for a qualifier. This method both takes into
		 * account the ordering of known qualifiers as well as lexical ordering for
		 * unknown qualifiers. just returning an Integer with the index here is
		 * faster, but requires a lot of if/then/else to check for -1 or
		 * QUALIFIERS.size and then resort to lexical ordering. Most comparisons are
		 * decided by the first character, so this is still fast. If more characters
		 * are needed then it requires a lexical sort anyway.
		 * 
		 * @param qualifier
		 * @return an equivalent value that can be used with lexical comparison
		 */
		public static String comparableQualifier( String qualifier ) {
			int index = QUALIFIERS.indexOf( qualifier );
			return index == -1 ? QUALIFIERS.size() + "-" + qualifier : String.valueOf( index );
		}

		public int compareTo( Item item ) {
			if( item == null ) {
				return comparableQualifier( value ).compareTo( RELEASE_VERSION_INDEX );
			}
			switch( item.getType() ) {
				case INTEGER_ITEM: {
					return -1;
				}

				case STRING_ITEM: {
					return comparableQualifier( value ).compareTo( comparableQualifier( ( (StringItem)item ).value ) );
				}

				case LIST_ITEM: {
					return -1;
				}

				default: {
					throw new RuntimeException( "Invalid item: " + item.getClass() );
				}
			}
		}

		public String toString() {
			return value;
		}
	}

	private static class ListItem extends ArrayList<Item> implements Item {

		private static final long serialVersionUID = -4773402270598497998L;

		public int getType() {
			return LIST_ITEM;
		}

		public boolean isNull() {
			return ( size() == 0 );
		}

		void normalize() {
			for( ListIterator<Item> iterator = listIterator( size() ); iterator.hasPrevious(); ) {
				Item item = iterator.previous();
				if( item.isNull() ) {
					iterator.remove();
				} else {
					break;
				}
			}
		}

		public int compareTo( Item item ) {
			if( item == null ) {
				if( size() == 0 ) {
					return 0;
				}
				Item first = get( 0 );
				return first.compareTo( null );
			}
			switch( item.getType() ) {
				case INTEGER_ITEM: {
					return -1;
				}

				case STRING_ITEM: {
					return 1;
				}

				case LIST_ITEM: {
					Iterator<Item> leftIterator = iterator();
					Iterator<Item> rightIterator = ( (ListItem)item ).iterator();

					while( leftIterator.hasNext() || rightIterator.hasNext() ) {
						Item left = leftIterator.hasNext() ? leftIterator.next() : null;
						Item right = rightIterator.hasNext() ? rightIterator.next() : null;

						int result = left == null ? -1 * right.compareTo( left ) : left.compareTo( right );

						if( result != 0 ) return result;
					}

					return 0;
				}

				default: {
					throw new RuntimeException( "invalid item: " + item.getClass() );
				}
			}
		}

		public String toString() {
			StringBuilder buffer = new StringBuilder( "(" );
			for( Iterator<Item> iterator = iterator(); iterator.hasNext(); ) {
				buffer.append( iterator.next() );
				if( iterator.hasNext() ) {
					buffer.append( ',' );
				}
			}
			buffer.append( ')' );
			return buffer.toString();
		}

	}

}
