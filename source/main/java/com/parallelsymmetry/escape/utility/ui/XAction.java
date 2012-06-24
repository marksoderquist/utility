package com.parallelsymmetry.escape.utility.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Deque;
import java.util.StringTokenizer;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import com.parallelsymmetry.escape.utility.log.Log;

/**
 * <p>
 * This class is used to handle the visual aspects of actions. Since components
 * may treat an action specifically this class allows a component specific
 * action to be set as the acting action.
 * <p>
 * The idea being that when a component gains focus it will register the actions
 * that it handles with the appropriate instance of this class. When the
 * component looses focus then the component will unregister the actions that it
 * handles. This works fine in theory but is a little problematic in reality.
 * The most noticeable issue is when a menu item gains the focus it will cause
 * the component that was focused to loose focus and therefore disable the
 * actions it can handle by the time the user selects the menu item.
 */
public class XAction extends AbstractAction {

	private static final long serialVersionUID = -9144908291833751044L;

	public static final int NONE = -1;

	public static final String DEFAULT_ALT_PREFIX = "Alt";

	public static final String DEFAULT_CTRL_PREFIX = "Ctl";

	public static final String DEFAULT_META_PREFIX = "Meta";

	public static final String DEFAULT_SHIFT_PREFIX = "Shift";

	public static final String ACCELERATOR_KEY_DISPLAY = "accelerator.key.display";

	public static final String ACCELERATOR_KEY_SEQUENCE = "accelerator.key.sequence";

	private Deque<XActionHandler> handlers;

	public XAction( String command, String name ) {
		this( command, name, null );
	}

	public XAction( String command, String name, Icon icon ) {
		this( command, name, icon, NONE );
	}

	public XAction( String command, String name, Icon icon, int mnemonic ) {
		this( command, name, icon, mnemonic, null );
	}

	public XAction( String command, String name, Icon icon, int mnemonic, String accelerator ) {
		this( command, name, icon, mnemonic, accelerator, null );
	}

	public XAction( String command, String name, Icon icon, int mnemonic, String accelerator, String display ) {
		super( name, icon );
		enabled = false;
		putValue( Action.ACTION_COMMAND_KEY, command );
		handlers = new LinkedBlockingDeque<XActionHandler>();

		setValues( name, icon, mnemonic, accelerator, display );
	}

	void setValues( String name, Icon icon, int mnemonic, String accelerator, String display ) {
		putValue( Action.NAME, name );
		putValue( Action.SMALL_ICON, icon );
		putValue( Action.SHORT_DESCRIPTION, name );

		int mnemonicKey = -1;
		if( mnemonic > -1 && mnemonic < name.length() ) {
			char mnemonicChar = name.charAt( mnemonic );
			mnemonicKey = getMnemonicKey( mnemonicChar );
		}

		// Set the mnemonic value.
		if( mnemonic != NONE ) {
			putValue( MNEMONIC_KEY, mnemonicKey );
			putValue( DISPLAYED_MNEMONIC_INDEX_KEY, mnemonic );
		}

		// Set the accelerator sequence value.
		if( accelerator != null ) {
			putValue( ACCELERATOR_KEY_SEQUENCE, accelerator );
			putValue( ACCELERATOR_KEY_DISPLAY, getAcceleratorDisplayText( accelerator ) );
		}

		// Set the accelerator display value.
		if( display != null ) {
			putValue( ACCELERATOR_KEY_DISPLAY, display );
		}
	}

	@Override
	public void actionPerformed( ActionEvent event ) {
		performAction( event );
	}

	/**
	 * Called by the <code>ActionAcceleratorWatcher</code>.
	 * 
	 * @return Whether the event was dispatched to any listeners.
	 */
	public boolean performAction( ActionEvent event ) {
		XActionHandler handler = peekHandler();

		if( handler != null && handler.isEnabled() ) {
			handler.actionPerformed( new ActionEvent( event.getSource(), event.getID(), (String)getValue( Action.ACTION_COMMAND_KEY ) ) );
			return true;
		}

		return false;
	}

	/**
	 * Set the enabled flag. This method is safe to call from any thread.
	 */
	@Override
	public void setEnabled( boolean enabled ) {
		Log.write( new Throwable( "Please use action handlers to enable/disable the action." ) );
	}

	/**
	 * Add an <code>XActionHandler</code>.
	 * 
	 * @param handler The XActionHandler that will handle this action.
	 */
	public void pushHandler( XActionHandler handler ) {
		if( handler == null ) throw new IllegalArgumentException( "Null ActionListener not allowed." );
		handlers.remove( handler );
		handlers.push( handler );
		handler.addActionCallback( this );
		new SetEnabled( handler.isEnabled() );
	}

	/**
	 * Get the current <code>XActionHandler</code>.
	 * 
	 * @return The <code>XActionHandler</code> that is handling this action.
	 */
	public XActionHandler peekHandler() {
		return handlers.peek();
	}

	/**
	 * Remove an <code>XActionHandler</code>.
	 * 
	 * @param handler The XActionHandler to remove.
	 */
	public ActionListener pullHandler( XActionHandler handler ) {
		handler.removeActionCallback( this );
		handlers.remove( handler );

		XActionHandler next = peekHandler();
		if( next == null ) {
			new SetEnabled( false );
		} else {
			new SetEnabled( next.isEnabled() );
		}

		return handler;
	}

	/**
	 * Get a virtual key from a specified character. This method is intended to
	 * only be used on characters between 'A'-'Z' and 'a'-'z'.
	 * 
	 * @param mnemonic
	 * @return The virtual key mnemonic for the character.
	 */
	public static final int getMnemonicKey( char mnemonic ) {
		int vk = (int)mnemonic;
		if( vk >= 'a' && vk <= 'z' ) vk -= ( 'a' - 'A' );
		return vk;
	}

	/**
	 * Create a key stroke string for a key event.
	 */
	public static final String encodeKeyEvent( KeyEvent event ) {
		StringBuffer buffer = new StringBuffer();

		if( event.isControlDown() ) buffer.append( "c" );
		if( event.isAltDown() ) buffer.append( "a" );
		if( event.isShiftDown() ) buffer.append( "s" );
		if( event.isMetaDown() ) buffer.append( "m" );

		if( buffer.length() > 0 ) buffer.append( "-" );

		String text = KeyEvent.getKeyText( event.getKeyCode() ).toLowerCase();

		buffer.append( text );

		return buffer.toString();
	}

	/**
	 * Get the displayable text for a accelerator.
	 */
	public static final String getAcceleratorDisplayText( String accelerator ) {
		if( accelerator == null || accelerator == "" ) return "";

		StringBuffer buffer = new StringBuffer();
		StringTokenizer tokenizer = new StringTokenizer( accelerator );

		while( tokenizer.hasMoreTokens() ) {
			String keystroke = tokenizer.nextToken();

			int dashIndex = keystroke.indexOf( "-" );
			if( dashIndex > 0 ) {
				String modifiers = keystroke.substring( 0, dashIndex );
				int length = modifiers.length();
				for( int index = 0; index < length; index++ ) {
					char modifier = modifiers.charAt( index );

					switch( modifier ) {
						case 'a': {
							buffer.append( DEFAULT_ALT_PREFIX );
							break;
						}
						case 'c': {
							buffer.append( DEFAULT_CTRL_PREFIX );
							break;
						}
						case 'm': {
							buffer.append( DEFAULT_META_PREFIX );
							break;
						}
						case 's': {
							buffer.append( DEFAULT_SHIFT_PREFIX );
							break;
						}
					}

					if( index < length - 1 ) buffer.append( "+" );
				}

				buffer.append( "+" );
			}

			buffer.append( keystroke.substring( dashIndex + 1 ).toUpperCase() );
			if( tokenizer.hasMoreTokens() ) buffer.append( " " );
		}

		return buffer.toString();
	}

	void handleEnabledChanged( XActionHandler handler, boolean enabled ) {
		new SetEnabled( peekHandler().isEnabled() );
	}

	/**
	 * The class to set the enabled flag from the event dispatch thread.
	 */
	private class SetEnabled implements Runnable {

		/**
		 * The enabled state.
		 */
		private boolean enabled;

		/**
		 * Create the <code>SetEnabled</code> class.
		 */
		public SetEnabled( boolean enabled ) {
			this.enabled = enabled;

			if( EventQueue.isDispatchThread() ) {
				run();
			} else {
				EventQueue.invokeLater( this );
			}
		}

		/**
		 * Called by the thread.
		 */
		@Override
		public void run() {
			XAction.super.setEnabled( enabled );
		}

	}

}
