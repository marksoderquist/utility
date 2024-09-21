package com.parallelsymmetry.utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilTest extends BaseTestCase {

	private static final String PREFIX = "test";

	private static final FilenameFilter TEST_FILE_FILTER = new TestFilenameFilter();

	@AfterEach
	@Override
	public void teardown() throws Exception {
		File tmp = new File( System.getProperty( "java.io.tmpdir" ) );
		for( File file : Objects.requireNonNull( tmp.listFiles( TEST_FILE_FILTER ) ) ) {
			FileUtil.delete( file );
		}
		super.teardown();
	}

	@Test
	public void testConstants() throws Exception {
		assertEquals( 1000L, FileUtil.KB );
		assertEquals( 1000000L, FileUtil.MB );
		assertEquals( 1000000000L, FileUtil.GB );
		assertEquals( 1000000000000L, FileUtil.TB );
		assertEquals( 1000000000000000L, FileUtil.PB );
		assertEquals( 1000000000000000000L, FileUtil.EB );

		assertEquals( 1024L, FileUtil.KiB );
		assertEquals( 1048576L, FileUtil.MiB );
		assertEquals( 1073741824L, FileUtil.GiB );
		assertEquals( 1099511627776L, FileUtil.TiB );
		assertEquals( 1125899906842624L, FileUtil.PiB );
		assertEquals( 1152921504606846976L, FileUtil.EiB );
	}

	@Test
	public void testGlobToRE() {
		assertEquals( null, FileUtil.globToRE( null ) );
		assertEquals( "", FileUtil.globToRE( "" ) );
		assertEquals( ".*\\.txt", FileUtil.globToRE( "*.txt" ) );
		assertEquals( "test\\.txt", FileUtil.globToRE( "test.txt" ) );
		assertEquals( ".*", FileUtil.globToRE( "*" ) );
		assertEquals( ".*\\..*", FileUtil.globToRE( "*.*" ) );

		Pattern.compile( FileUtil.globToRE( "*" ) );
		Pattern.compile( FileUtil.globToRE( "*.*" ) );
	}

	@Test
	public void testGetExtensionWithFile() {
		assertEquals(  null, FileUtil.getExtension( (File)null ) );
		assertEquals(  "", FileUtil.getExtension( new File( "test" ) ) );
		assertEquals(  "txt", FileUtil.getExtension( new File( "test.txt" ) ) );
	}

	@Test
	public void testGetExtensionWithName() {
		assertEquals(  null, FileUtil.getExtension( (String)null ) );
		assertEquals(  "", FileUtil.getExtension( "test" ) );
		assertEquals(  "txt", FileUtil.getExtension( "test.txt" ) );
	}

	@Test
	public void testGetHumanSize() {
		assertEquals( "0B", FileUtil.getHumanSize( 0 ) );
		assertEquals( "1B", FileUtil.getHumanSize( 1 ) );
		assertEquals( "12B", FileUtil.getHumanSize( 12 ) );
		assertEquals( "123B", FileUtil.getHumanSize( 123 ) );
		assertEquals( "1.2KB", FileUtil.getHumanSize( 1234 ) );
		assertEquals( "12KB", FileUtil.getHumanSize( 12345 ) );
		assertEquals( "123KB", FileUtil.getHumanSize( 123456 ) );
		assertEquals( "1.2MB", FileUtil.getHumanSize( 1234567 ) );
		assertEquals( "12MB", FileUtil.getHumanSize( 12345678 ) );
		assertEquals( "123MB", FileUtil.getHumanSize( 123456789 ) );

		assertEquals( "999B", FileUtil.getHumanSize( FileUtil.KB - 1 ) );
		assertEquals( "1.0KB", FileUtil.getHumanSize( FileUtil.KB ) );

		assertEquals( "999KB", FileUtil.getHumanSize( FileUtil.MB - 1 ) );
		assertEquals( "1.0MB", FileUtil.getHumanSize( FileUtil.MB ) );

		assertEquals( "999MB", FileUtil.getHumanSize( FileUtil.GB - 1 ) );
		assertEquals( "1.0GB", FileUtil.getHumanSize( FileUtil.GB ) );

		assertEquals( "999GB", FileUtil.getHumanSize( FileUtil.TB - 1 ) );
		assertEquals( "1.0TB", FileUtil.getHumanSize( FileUtil.TB ) );

		assertEquals( "999TB", FileUtil.getHumanSize( FileUtil.PB - 1 ) );
		assertEquals( "1.0PB", FileUtil.getHumanSize( FileUtil.PB ) );

		assertEquals( "999PB", FileUtil.getHumanSize( FileUtil.EB - 1 ) );
		assertEquals( "1.0EB", FileUtil.getHumanSize( FileUtil.EB ) );
	}

	@Test
	public void testGetHumanBinSize() {
		assertEquals( "0B", FileUtil.getHumanBinSize( 0 ) );
		assertEquals( "1B", FileUtil.getHumanBinSize( 1 ) );
		assertEquals( "12B", FileUtil.getHumanBinSize( 12 ) );
		assertEquals( "123B", FileUtil.getHumanBinSize( 123 ) );
		assertEquals( "1.2KiB", FileUtil.getHumanBinSize( 1234 ) );
		assertEquals( "12KiB", FileUtil.getHumanBinSize( 12345 ) );
		assertEquals( "120KiB", FileUtil.getHumanBinSize( 123456 ) );
		assertEquals( "1.2MiB", FileUtil.getHumanBinSize( 1234567 ) );
		assertEquals( "11MiB", FileUtil.getHumanBinSize( 12345678 ) );
		assertEquals( "117MiB", FileUtil.getHumanBinSize( 123456789 ) );

		assertEquals( "1023B", FileUtil.getHumanBinSize( FileUtil.KiB - 1 ) );
		assertEquals( "1.0KiB", FileUtil.getHumanBinSize( FileUtil.KiB ) );

		assertEquals( "1023KiB", FileUtil.getHumanBinSize( FileUtil.MiB - 1 ) );
		assertEquals( "1.0MiB", FileUtil.getHumanBinSize( FileUtil.MiB ) );

		assertEquals( "1023MiB", FileUtil.getHumanBinSize( FileUtil.GiB - 1 ) );
		assertEquals( "1.0GiB", FileUtil.getHumanBinSize( FileUtil.GiB ) );

		assertEquals( "1023GiB", FileUtil.getHumanBinSize( FileUtil.TiB - 1 ) );
		assertEquals( "1.0TiB", FileUtil.getHumanBinSize( FileUtil.TiB ) );

		assertEquals( "1023TiB", FileUtil.getHumanBinSize( FileUtil.PiB - 1 ) );
		assertEquals( "1.0PiB", FileUtil.getHumanBinSize( FileUtil.PiB ) );

		assertEquals( "1023PiB", FileUtil.getHumanBinSize( FileUtil.EiB - 1 ) );
		assertEquals( "1.0EiB", FileUtil.getHumanBinSize( FileUtil.EiB ) );
	}

	@Test
	public void testRemoveExtensionWithFile() {
		assertEquals( null, FileUtil.removeExtension( (File)null ) );
		assertEquals( new File( "test" ), FileUtil.removeExtension( new File( "test" ) ) );
		assertEquals( new File( "test" ), FileUtil.removeExtension( new File( "test.txt" ) ) );
	}

	@Test
	public void testRemoveExtensionWithName() {
		assertEquals(  null, FileUtil.removeExtension( (String)null ) );
		assertEquals(  "test", FileUtil.removeExtension( "test" ) );
		assertEquals(  "test", FileUtil.removeExtension( "test.txt" ) );
	}

	@Test
	public void testCreateTempFolder() throws Exception {
		File folder = FileUtil.createTempFolder( PREFIX, "createTempFolder" );
		assertTrue( folder.exists() );
		String name = folder.getName();
		File check = new File( System.getProperty( "java.io.tmpdir" ), name );
		assertEquals( check, folder );
		assertTrue( check.exists() );
		folder.delete();
	}

	@Test
	public void testSaveAndLoad() throws Exception {
		File file = File.createTempFile( PREFIX, "Test" );
		FileUtil.save( file.toString(), file );
		assertEquals( file.toString(), FileUtil.load( file ) );
	}

	@Test
	public void testSaveAndLoadAsLines() throws Exception {
		String content = "A\nB\nC";
		File file = File.createTempFile( PREFIX, "Test" );
		FileUtil.save( content, file );

		List<String> lines = FileUtil.loadAsLines( file );
		assertEquals( 3, lines.size() );
		assertEquals( "A", lines.get( 0 ) );
		assertEquals( "B", lines.get( 1 ) );
		assertEquals( "C", lines.get( 2 ) );
	}

	@Test
	public void testZipAndUnzip() throws Exception {
		File sourceData = new File( "source/test/java" );
		File zip = new File( "target/test.source.zip" );
		File targetData = new File( "target/test/data" );

		// Make a list of relativized paths.
		URI base = sourceData.toURI();
		List<File> sourceFiles = FileUtil.listFiles( sourceData );
		List<String> paths = new ArrayList<String>( sourceFiles.size() );
		for( File file : sourceFiles ) {
			paths.add( base.relativize( file.toURI() ).toString() );
		}

		// Initialize for zip tests.
		zip.delete();
		assertFalse( zip.exists() );

		// Zip the data.
		FileUtil.zip( sourceData, zip );
		assertTrue( zip.exists() );

		// Check that all paths are in the zip file.
		ZipFile zipFile = new ZipFile( zip );
		try {
			for( Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
				String zipEntryName = entries.nextElement().getName();
				assertTrue( paths.contains( zipEntryName ) );
			}

			// Initialize for unzip tests.
			assertTrue( FileUtil.delete( targetData ) );
			assertFalse( targetData.exists() );
			targetData.mkdirs();
			assertTrue( targetData.exists() );

			// Unzip the data.
			FileUtil.unzip( zip, targetData );

			// Check that all the files are in the target.
			for( Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
				assertTrue( new File( targetData, entries.nextElement().getName() ).exists() );
			}
		} finally {
			if( zipFile != null ) zipFile.close();
		}

		targetData.deleteOnExit();
	}

	@Test
	public void testCopyWithNonExistantFiles() throws Exception {
		assertFalse( FileUtil.copy( new File( "" ), new File( "" ) ) );
	}

	@Test
	public void testCopyFileToFile() throws Exception {
		long time = System.currentTimeMillis();
		File source = File.createTempFile( PREFIX, "copyFileToFileSource" );
		File target = File.createTempFile( PREFIX, "copyFileToFileTarget" );
		FileOutputStream fileOutput = new FileOutputStream( source );
		DataOutputStream output = new DataOutputStream( fileOutput );
		output.writeLong( time );
		output.close();

		assertTrue( FileUtil.copy( source, target ) );

		FileInputStream fileInput = new FileInputStream( target );
		DataInputStream input = new DataInputStream( fileInput );
		assertEquals( time, input.readLong() );
		input.close();

		source.deleteOnExit();
		target.deleteOnExit();
	}

	@Test
	public void testCopyFileToNewFile() throws Exception {
		long time = System.currentTimeMillis();
		File source = File.createTempFile( PREFIX, "copyFileToFileSource" );
		File temp = File.createTempFile( PREFIX, "copyFileToFileTarget" );
		File target = new File( temp.getParentFile(), "copyFileToNewFileTarget" );
		FileOutputStream fileOutput = new FileOutputStream( source );
		DataOutputStream output = new DataOutputStream( fileOutput );
		output.writeLong( time );
		output.close();

		assertTrue( FileUtil.copy( source, target ) );

		FileInputStream fileInput = new FileInputStream( target );
		DataInputStream input = new DataInputStream( fileInput );
		assertEquals( time, input.readLong() );
		input.close();

		source.deleteOnExit();
		temp.deleteOnExit();
		target.deleteOnExit();
	}

	@Test
	public void testCopyFileToFolder() throws Exception {
		long time = System.currentTimeMillis();
		File source = File.createTempFile( PREFIX, "copyFileToFolderSource" );
		File target = FileUtil.createTempFolder( PREFIX, "copyFileToFolderTarget" );
		FileOutputStream fileOutput = new FileOutputStream( source );
		DataOutputStream output = new DataOutputStream( fileOutput );
		output.writeLong( time );
		output.close();

		assertTrue( FileUtil.copy( source, target ) );

		File child = new File( target, source.getName() );
		FileInputStream fileInput = new FileInputStream( child );
		DataInputStream input = new DataInputStream( fileInput );
		assertEquals( time, input.readLong() );
		input.close();

		source.deleteOnExit();
		target.deleteOnExit();
	}

	@Test
	public void testCopyFolderToFile() throws Exception {
		File source = FileUtil.createTempFolder( PREFIX, "copyFolderToFileSource" );
		File target = File.createTempFile( PREFIX, "copyFolderToFileTarget" );
		assertFalse( FileUtil.copy( source, target ) );
		assertTrue( source.exists() );
		assertTrue( target.exists() );

		source.deleteOnExit();
		target.deleteOnExit();
	}

	@Test
	public void testCopyFolderToFolder() throws Exception {
		File parent0 = FileUtil.createTempFolder( PREFIX, "copyFolderToFolderParent0" );
		File parent1 = FileUtil.createTempFolder( PREFIX, "copyFolderToFolderParent1", parent0 );
		File leaf0 = File.createTempFile( PREFIX, "copyFolderToFolderLeaf0", parent0 );
		File leaf1 = File.createTempFile( PREFIX, "copyFolderToFolderLeaf1", parent0 );
		File leaf2 = File.createTempFile( PREFIX, "copyFolderToFolderLeaf2", parent1 );
		File leaf3 = File.createTempFile( PREFIX, "copyFolderToFolderLeaf3", parent1 );
		assertEquals( 3, parent0.listFiles().length );
		assertEquals( 2, parent1.listFiles().length );

		File target = FileUtil.createTempFolder( PREFIX, "copyFolderToFolderTarget" );

		assertTrue( FileUtil.copy( parent0, target, false ) );

		File target1 = new File( target, parent1.getName() );
		assertEquals( 3, target.listFiles().length );
		assertEquals( 2, target1.listFiles().length );
		assertTrue( new File( target, leaf0.getName() ).exists() );
		assertTrue( new File( target, leaf1.getName() ).exists() );
		assertTrue( new File( target1, leaf2.getName() ).exists() );
		assertTrue( new File( target1, leaf3.getName() ).exists() );

		parent0.deleteOnExit();
		parent1.deleteOnExit();
	}

	@Test
	public void testCopyFolderToFolderWithSourceFolder() throws Exception {
		File parent0 = FileUtil.createTempFolder( PREFIX, "copyFolderToFolderParent0" );
		File parent1 = FileUtil.createTempFolder( PREFIX, "copyFolderToFolderParent1", parent0 );
		File leaf0 = File.createTempFile( PREFIX, "copyFolderToFolderLeaf0", parent0 );
		File leaf1 = File.createTempFile( PREFIX, "copyFolderToFolderLeaf1", parent0 );
		File leaf2 = File.createTempFile( PREFIX, "copyFolderToFolderLeaf2", parent1 );
		File leaf3 = File.createTempFile( PREFIX, "copyFolderToFolderLeaf3", parent1 );
		assertEquals( 3, parent0.listFiles().length );
		assertEquals( 2, parent1.listFiles().length );

		File target = FileUtil.createTempFolder( PREFIX, "copyFolderToFolderTarget" );

		assertTrue( FileUtil.copy( parent0, target, true ) );

		File target0 = new File( target, parent0.getName() );
		File target1 = new File( target0, parent1.getName() );
		assertEquals( 3, target0.listFiles().length );
		assertEquals( 2, target1.listFiles().length );
		assertTrue( new File( target0, leaf0.getName() ).exists() );
		assertTrue( new File( target0, leaf1.getName() ).exists() );
		assertTrue( new File( target1, leaf2.getName() ).exists() );
		assertTrue( new File( target1, leaf3.getName() ).exists() );

		parent0.deleteOnExit();
		parent1.deleteOnExit();
	}

	@Test
	public void testCopyFileToOutputStream() throws Exception {
		long time = System.currentTimeMillis();
		File source = File.createTempFile( PREFIX, "copyFileToFileSource" );
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		FileOutputStream fileOutput = new FileOutputStream( source );
		DataOutputStream output = new DataOutputStream( fileOutput );
		output.writeLong( time );
		output.close();

		long count = FileUtil.copy( source, target );
		assertEquals( 8, count );

		DataInputStream input = new DataInputStream( new ByteArrayInputStream( target.toByteArray() ) );
		assertEquals( time, input.readLong() );
		input.close();

		source.deleteOnExit();
	}

	@Test
	public void testDeleteTree() throws Exception {
		assertTrue( FileUtil.delete( new File( "" ) ) );
		File file = File.createTempFile( PREFIX, "deleteTree" );
		File temp = file.getParentFile();
		File parent = new File( temp, "parent" );
		parent.mkdirs();
		File child = new File( parent, "child" );

		child.createNewFile();
		assertTrue( parent.exists() );
		assertTrue( child.exists() );

		FileUtil.delete( parent );
		assertFalse( parent.exists() );
		assertFalse( child.exists() );

		assertTrue( file.delete() );
	}

	@Test
	public void testDeleteTreeOnExit() throws Exception {
		FileUtil.deleteOnExit( new File( "" ) );

		File parent0 = FileUtil.createTempFolder( PREFIX, "copyFolderToFolderParent0" );
		File parent1 = FileUtil.createTempFolder( PREFIX, "copyFolderToFolderParent1", parent0 );
		File.createTempFile( PREFIX, "copyFolderToFolderLeaf0", parent0 );
		File.createTempFile( PREFIX, "copyFolderToFolderLeaf1", parent0 );
		File.createTempFile( PREFIX, "copyFolderToFolderLeaf2", parent1 );
		File.createTempFile( PREFIX, "copyFolderToFolderLeaf3", parent1 );
		assertEquals( 3, parent0.listFiles().length );
		assertEquals( 2, parent1.listFiles().length );

		FileUtil.deleteOnExit( parent0 );
	}

	@Test
	public void testIsWritable() throws Exception {
		File folder = FileUtil.createTempFolder( "FileUtil", "test" );
		assertNotNull( folder );
		assertTrue( FileUtil.isWritable( folder ) );
	}

	private static final class TestFilenameFilter implements FilenameFilter {

		@Override
		public boolean accept( File dir, String name ) {
			return name.startsWith( PREFIX );
		}

	}

}
