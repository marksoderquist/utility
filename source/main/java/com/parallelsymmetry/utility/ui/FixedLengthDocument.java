package com.parallelsymmetry.utility.ui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class FixedLengthDocument extends PlainDocument {

	private static final long serialVersionUID = -1166161428091904568L;

	private int length;

	public FixedLengthDocument( int length ) {
		this.length = length;
	}
	
	public int getMaxLength() {
		return length;
	}
	
	public void setMaxLength( int length ) {
		this.length = length;
	}

	public void insertString( int offset, String string, AttributeSet attributes ) throws BadLocationException {
		if( string != null && string.length() + getLength() <= length ) {
			super.insertString( offset, string, attributes );
		}
	}

}
