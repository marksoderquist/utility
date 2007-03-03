package org.novaworx.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Map;

import junit.framework.TestCase;

public class IOPumpTest extends TestCase {

	private String longString = "qpweiofhpqiweufhiqerufgipqegbqeiprgbvipqer ipg qperibqiwepf qipwevqipweriqgpeigqeripgqeipgqipegviqpgpiqegipg";

	private String[] testStrings = new String[] { "test string", "test\nstring", longString };

	@Override
	public void setUp() {
		Log.setLevel( Log.NONE );
	}

	public void testWithInputAndOutputStreams() throws Exception {
		for( String string : testStrings ) {
			testInputToOutput( string );
		}
	}

	public void testWithInputStreamAndWriter() throws Exception {
		for( String string : testStrings ) {
			Map<String, Charset> charsets = Charset.availableCharsets();
			for( Charset charset : charsets.values() ) {
				testInputToWriter( string, charset );
			}
		}
	}

	public void testWithReaderAndOutputStream() throws Exception {
		for( String string : testStrings ) {
			Map<String, Charset> charsets = Charset.availableCharsets();
			for( Charset charset : charsets.values() ) {
				testReaderToOutput( string, charset );
			}
		}
	}

	public void testWithReaderAndWriter() throws Exception {
		for( String string : testStrings ) {
			testReaderToWriter( string );
		}
	}

	private void testInputToOutput( String string ) throws Exception {
		InputStream input = new ByteArrayInputStream( string.getBytes() );
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IOPump pump = new IOPump( input, output );
		pump.startAndWait();
		assertEquals( string, new String( output.toByteArray() ) );
	}

	private void testInputToWriter( String string, Charset charset ) throws Exception {
		if( !charset.canEncode() ) return;

		String comparator = new String( string.getBytes( charset ), charset );
		if( !comparator.equals( string ) ) return;

		InputStream input = new ByteArrayInputStream( string.getBytes( charset ) );
		CharArrayWriter writer = new CharArrayWriter();
		IOPump pump = new IOPump( input, writer, charset );
		if( "UTF-16".equals( charset.name() ) ) {
			Log.setLevel( Log.ALL );
			pump.setLogEnabled( true );
			pump.setLogContent( true );
		}
		pump.startAndWait();
		assertEquals( "charset: " + charset, string, new String( writer.toCharArray() ) );
	}

	private void testReaderToOutput( String string, Charset charset ) throws Exception {
		if( !charset.canEncode() ) return;

		String comparator = new String( string.getBytes( charset ), charset );
		if( !comparator.equals( string ) ) return;

		Reader reader = new CharArrayReader( string.toCharArray() );
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IOPump pump = new IOPump( reader, output, charset );
		if( "UTF-16".equals( charset.name() ) ) {
			Log.setLevel( Log.ALL );
			pump.setLogEnabled( true );
			pump.setLogContent( true );
		}
		pump.startAndWait();
		assertEquals( "charset: " + charset, string, new String( output.toByteArray(), charset ) );
	}

	private void testReaderToWriter( String string ) {
		Reader reader = new CharArrayReader( string.toCharArray() );
		CharArrayWriter writer = new CharArrayWriter();

		IOPump pump = new IOPump( reader, writer );
		pump.startAndWait();
		assertEquals( string, new String( writer.toCharArray() ) );
	}

}
