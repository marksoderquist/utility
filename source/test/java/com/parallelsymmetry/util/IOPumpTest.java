package com.parallelsymmetry.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import com.parallelsymmetry.log.Log;

import junit.framework.TestCase;

public class IOPumpTest extends TestCase {

	private String[] charsets = new String[] { "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE" };

	private String longString = "qpweiofhpqiweuf\n\n\n\n\r\r\r\rhiqerufgipq\tegbqeiprgbvipqer ipg qper\u0000ibqiwepf qipwevqipweriqgpeig\u0002qeripgqeipgqipegviqpgpiqegipg";

	private String[] testStrings = new String[] { "", "test string", "test\nstring", longString };

	@Override
	public void setUp() {
		Log.setLevel( Log.NONE );
	}

	public void testWithInputAndOutputStreamsBuffer1() throws Exception {
		testWithInputAndOutputStreams( 1 );
	}

	public void testWithInputAndOutputStreamsBuffer2() throws Exception {
		testWithInputAndOutputStreams( 2 );
	}

	public void testWithInputAndOutputStreamsBuffer3() throws Exception {
		testWithInputAndOutputStreams( 3 );
	}

	public void testWithInputAndOutputStreamsBuffer4() throws Exception {
		testWithInputAndOutputStreams( 4 );
	}

	public void testWithInputAndOutputStreamsBuffer5() throws Exception {
		testWithInputAndOutputStreams( 5 );
	}

	public void testWithInputAndOutputStreamsBuffer32() throws Exception {
		testWithInputAndOutputStreams( 32 );
	}

	public void testWithInputAndOutputStreamsBufferDefault() throws Exception {
		testWithInputAndOutputStreams( IOPump.DEFAULT_BUFFER_SIZE );
	}

	public void testWithInputStreamAndWriterBuffer1() throws Exception {
		testWithInputStreamAndWriter( 1 );
	}

	public void testWithInputStreamAndWriterBuffer2() throws Exception {
		testWithInputStreamAndWriter( 2 );
	}

	public void testWithInputStreamAndWriterBuffer3() throws Exception {
		testWithInputStreamAndWriter( 3 );
	}

	public void testWithInputStreamAndWriterBuffer4() throws Exception {
		testWithInputStreamAndWriter( 4 );
	}

	public void testWithInputStreamAndWriterBuffer5() throws Exception {
		testWithInputStreamAndWriter( 5 );
	}

	public void testWithInputStreamAndWriterBuffer32() throws Exception {
		testWithInputStreamAndWriter( 32 );
	}

	public void testWithInputStreamAndWriterBufferDefault() throws Exception {
		testWithInputStreamAndWriter( IOPump.DEFAULT_BUFFER_SIZE );
	}

	public void testWithReaderAndOutputStreamBuffer1() throws Exception {
		testWithReaderAndOutputStream( 1 );
	}

	public void testWithReaderAndOutputStreamBuffer2() throws Exception {
		testWithReaderAndOutputStream( 2 );
	}

	public void testWithReaderAndOutputStreamBuffer3() throws Exception {
		testWithReaderAndOutputStream( 3 );
	}

	public void testWithReaderAndOutputStreamBuffer4() throws Exception {
		testWithReaderAndOutputStream( 4 );
	}

	public void testWithReaderAndOutputStreamBuffer5() throws Exception {
		testWithReaderAndOutputStream( 5 );
	}

	public void testWithReaderAndOutputStreamBuffer32() throws Exception {
		testWithReaderAndOutputStream( 32 );
	}

	public void testWithReaderAndOutputStreamBufferDefault() throws Exception {
		testWithReaderAndOutputStream( IOPump.DEFAULT_BUFFER_SIZE );
	}

	public void testWithReaderAndWriterBuffer1() throws Exception {
		testWithReaderAndWriter( 1 );
	}

	public void testWithReaderAndWriterBuffer2() throws Exception {
		testWithReaderAndWriter( 2 );
	}

	public void testWithReaderAndWriterBuffer3() throws Exception {
		testWithReaderAndWriter( 3 );
	}

	public void testWithReaderAndWriterBuffer4() throws Exception {
		testWithReaderAndWriter( 4 );
	}

	public void testWithReaderAndWriterBuffer5() throws Exception {
		testWithReaderAndWriter( 5 );
	}

	public void testWithReaderAndWriterBuffer32() throws Exception {
		testWithReaderAndWriter( 32 );
	}

	public void testWithReaderAndWriterBufferDefault() throws Exception {
		testWithReaderAndWriter( IOPump.DEFAULT_BUFFER_SIZE );
	}

	private void testWithInputAndOutputStreams( int bufferSize ) throws Exception {
		for( String string : testStrings ) {
			testInputToOutput( string, bufferSize );
		}
	}

	private void testWithInputStreamAndWriter( int bufferSize ) throws Exception {
		for( String string : testStrings ) {
			for( String charset : charsets ) {
				testInputToWriter( string, Charset.forName( charset ), bufferSize );
			}
		}
	}

	private void testWithReaderAndOutputStream( int bufferSize ) throws Exception {
		for( String string : testStrings ) {
			for( String charset : charsets ) {
				testReaderToOutput( string, Charset.forName( charset ), bufferSize );
			}
		}
	}

	private void testWithReaderAndWriter( int bufferSize ) throws Exception {
		for( String string : testStrings ) {
			testReaderToWriter( string, bufferSize );
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

	private void testReaderToWriter( String string, int bufferSize ) throws Exception {
		Reader reader = new CharArrayReader( string.toCharArray() );
		CharArrayWriter writer = new CharArrayWriter();

		IOPump pump = new IOPump( reader, writer );
		pump.startAndWait();
		assertEquals( string, new String( writer.toCharArray() ) );
	}

}
