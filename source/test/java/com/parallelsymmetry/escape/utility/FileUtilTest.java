package com.parallelsymmetry.escape.utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import junit.framework.TestCase;

public class FileUtilTest extends TestCase {

	private static final String PREFIX = "test";

	private static final FilenameFilter TEST_FILE_FILTER = new TestFilenameFilter();

	@Override
	public void tearDown() throws Exception {
		File tmp = new File( System.getProperty( "java.io.tmpdir" ) );
		for( File file : tmp.listFiles( TEST_FILE_FILTER ) ) {
			FileUtil.deleteTree( file );
		}
	}

	public void testConstants() throws Exception {
		assertEquals( 1024L, FileUtil.KILOBYTE );
		assertEquals( 1048576L, FileUtil.MEGABYTE );
		assertEquals( 1073741824L, FileUtil.GIGABYTE );
		assertEquals( 1099511627776L, FileUtil.TERABYTE );
		assertEquals( 1125899906842624L, FileUtil.PETABYTE );
	}

	public void testGetExtensionWithFile() throws Exception {
		assertEquals( "Incorrect extension.", null, FileUtil.getExtension( (File)null ) );
		assertEquals( "Incorrect extension.", "", FileUtil.getExtension( new File( "test" ) ) );
		assertEquals( "Incorrect extension.", "txt", FileUtil.getExtension( new File( "test.txt" ) ) );
	}

	public void testGetExtensionWithName() throws Exception {
		assertEquals( "Incorrect extension.", null, FileUtil.getExtension( (String)null ) );
		assertEquals( "Incorrect extension.", "", FileUtil.getExtension( "test" ) );
		assertEquals( "Incorrect extension.", "txt", FileUtil.getExtension( "test.txt" ) );
	}

	public void testCreateTempFolder() throws Exception {
		File folder = FileUtil.createTempFolder( PREFIX, "createTempFolder" );
		assertTrue( folder.exists() );
		String name = folder.getName();
		File check = new File( System.getProperty( "java.io.tmpdir" ), name );
		assertEquals( check, folder );
		assertTrue( check.exists() );
		folder.delete();
	}

	public void testSaveAndLoad() throws Exception {
		File file = File.createTempFile( "FileUtil", "Test" );
		FileUtil.save( file.toString(), file );
		assertEquals( file.toString(), FileUtil.load( file ) );
	}

	public void testCopyWithNonExistantFiles() throws Exception {
		assertFalse( FileUtil.copy( new File( "" ), new File( "" ) ) );
	}

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
	}

	public void testCopyFolderToFile() throws Exception {
		File source = FileUtil.createTempFolder( PREFIX, "copyFolderToFileSource" );
		File target = File.createTempFile( PREFIX, "copyFolderToFileTarget" );
		assertFalse( FileUtil.copy( source, target ) );
		assertTrue( source.delete() );
		assertTrue( target.delete() );
	}

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
	}

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
	}

	public void testDeleteTree() throws Exception {
		assertTrue( FileUtil.deleteTree( new File( "" ) ) );
		File file = File.createTempFile( PREFIX, "deleteTree" );
		File temp = file.getParentFile();
		File parent = new File( temp, "parent" );
		parent.mkdirs();
		File child = new File( parent, "child" );

		child.createNewFile();
		assertTrue( parent.exists() );
		assertTrue( child.exists() );

		FileUtil.deleteTree( parent );
		assertFalse( parent.exists() );
		assertFalse( child.exists() );

		assertTrue( file.delete() );
	}

	public void testDeleteTreeOnExit() throws Exception {
		FileUtil.deleteTreeOnExit( new File( "" ) );

		File parent0 = FileUtil.createTempFolder( PREFIX, "copyFolderToFolderParent0" );
		File parent1 = FileUtil.createTempFolder( PREFIX, "copyFolderToFolderParent1", parent0 );
		File.createTempFile( PREFIX, "copyFolderToFolderLeaf0", parent0 );
		File.createTempFile( PREFIX, "copyFolderToFolderLeaf1", parent0 );
		File.createTempFile( PREFIX, "copyFolderToFolderLeaf2", parent1 );
		File.createTempFile( PREFIX, "copyFolderToFolderLeaf3", parent1 );
		assertEquals( 3, parent0.listFiles().length );
		assertEquals( 2, parent1.listFiles().length );

		FileUtil.deleteTreeOnExit( parent0 );
	}

	private static final class TestFilenameFilter implements FilenameFilter {

		@Override
		public boolean accept( File dir, String name ) {
			return name.startsWith( PREFIX );
		}

	}

}
