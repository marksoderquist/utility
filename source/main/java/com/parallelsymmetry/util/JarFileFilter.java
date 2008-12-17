package com.parallelsymmetry.util;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Mark Soderquist
 */
public class JarFileFilter implements FileFilter {

	public boolean accept( File file ) {
		return file.getName().endsWith( ".jar" );
	}

}
