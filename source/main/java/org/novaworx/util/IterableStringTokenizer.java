package org.novaworx.util;

import java.util.Iterator;
import java.util.StringTokenizer;

public class IterableStringTokenizer extends StringTokenizer implements Iterable<String> {

	public IterableStringTokenizer( String str ) {
		super( str );
	}

	public IterableStringTokenizer( String str, String delim ) {
		super( str, delim );
	}

	public IterableStringTokenizer( String str, String delim, boolean returnDelims ) {
		super( str, delim, returnDelims );
	}
	
	@Override
	public Iterator<String> iterator() {
		return new TokenizerIterator();
	}

	
	private class TokenizerIterator implements Iterator<String> {

		@Override
		public boolean hasNext() {
			return hasMoreTokens();
		}

		@Override
		public String next() {
			return nextToken();
		}

		@Override
		public void remove() {}
		
	}

}

