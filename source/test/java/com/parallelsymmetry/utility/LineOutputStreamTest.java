package com.parallelsymmetry.utility;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class LineOutputStreamTest extends TestCase {

	public void testNoLines() throws Exception {
		MockLineListener listener = new MockLineListener();
		LineOutputStream stream = new LineOutputStream();
		stream.addLineListener( listener );

		stream.close();

		List<String> lines = listener.getLines();
		assertEquals( 0, lines.size() );
	}

	public void testWriteOneLine() throws Exception {
		MockLineListener listener = new MockLineListener();
		LineOutputStream stream = new LineOutputStream();
		stream.addLineListener( listener );

		stream.write( "line one".getBytes() );
		stream.close();

		List<String> lines = listener.getLines();
		assertEquals( 1, lines.size() );

		int index = 0;
		assertEquals( "line one", lines.get( index++ ) );
	}

	public void testWriteTwoLines() throws Exception {
		MockLineListener listener = new MockLineListener();
		LineOutputStream stream = new LineOutputStream();
		stream.addLineListener( listener );

		stream.write( "line one\nline two".getBytes() );
		stream.close();

		List<String> lines = listener.getLines();
		assertEquals( 2, lines.size() );

		int index = 0;
		assertEquals( "line one", lines.get( index++ ) );
		assertEquals( "line two", lines.get( index++ ) );
	}

	private class MockLineListener implements LineListener {

		private List<String> lines = new ArrayList<String>();

		public void line( String line ) {
			lines.add( line );
		}

		public List<String> getLines() {
			return lines;
		}

	}

}
