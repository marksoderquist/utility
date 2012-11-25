package com.parallelsymmetry.utility;

public class ProcessUtil {

	public static final boolean isProcessRunning( Process process ) {
		try {
			process.exitValue();
			return false;
		} catch( IllegalThreadStateException exception ) {
			return true;
		}
	}

}
