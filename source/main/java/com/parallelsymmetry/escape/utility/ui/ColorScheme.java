package com.parallelsymmetry.escape.utility.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.parallelsymmetry.escape.utility.Descriptor;
import com.parallelsymmetry.escape.utility.log.Log;

public class ColorScheme {

	private Descriptor descriptor;

	private boolean flipSecondary;

	public ColorScheme( String uri ) throws SAXException, IOException, ParserConfigurationException {
		this( uri, false );
	}

	public ColorScheme( String uri, boolean flipSecondary ) throws SAXException, IOException, ParserConfigurationException {
		this( new Descriptor( uri ), flipSecondary );
	}

	public ColorScheme( Descriptor descriptor ) throws SAXException, IOException, ParserConfigurationException {
		this( descriptor, false );
	}

	public ColorScheme( Descriptor descriptor, boolean flipSecondary ) throws SAXException, IOException, ParserConfigurationException {
		this.descriptor = descriptor;
		this.flipSecondary = flipSecondary;
	}

	public Color getPrimary( int index ) {
		return Color.decode( "#" + getPrimaryCode( index ) );
	}

	public String getPrimaryCode( int index ) {
		index = fixIndex( index );
		return descriptor.getValue( "/palette/colorset[@id='primary']/color[@id='primary-" + index + "']/@rgb" );
	}

	public Color getSecondaryA( int index ) {
		return Color.decode( "#" + getSecondaryACode( index ) );
	}

	public String getSecondaryACode( int index ) {
		String path = null;
		index = fixIndex( index );
		if( flipSecondary ) {
			path = "/palette/colorset[@id='secondary-b']/color[@id='secondary-b-" + index + "']/@rgb";
		} else {
			path = "/palette/colorset[@id='secondary-a']/color[@id='secondary-a-" + index + "']/@rgb";
		}
		String value = descriptor.getValue( path );
		return value == null ? getPrimaryCode( index ) : value;
	}

	public Color getSecondaryB( int index ) {
		return Color.decode( "#" + getSecondaryBCode( index ) );
	}

	public String getSecondaryBCode( int index ) {
		String path = null;
		index = fixIndex( index );
		if( flipSecondary ) {
			path = "/palette/colorset[@id='secondary-a']/color[@id='secondary-a-" + index + "']/@rgb";
		} else {
			path = "/palette/colorset[@id='secondary-b']/color[@id='secondary-b-" + index + "']/@rgb";
		}
		String value = descriptor.getValue( path );
		return value == null ? getSecondaryACode( index ) : value;
	}

	public Color getComplement( int index ) {
		return Color.decode( "#" + getComplementCode( index ) );
	}

	public String getComplementCode( int index ) {
		index = fixIndex( index );
		String value = descriptor.getValue( "/palette/colorset[@id='complement']/color[@id='complement-" + index + "']/@rgb" );
		return value == null ? getSecondaryACode( index ) : value;
	}

	private int fixIndex( int index ) {
		switch( index ) {
			case -2:
				return 3;
			case -1:
				return 2;
			case 0:
				return 1;

			case 1:
				return 4;
			case 2:
				return 5;
		}
		if( index == 0 ) return 1;
		if( index > 0 ) return index += 3;
		return -index;
	}

	public void showColors() {
		JPanel panel = new JPanel();
		panel.setLayout( new GridLayout( 5, 6, 2, 2 ) );

		panel.add( getCenteredLabel( "Color" ) );
		panel.add( getCenteredLabel( "1" ) );
		panel.add( getCenteredLabel( "2" ) );
		panel.add( getCenteredLabel( "3" ) );
		panel.add( getCenteredLabel( "4" ) );
		panel.add( getCenteredLabel( "5" ) );

		panel.add( new JLabel( "Primary" ) );
		panel.add( getColorField( getPrimary( -2 ) ) );
		panel.add( getColorField( getPrimary( -1 ) ) );
		panel.add( getColorField( getPrimary( 0 ) ) );
		panel.add( getColorField( getPrimary( 1 ) ) );
		panel.add( getColorField( getPrimary( 2 ) ) );

		panel.add( new JLabel( "Secondary A" ) );
		panel.add( getColorField( getSecondaryA( -2 ) ) );
		panel.add( getColorField( getSecondaryA( -1 ) ) );
		panel.add( getColorField( getSecondaryA( 0 ) ) );
		panel.add( getColorField( getSecondaryA( 1 ) ) );
		panel.add( getColorField( getSecondaryA( 2 ) ) );

		panel.add( new JLabel( "Secondary B" ) );
		panel.add( getColorField( getSecondaryB( -2 ) ) );
		panel.add( getColorField( getSecondaryB( -1 ) ) );
		panel.add( getColorField( getSecondaryB( 0 ) ) );
		panel.add( getColorField( getSecondaryB( 1 ) ) );
		panel.add( getColorField( getSecondaryB( 2 ) ) );

		panel.add( new JLabel( "Complement" ) );
		panel.add( getColorField( getComplement( -2 ) ) );
		panel.add( getColorField( getComplement( -1 ) ) );
		panel.add( getColorField( getComplement( 0 ) ) );
		panel.add( getColorField( getComplement( 1 ) ) );
		panel.add( getColorField( getComplement( 2 ) ) );

		JOptionPane.showMessageDialog( null, panel, "Color Scheme", JOptionPane.PLAIN_MESSAGE );
	}
	
	private JLabel getCenteredLabel( String text ) {
		JLabel label = new JLabel( text );
		label.setHorizontalAlignment( JLabel.CENTER );
		return label;
	}
	
	private JTextField getColorField( Color color ) {
		double intensity = ( color.getRed() + color.getGreen() + color.getBlue() ) / ( 3.0 * 255 );
		JTextField field = new JTextField( Colors.encode( color ) );
		field.setForeground( intensity > 0.5 ? Color.BLACK : Color.WHITE );
		field.setBackground( color );
		field.setBorder( null );
		return field;
	}

}
