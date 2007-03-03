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

	private int[] bufferSizes = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 16, 32, 64, IOPump.DEFAULT_BUFFER_SIZE };

	private String longString = "qpweiofhpqiweuf\n\n\n\n\r\r\r\rhiqerufgipq\tegbqeiprgbvipqer ipg qper\u0000ibqiwepf qipwevqipweriqgpeig\u0002qeripgqeipgqipegviqpgpiqegipg";

	private String[] testStrings = new String[] { "", "test string", "test\nstring", longString };

	@Override
	public void setUp() {
		Log.setLevel( Log.NONE );
	}

	public void testWithInputAndOutputStreams() throws Exception {
		for( String string : testStrings ) {
			for( int bufferSize : bufferSizes ) {
				testInputToOutput( string, bufferSize );
			}
		}
	}

	public void testWithInputStreamAndWriter() throws Exception {
		for( String string : testStrings ) {
			Map<String, Charset> charsets = Charset.availableCharsets();
			for( Charset charset : charsets.values() ) {
				for( int bufferSize : bufferSizes ) {
					testInputToWriter( string, charset, bufferSize );
				}
			}
		}
	}

	public void testWithReaderAndOutputStream() throws Exception {
		for( String string : testStrings ) {
			Map<String, Charset> charsets = Charset.availableCharsets();
			for( Charset charset : charsets.values() ) {
				for( int bufferSize : bufferSizes ) {
					testReaderToOutput( string, charset, bufferSize );
				}
			}
		}
	}

	public void testWithReaderAndWriter() throws Exception {
		for( String string : testStrings ) {
			for( int bufferSize : bufferSizes ) {
				testReaderToWriter( string, bufferSize );
			}
		}
	}

	private void testInputToOutput( String string, int bufferSize ) throws Exception {
		InputStream input = new ByteArrayInputStream( string.getBytes() );
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IOPump pump = new IOPump( input, output );
		pump.startAndWait();
		assertEquals( string, new String( output.toByteArray() ) );
	}

	private void testInputToWriter( String string, Charset charset, int bufferSize ) throws Exception {
		if( !charset.canEncode() ) return;

		String comparator = new String( string.getBytes( charset ), charset );
		if( !comparator.equals( string ) ) return;

		InputStream input = new ByteArrayInputStream( string.getBytes( charset ) );
		CharArrayWriter writer = new CharArrayWriter();
		IOPump pump = new IOPump( input, writer, charset );
		pump.startAndWait();
		assertEquals( "charset: " + charset, string, new String( writer.toCharArray() ) );
	}

	private void testReaderToOutput( String string, Charset charset, int bufferSize ) throws Exception {
		if( !charset.canEncode() ) return;

		String comparator = new String( string.getBytes( charset ), charset );
		if( !comparator.equals( string ) ) return;

		Reader reader = new CharArrayReader( string.toCharArray() );
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IOPump pump = new IOPump( reader, output, charset );
		pump.startAndWait();
		assertEquals( "charset: " + charset, string, new String( output.toByteArray(), charset ) );
	}

	private void testReaderToWriter( String string, int bufferSize ) {
		Reader reader = new CharArrayReader( string.toCharArray() );
		CharArrayWriter writer = new CharArrayWriter();

		IOPump pump = new IOPump( reader, writer );
		pump.startAndWait();
		assertEquals( string, new String( writer.toCharArray() ) );
	}

}
