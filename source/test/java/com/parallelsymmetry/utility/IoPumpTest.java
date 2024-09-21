package com.parallelsymmetry.utility;

import com.parallelsymmetry.utility.log.DefaultHandler;
import com.parallelsymmetry.utility.log.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IoPumpTest extends BaseTestCase {

	private final String[] charsets = new String[]{ "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE" };

	private final String longString = "qpweiofhpqiweuf\n\n\n\n\r\r\r\rhiqerufgipq\tegbqeiprgbvipqer ipg qper\u0000ibqiwepf qipwevqipweriqgpeig\u0002qeripgqeipgqipegviqpgpiqegipg";

	private final String[] testStrings = new String[]{ "", "test string", "test\nstring", longString };

	@BeforeEach
	@Override
	public void setup() throws Exception {
		super.setup();
		Log.setLevel( Log.NONE );
	}

	@Test
	public void testWithInputAndOutputStreamsBuffer1() throws Exception {
		testWithInputAndOutputStreams( 1 );
	}

	@Test
	public void testWithInputAndOutputStreamsBuffer2() throws Exception {
		testWithInputAndOutputStreams( 2 );
	}

	@Test
	public void testWithInputAndOutputStreamsBuffer3() throws Exception {
		testWithInputAndOutputStreams( 3 );
	}

	@Test
	public void testWithInputAndOutputStreamsBuffer4() throws Exception {
		testWithInputAndOutputStreams( 4 );
	}

	@Test
	public void testWithInputAndOutputStreamsBuffer5() throws Exception {
		testWithInputAndOutputStreams( 5 );
	}

	@Test
	public void testWithInputAndOutputStreamsBuffer32() throws Exception {
		testWithInputAndOutputStreams( 32 );
	}

	@Test
	public void testWithInputAndOutputStreamsBufferDefault() throws Exception {
		testWithInputAndOutputStreams( IoPump.DEFAULT_BUFFER_SIZE );
	}

	@Test
	public void testWithInputStreamAndWriterBuffer1() throws Exception {
		testWithInputStreamAndWriter( 1 );
	}

	@Test
	public void testWithInputStreamAndWriterBuffer2() throws Exception {
		testWithInputStreamAndWriter( 2 );
	}

	@Test
	public void testWithInputStreamAndWriterBuffer3() throws Exception {
		testWithInputStreamAndWriter( 3 );
	}

	@Test
	public void testWithInputStreamAndWriterBuffer4() throws Exception {
		testWithInputStreamAndWriter( 4 );
	}

	@Test
	public void testWithInputStreamAndWriterBuffer5() throws Exception {
		testWithInputStreamAndWriter( 5 );
	}

	@Test
	public void testWithInputStreamAndWriterBuffer32() throws Exception {
		testWithInputStreamAndWriter( 32 );
	}

	@Test
	public void testWithInputStreamAndWriterBufferDefault() throws Exception {
		testWithInputStreamAndWriter( IoPump.DEFAULT_BUFFER_SIZE );
	}

	@Test
	public void testWithReaderAndOutputStreamBuffer1() throws Exception {
		testWithReaderAndOutputStream( 1 );
	}

	@Test
	public void testWithReaderAndOutputStreamBuffer2() throws Exception {
		testWithReaderAndOutputStream( 2 );
	}

	@Test
	public void testWithReaderAndOutputStreamBuffer3() throws Exception {
		testWithReaderAndOutputStream( 3 );
	}

	@Test
	public void testWithReaderAndOutputStreamBuffer4() throws Exception {
		testWithReaderAndOutputStream( 4 );
	}

	@Test
	public void testWithReaderAndOutputStreamBuffer5() throws Exception {
		testWithReaderAndOutputStream( 5 );
	}

	@Test
	public void testWithReaderAndOutputStreamBuffer32() throws Exception {
		testWithReaderAndOutputStream( 32 );
	}

	@Test
	public void testWithReaderAndOutputStreamBufferDefault() throws Exception {
		testWithReaderAndOutputStream( IoPump.DEFAULT_BUFFER_SIZE );
	}

	@Test
	public void testWithReaderAndWriterBuffer1() throws Exception {
		testWithReaderAndWriter( 1 );
	}

	@Test
	public void testWithReaderAndWriterBuffer2() throws Exception {
		testWithReaderAndWriter( 2 );
	}

	@Test
	public void testWithReaderAndWriterBuffer3() throws Exception {
		testWithReaderAndWriter( 3 );
	}

	@Test
	public void testWithReaderAndWriterBuffer4() throws Exception {
		testWithReaderAndWriter( 4 );
	}

	@Test
	public void testWithReaderAndWriterBuffer5() throws Exception {
		testWithReaderAndWriter( 5 );
	}

	@Test
	public void testWithReaderAndWriterBuffer32() throws Exception {
		testWithReaderAndWriter( 32 );
	}

	@Test
	public void testWithReaderAndWriterBufferDefault() throws Exception {
		testWithReaderAndWriter( IoPump.DEFAULT_BUFFER_SIZE );
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

		IoPump pump = new IoPump( input, output );
		pump.startAndWait();
		pump.waitFor();
		assertEquals( string, output.toString() );
	}

	private void testInputToWriter( String string, Charset charset, int bufferSize ) throws Exception {
		if( !charset.canEncode() ) return;

		String comparator = new String( string.getBytes( charset ), charset );
		if( !comparator.equals( string ) ) return;

		InputStream input = new ByteArrayInputStream( string.getBytes( charset ) );
		CharArrayWriter writer = new CharArrayWriter();
		IoPump pump = new IoPump( input, writer, charset );
		pump.startAndWait();
		pump.waitFor();
		assertEquals( string, new String( writer.toCharArray() ), "charset: " + charset );
	}

	private void testReaderToOutput( String string, Charset charset, int bufferSize ) throws Exception {
		if( !charset.canEncode() ) return;

		String comparator = new String( string.getBytes( charset ), charset );
		if( !comparator.equals( string ) ) return;

		Reader reader = new CharArrayReader( string.toCharArray() );
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		IoPump pump = new IoPump( reader, output, charset );
		pump.startAndWait();
		pump.waitFor();
		assertEquals( string, output.toString( charset ), "charset: " + charset );
	}

	private void testReaderToWriter( String string, int bufferSize ) throws Exception {
		Reader reader = new CharArrayReader( string.toCharArray() );
		CharArrayWriter writer = new CharArrayWriter();

		IoPump pump = new IoPump( reader, writer );
		pump.startAndWait();
		pump.waitFor();
		assertEquals( string, new String( writer.toCharArray() ) );
	}

	@Test
	public void testLogContent() throws Exception {
		String string = "abcd";
		Reader reader = new CharArrayReader( string.toCharArray() );
		CharArrayWriter writer = new CharArrayWriter();

		ByteArrayOutputStream logOutput = new ByteArrayOutputStream();
		Log.setDefaultHandler( new DefaultHandler( logOutput ) );

		Level level = Log.getLevel();
		Log.setLevel( Log.TRACE );
		IoPump pump = new IoPump( "Test", reader, writer );
		pump.setLogEnabled( true );
		pump.setLogContent( true );
		pump.startAndWait();
		pump.waitFor();
		Log.setLevel( level );

		assertEquals( "abcd", new String( writer.toCharArray() ) );

		StringBuilder builder = new StringBuilder();
		builder.append( "[T] Test IOPump started.\n" );
		builder.append( "[T] Test: abcd\n" );
		builder.append( "[T] Test IOPump finished.\n" );
		assertEquals( builder.toString(), logOutput.toString() );
	}

	@Test
	public void testLogContentWithSlowReader() throws Exception {
		String string = "abcde";
		Reader reader = new SlowCharArrayReader( string.toCharArray(), 50 );
		CharArrayWriter writer = new CharArrayWriter();

		ByteArrayOutputStream logOutput = new ByteArrayOutputStream();
		Log.setDefaultHandler( new DefaultHandler( logOutput ) );

		Level level = Log.getLevel();
		Log.setLevel( Log.TRACE );
		IoPump pump = new IoPump( "Test", reader, writer );
		pump.setLogEnabled( true );
		pump.setLogContent( true );
		pump.setLineTimeout( 20 );
		pump.startAndWait();
		pump.waitFor();
		Log.setLevel( level );

		assertEquals( string, new String( writer.toCharArray() ) );

		StringBuilder builder = new StringBuilder();
		builder.append( "[T] Test IOPump started.\n" );
		builder.append( "[T] Test: a\n" );
		builder.append( "[T] Test: b\n" );
		builder.append( "[T] Test: c\n" );
		builder.append( "[T] Test: d\n" );
		builder.append( "[T] Test: e\n" );
		builder.append( "[T] Test IOPump finished.\n" );
		assertEquals( builder.toString(), new String( logOutput.toByteArray() ) );
	}

	@Test
	public void testLogLinesWithSlowReader() throws Exception {
		String string = "some lines\nof text";
		Reader reader = new SlowCharArrayReader( string.toCharArray(), 20 );
		CharArrayWriter writer = new CharArrayWriter();

		ByteArrayOutputStream logOutput = new ByteArrayOutputStream();
		Log.setDefaultHandler( new DefaultHandler( logOutput ) );

		Level level = Log.getLevel();
		Log.setLevel( Log.TRACE );
		IoPump pump = new IoPump( "Test", reader, writer );
		pump.setLogEnabled( true );
		pump.setLogContent( true );
		pump.setLineTimeout( 50 );
		pump.startAndWait();
		pump.waitFor();
		Log.setLevel( level );

		assertEquals( string, new String( writer.toCharArray() ) );

		StringBuilder builder = new StringBuilder();
		builder.append( "[T] Test IOPump started.\n" );
		builder.append( "[T] Test: some lines\n" );
		builder.append( "[T] Test: of text\n" );
		builder.append( "[T] Test IOPump finished.\n" );
		assertEquals( builder.toString(), logOutput.toString() );
	}

	private static class SlowCharArrayReader extends CharArrayReader {

		private int delay = 100;

		public SlowCharArrayReader( char[] buffer, int delay ) {
			super( buffer, 0, buffer.length );
			this.delay = delay;
		}

		public SlowCharArrayReader( char[] buffer, int offset, int length ) {
			super( buffer, offset, length );
		}

		public int read() throws IOException {
			ThreadUtil.pause( delay );
			return super.read();
		}

		public int read( char[] buffer ) throws IOException {
			ThreadUtil.pause( delay );
			return super.read( buffer, 0, 1 );
		}

		public int read( char[] buffer, int offset, int length ) throws IOException {
			ThreadUtil.pause( delay );
			new Throwable( "read" ).printStackTrace( System.out );
			return super.read( buffer, offset, 1 );
		}

	}

}
