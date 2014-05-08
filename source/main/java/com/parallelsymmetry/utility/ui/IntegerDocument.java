package com.parallelsymmetry.utility.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class IntegerDocument extends PlainDocument {

	private static final long serialVersionUID = -7820524076328760610L;

	private int minimum = Integer.MIN_VALUE;

	private int maximum = Integer.MAX_VALUE;

	public IntegerDocument() {}

	public IntegerDocument( int minimum, int maximum ) {
		this.minimum = minimum;
		this.maximum = maximum;
	}

	public int getMinimum() {
		return minimum;
	}

	public void setMinimum( int minimum ) {
		this.minimum = minimum;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum( int maximum ) {
		this.maximum = maximum;
	}

	@Override
	public void insertString( int offset, String string, AttributeSet attributes ) throws BadLocationException {
		String before = getText( 0, offset );
		String after = getText( offset, getLength() - offset );
		String newValue = before + string + after;
		try {
			int value = Integer.parseInt( newValue.trim() );
			if( value >= minimum && value <= maximum ) super.insertString( offset, string, attributes );
		} catch( NumberFormatException exception ) {
			// Intentionally ignore exception.
		}
	}

}
