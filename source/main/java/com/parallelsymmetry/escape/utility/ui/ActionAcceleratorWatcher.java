package com.parallelsymmetry.escape.utility.ui;

import java.awt.KeyEventPostProcessor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import com.parallelsymmetry.escape.utility.log.Log;

public class ActionAcceleratorWatcher implements KeyEventPostProcessor {

	enum Match {
		NONE, EXACT, PARTIAL
	};

	private ActionLibrary library;

	private String sequence;

	private boolean block;

	private List<String> accelerators;

	public ActionAcceleratorWatcher( ActionLibrary library ) {
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

			// If starting a new sequence get a new accelerator list.
			if( sequence == null ) {
				sequence = keystroke;
				accelerators = getAccelerators();
			} else {
				sequence = new StringBuilder( sequence ).append( " " ).append( keystroke ).toString();
			}

			// Match the key sequence with a accelerator.
			switch( match( accelerators, sequence ) ) {
				case EXACT: {
					block = processAccelerator( sequence );
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

	private List<String> getAccelerators() {
		List<String> accelerators = new ArrayList<String>();

		accelerators.addAll( library.getAccelerators() );
		Collections.sort( accelerators );

		return accelerators;
	}

	/**
	 * Match the sequence to one of the accelerators.
	 * 
	 * @param accelerators The accelerators to try and match.
	 * @param sequence The key sequence to look for.
	 * @return Match.NONE if the sequence does not match any accelerators.<br/>
	 *         Match.PARTIAL if the accelerator matches the start of a
	 *         accelerator.<br>
	 *         Match.EXACT if the sequence exactly matches a accelerator.
	 */
	private Match match( List<String> accelerators, String sequence ) {
		if( sequence == null ) return Match.NONE;

		Log.write( Log.DEBUG, "Match key sequence: " + sequence );

		int index = Collections.binarySearch( accelerators, sequence );
		if( index >= 0 ) return Match.EXACT;

		// Check for start with matches.
		index = -index - 1;
		List<String> matches = new ArrayList<String>();
		String accelerator = null;
		int count = accelerators.size();
		while( index < count ) {
			accelerator = (String)accelerators.get( index++ );
			if( sequenceStartsWith( accelerator, sequence ) ) {
				matches.add( accelerator );
			} else {
				index = accelerators.size();
			}
		}

		// Trim the list down to just those that matched.
		if( matches.size() > 0 ) {
			accelerators = matches;
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
	 * Lookup the action for the accelerator and cause an event to be sent.
	 */
	private boolean processAccelerator( String accelerator ) {
		Log.write( Log.DEBUG, "Accelerator typed: " + accelerator );
		ActionEvent event = new ActionEvent( this, ActionEvent.ACTION_PERFORMED, accelerator );
		XAction action = library.getActionByAccelerator( accelerator );
		reset();

		if( action == null ) {
			Log.write( Log.WARN, "Accelerator action not found: " + accelerator );
			return false;
		}

		boolean result = action.performAction( event );

		if( result == false ) {
			Log.write( Log.WARN, "Accelerator not used: " + accelerator );
		} else {
			Log.write( Log.DEBUG, "Accelerator used:  " + accelerator );
		}

		return result;
	}

	private void reset() {
		sequence = null;
	}

}
