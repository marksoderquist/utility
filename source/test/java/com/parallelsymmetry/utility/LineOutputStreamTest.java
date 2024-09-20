package com.parallelsymmetry.utility;

import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LineOutputStreamTest extends BaseTestCase {

	@Test
	public void testNoLines() {
		MockLineListener listener = new MockLineListener();
		LineOutputStream stream = new LineOutputStream();
		stream.addLineListener( listener );

		stream.close();

		List<String> lines = listener.getLines();
		assertEquals( 0, lines.size() );
	}

	@Test
	public void testWriteOneLine() throws Exception {
		MockLineListener listener = new MockLineListener();
		LineOutputStream stream = new LineOutputStream();
		stream.addLineListener( listener );

		stream.write( "line one".getBytes() );
		stream.close();

		List<String> lines = listener.getLines();
		assertEquals( 1, lines.size() );

		int index = 0;
		assertEquals( "line one", lines.get( index ) );
	}

	@Test
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
		assertEquals( "line two", lines.get( index ) );
	}

	@Getter
	private static class MockLineListener implements LineListener {

		private final List<String> lines = new ArrayList<>();

		public void line( String line ) {
			lines.add( line );
		}

	}

}
