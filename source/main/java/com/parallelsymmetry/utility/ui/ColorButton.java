package com.parallelsymmetry.utility.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ColorButton extends JButton {

	public static final String COLOR = "color";

	public static final String TITLE = "title";

	private static final long serialVersionUID = -6399118339246604209L;

	private Color color;

	private String title;

	private String text;

	private ActionListener defaultActionListener = new DefaultActionHandler();

	public ColorButton() {
		this( Color.WHITE, null );
	}

	public ColorButton( Color color ) {
		this( color, null );
	}

	public ColorButton( String sampleText ) {
		this( Color.BLACK, sampleText );
	}

	/**
	 * Create a color button with the specific color and sample text. If the
	 * sample text is null then a color swatch is drawn. If the sample text is not
	 * null then the sample text is draw in with the specified color.
	 * 
	 * @param color The initial color value for the button
	 * @param text The sample text to show on the button
	 */
	public ColorButton( Color color, String text ) {
		super( " " );
		this.color = color;
		this.text = text;
		addActionListener( defaultActionListener );
	}

	public String getSampleText() {
		return text;
	}

	public void setSampleText( String text ) {
		this.text = text;
		repaint();
	}

	public Color getColor() {
		return color;
	}

	public void setColor( Color color ) {
		Color oldColor = this.color;
		this.color = color;
		firePropertyChange( COLOR, oldColor, color );
		repaint();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle( String title ) {
		String oldTitle = this.title;
		this.title = title;
		firePropertyChange( TITLE, oldTitle, title );
	}

	@Override
	public void addActionListener( ActionListener listener ) {
		if( listener == null ) return;
		super.removeActionListener( defaultActionListener );
		super.addActionListener( listener );
	}

	public void removeActionListener( ActionListener listener ) {
		if( listener == null ) return;
		super.removeActionListener( listener );
		if( getActionListeners().length == 0 ) super.addActionListener( defaultActionListener );
	}

	@Override
	public void paintComponent( Graphics graphics ) {
		super.paintComponent( graphics );

		Rectangle bounds = getBounds();
		SwingUtilities.calculateInnerArea( this, bounds );

		Graphics2D gfx = (Graphics2D)graphics.create();
		gfx.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		gfx.setColor( getColor() );
		if( text == null ) {
			gfx.fillRoundRect( bounds.x, bounds.y, bounds.width, bounds.height, 4, 4 );
		} else {
			Rectangle2D textBounds = getFont().createGlyphVector( gfx.getFontRenderContext(), text ).getVisualBounds();
			float x = (float)( 0.5f * ( bounds.width - textBounds.getWidth() ) );
			float y = (float)( 0.5f * ( bounds.height + textBounds.getHeight() ) );
			gfx.drawString( text, bounds.x + x, bounds.y + y );
		}
		gfx.dispose();
	}

	private static class DefaultActionHandler implements ActionListener {

		@Override
		public void actionPerformed( ActionEvent event ) {
			ColorButton button = (ColorButton)event.getSource();
			JColorChooser chooser = new JColorChooser( button.getColor() );
			JDesktopPane desktop = JOptionPane.getDesktopPaneForComponent( button );
			int result = JOptionPane.showInternalOptionDialog( desktop, chooser, button.getTitle(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null );
			if( result == JOptionPane.OK_OPTION ) button.setColor( chooser.getColor() );
		}

	}

}
