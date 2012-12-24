package com.parallelsymmetry.utility.ui;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.Icon;

import junit.framework.TestCase;

import com.parallelsymmetry.utility.Accessor;
import com.parallelsymmetry.utility.HashUtil;
import com.parallelsymmetry.utility.JavaUtil;

public class IconLibraryTest extends TestCase {

	private File cachePath = new File( "target/iconcache" );

	private IconLibrary library;

	@Override
	public void setUp() {
		library = new IconLibrary();
		library.setCachePath( cachePath );
		assertTrue( cachePath.exists() );
	}

	public void testConstructor() {
		assertNotNull( new IconLibrary() );
	}

	public void testGetIconUrl() throws Exception {
		IconLibrary library = new IconLibrary();
		library.addSearchPath( JavaUtil.getPackagePath( getClass() ), getClass().getClassLoader() );

		assertNull( "Null icon should not be found and was.", Accessor.callMethod( library, "getIconUrl", "null" ) );
		assertNotNull( "Test icon should be found and was not.", Accessor.callMethod( library, "getIconUrl", "test" ) );
	}

	public void testGetIconWithNull() {
		//assertEquals( "broken", library.getIcon( (URI)null ).toString() );
		assertNull( library.getIcon( (URI)null ) );
	}

	public void testGetIcon() throws Exception {
		URI uri = URI.create( "http://www.parallelsymmetry.com/images/parasymm.png" );
		
		// Get a direct version of the image to compare against.
		Image source = ImageIO.read( uri.toURL() ).getScaledInstance( 16, 16, Image.SCALE_SMOOTH );

		// Remove the existing file.
		File file = new File( cachePath, HashUtil.hash( uri.toString() ) );
		if( file.exists() ) file.delete();
		assertFalse( file.exists() );

		// Get the icon from the icon library.
		Icon icon = library.getIcon( uri );
		assertNotNull( icon );

		// Create an image from the icon to use for comparison.
		Image target = Icons.getImage( icon );
		ImageIO.write( (RenderedImage)target, "png", new File( cachePath, "target.png" ) );

		// Due to various factors the images will not be identical be should be similar.
		assertTrue( Images.areSimilar( source, target ) );
		assertTrue( file.exists() );
	}

	public void testGetBrokenIcon() throws Exception {
		Icon icon = library.getIcon( IconLibrary.BROKEN );
		assertNotNull( icon );

		Image target = Icons.getImage( icon );

		assertEquals( "964e85e8361583ce5b2f6bf2b334a479085b38d6", HashUtil.hash( Images.getArrayFromImage( target ) ) );
	}

}
