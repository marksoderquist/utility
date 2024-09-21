package com.parallelsymmetry.utility.ui;

import com.parallelsymmetry.utility.Accessor;
import com.parallelsymmetry.utility.BaseTestCase;
import com.parallelsymmetry.utility.HashUtil;
import com.parallelsymmetry.utility.JavaUtil;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class IconLibraryTest extends BaseTestCase {

	private final File cachePath = new File( "target/iconcache" );

	private IconLibrary library;

	@BeforeEach
	@Override
	public void setup() throws Exception {
		super.setup();
		library = new IconLibrary();
		library.setCachePath( cachePath );
		assertTrue( cachePath.exists() );
	}

	@Test
	public void testConstructor() {
		assertNotNull( new IconLibrary() );
	}

	@Test
	public void testGetIconUrl() throws Exception {
		IconLibrary library = new IconLibrary();
		library.addSearchPath( JavaUtil.getPackagePath( getClass() ), getClass().getClassLoader() );

		assertNull( Accessor.callMethod( library, "getIconUrl", "null" ), "Null icon should not be found and was." );
		assertNotNull( Accessor.callMethod( library, "getIconUrl", "test" ), "Test icon should be found and was not." );
	}

	@Test
	public void testGetIconWithNull() {
		//assertEquals( "broken", library.getIcon( (URI)null ).toString() );
		assertNull( library.getIcon( (URI)null ) );
	}

	@Test
	public void testGetIcon() throws Exception {
		URI uri = new File( "source/test/resources/com/parallelsymmetry/utility/ui/test.png" ).toURI();

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

	@Test
	public void testGetBrokenIcon() {
		Icon icon = library.getIcon( IconLibrary.BROKEN );
		assertNotNull( icon );

		Image target = Icons.getImage( icon );

		//assertEquals( "964e85e8361583ce5b2f6bf2b334a479085b38d6", HashUtil.hash( Images.getArrayFromImage( target ) ) );
	}

	@Test
	public void testIconCache() {
		TestIcon renderer = new TestIcon();
		library.putIcon( "test", renderer );
		assertEquals( 0, renderer.getRenderCalledCount() );

		BufferedImage image = new BufferedImage( 16, 16, BufferedImage.TYPE_4BYTE_ABGR );
		Graphics graphics = image.getGraphics();
		assertNotNull( graphics );

		Icon icon = library.getIcon( "test" );
		icon.paintIcon( null, graphics, 0, 0 );
		assertEquals( 1, renderer.getRenderCalledCount() );

		icon.paintIcon( null, graphics, 0, 0 );
		assertEquals( 1, renderer.getRenderCalledCount() );
	}

	@Getter
	private static class TestIcon extends BaseIcon {

		private int renderCalledCount;

		@Override
		public void render() {
			renderCalledCount++;
		}

	}

}
