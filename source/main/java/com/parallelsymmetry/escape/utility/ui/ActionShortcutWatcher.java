package com.parallelsymmetry.escape.utility.ui;

import java.awt.KeyEventPostProcessor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.parallelsymmetry.escape.utility.log.Log;

public class ActionShortcutWatcher implements KeyEventPostProcessor {

	enum Match {
		NONE, EXACT, PARTIAL
	};

	private ActionLibrary library;

	private String sequence;

	private boolean block;

	private List<String> shortcuts;

	public ActionShortcutWatcher( ActionLibrary library ) {
		this.library = library;
	}

	@Override
	public boolean postProcessKeyEvent( KeyEvent event ) {
		if( event.isConsumed() ) return false;

		if( event.getID() == KeyEvent.KEY_PRESSED ) {

			// Don't pay attention to the modifier keys.
			int code = event.getKeyCode();
			switch( code ) {
				case KeyEvent.VK_CONTROL:
				case KeyEvent.VK_SHIFT:
				case KeyEvent.VK_ALT:
				case KeyEvent.VK_META: {
					return false;
				}
			}

			// Check for the escape key.
			if( event.getKeyCode() == KeyEvent.VK_ESCAPE ) {
				reset();
				return false;
			}

			// Get the keystroke string representation.
			String keystroke = XAction.encodeKeyEvent( event );

			// If starting a new sequence get a new shortcut list.
			if( sequence == null ) {
				sequence = keystroke;
				shortcuts = getShortcuts();
			} else {
				sequence = new StringBuilder( sequence ).append( " " ).append( keystroke ).toString();
			}

			// Match the key sequence with a shortcut.
			switch( match( shortcuts, sequence ) ) {
				case EXACT: {
					block = processShortcut( sequence );
					return block;
				}
				case PARTIAL: {
					return false;
				}
				case NONE: {
					reset();
					return false;
				}
			}
		} else if( block ) {
			if( event.getID() == KeyEvent.KEY_RELEASED ) block = false;
			return true;
		}

		return false;
	}

	private List<String> getShortcuts() {
		List<String> shortcuts = new ArrayList<String>();

		shortcuts.addAll( library.getShortcuts() );
		Collections.sort( shortcuts );

		return shortcuts;
	}

	/**
	 * Match the sequence to one of the shortcuts.
	 * 
	 * @param shortcuts The shortcuts to try and match.
	 * @param sequence The key sequence to look for.
	 * @return Match.NONE if the sequence does not match any shortcuts.<br/>
	 *         Match.PARTIAL if the shortcut matches the start of a shortcut.<br>
	 *         Match.EXACT if the sequence exactly matches a shortcut.
	 */
	private Match match( List<String> shortcuts, String sequence ) {
		if( sequence == null ) return Match.NONE;

		Log.write( Log.DEBUG, "Match key sequence: " + sequence );

		int index = Collections.binarySearch( shortcuts, sequence );
		if( index >= 0 ) return Match.EXACT;

		// Check for start with matches.
		index = -index - 1;
		List<String> matches = new ArrayList<String>();
		String shortcut = null;
		int count = shortcuts.size();
		while( index < count ) {
			shortcut = (String)shortcuts.get( index++ );
			if( sequenceStartsWith( shortcut, sequence ) ) {
				matches.add( shortcut );
			} else {
				index = shortcuts.size();
			}
		}

		// Trim the list down to just those that matched.
		if( matches.size() > 0 ) {
			shortcuts = matches;
			return Match.PARTIAL;
		}

		return Match.NONE;
	}

	/**
	 * Checks if sequenceOne starts with the key strokes in sequenceTwo.
	 * 
	 * @param String sequenceOne The sequence to compare to.
	 * @param String sequenceTwo The sequence to check.
	 * @return If sequenceOne starts with the key strokes in sequenceTwo.
	 */
	private boolean sequenceStartsWith( String sequenceOne, String sequenceTwo ) {
		if( sequenceOne == sequenceTwo ) return true;
		if( sequenceOne == null ) return false;
		if( sequenceTwo == null ) return false;

		StringTokenizer tokenizerOne = new StringTokenizer( sequenceOne );
		StringTokenizer tokenizerTwo = new StringTokenizer( sequenceTwo );

		String tokenOne = null;
		String tokenTwo = null;

		while( tokenizerTwo.hasMoreTokens() ) {
			if( !tokenizerOne.hasMoreTokens() ) return false;

			tokenOne = tokenizerOne.nextToken();
			tokenTwo = tokenizerTwo.nextToken();

			if( !tokenOne.equals( tokenTwo ) ) return false;
		}

		return true;
	}

	/**
	 * Lookup the action for the shortcut and cause an event to be sent.
	 */
	private boolean processShortcut( String shortcut ) {
		Log.write( Log.DEBUG, "Shortcut typed: " + shortcut );
		ActionEvent event = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, shortcut );
		XAction action = library.getActionByShortcut( shortcut );
		reset();

		if( action == null ) {
			Log.write( Log.WARN, "Shortcut action not found: " + shortcut );
			return false;
		}

		boolean result = action.performAction( event );

		if( result == false ) {
			Log.write( Log.WARN, "Shortcut not used: " + shortcut );
		} else {
			Log.write( Log.DEBUG, "Shortcut used:  " + shortcut );
		}

		return result;
	}

	private void reset() {
		sequence = null;
	}

}
