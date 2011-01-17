package com.parallelsymmetry.escape.utility;

import java.util.Locale;

import junit.framework.TestCase;

public class MavenVersionTest extends TestCase {

	private static final String[] VERSIONS_QUALIFIER = { "1-alpha2snapshot", "1-alpha2", "1-alpha-123", "1-beta-2", "1-beta123", "1-m2", "1-m11", "1-rc", "1-cr2", "1-rc123", "1-SNAPSHOT", "1", "1-sp", "1-sp2", "1-sp123", "1-abc", "1-def", "1-pom-1", "1-1-snapshot", "1-1", "1-2", "1-123" };

	private static final String[] VERSIONS_NUMBER = { "2.0", "2-1", "2.0.a", "2.0.0.a", "2.0.2", "2.0.123", "2.1.0", "2.1-a", "2.1b", "2.1-c", "2.1-1", "2.1.0.1", "2.2", "2.123", "11.a2", "11.a11", "11.b2", "11.b11", "11.m2", "11.m11", "11", "11.a", "11b", "11c", "11m" };

	public void testIsSnapshot() {
		assertEquals( true, new MavenVersion( "1-alpha2snapshot" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-alpha2" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-alpha-123" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-beta-2" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-beta123" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-m2" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-m11" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-rc" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-cr2" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-rc123" ).isSnapshot() );
		assertEquals( true, new MavenVersion( "1-SNAPSHOT" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-sp" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-sp2" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-sp123" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-abc" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-def" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-pom-1" ).isSnapshot() );
		assertEquals( true, new MavenVersion( "1-1-snapshot" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-1" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-2" ).isSnapshot() );
		assertEquals( false, new MavenVersion( "1-123" ).isSnapshot() );
	}

	public void testVersionsQualifier() {
		checkVersionsOrder( VERSIONS_QUALIFIER );
	}

	public void testVersionsNumber() {
		checkVersionsOrder( VERSIONS_NUMBER );
	}

	public void testVersionsEqual() {
		checkVersionsEqual( "1", "1" );
		checkVersionsEqual( "1", "1.0" );
		checkVersionsEqual( "1", "1.0.0" );
		checkVersionsEqual( "1.0", "1.0.0" );
		checkVersionsEqual( "1", "1-0" );
		checkVersionsEqual( "1", "1.0-0" );
		checkVersionsEqual( "1.0", "1.0-0" );

		// no separator between number and character
		checkVersionsEqual( "1a", "1.a" );
		checkVersionsEqual( "1a", "1-a" );
		checkVersionsEqual( "1a", "1.0-a" );
		checkVersionsEqual( "1a", "1.0.0-a" );
		checkVersionsEqual( "1.0a", "1.0.a" );
		checkVersionsEqual( "1.0.0a", "1.0.0.a" );
		checkVersionsEqual( "1x", "1.x" );
		checkVersionsEqual( "1x", "1-x" );
		checkVersionsEqual( "1x", "1.0-x" );
		checkVersionsEqual( "1x", "1.0.0-x" );
		checkVersionsEqual( "1.0x", "1.0.x" );
		checkVersionsEqual( "1.0.0x", "1.0.0.x" );

		// aliases
		checkVersionsEqual( "1ga", "1" );
		checkVersionsEqual( "1final", "1" );
		checkVersionsEqual( "1cr", "1rc" );

		// special "aliases" a, b, m, p and u for alpha, beta, milestone, patch and update
		checkVersionsEqual( "1a1", "1alpha1" );
		checkVersionsEqual( "1b2", "1beta2" );
		checkVersionsEqual( "1m3", "1milestone3" );
		checkVersionsEqual( "1p4", "1patch4" );
		checkVersionsEqual( "1u5", "1update5" );

		checkVersionsEqual( "1.0-patch", "1.0-patch-0" );
		checkVersionsEqual( "1.0-update", "1.0-update-0" );

		// case insensitive
		checkVersionsEqual( "1X", "1x" );
		checkVersionsEqual( "1A", "1a" );
		checkVersionsEqual( "1B", "1b" );
		checkVersionsEqual( "1M", "1m" );
		checkVersionsEqual( "1Ga", "1" );
		checkVersionsEqual( "1GA", "1" );
		checkVersionsEqual( "1Final", "1" );
		checkVersionsEqual( "1FinaL", "1" );
		checkVersionsEqual( "1FINAL", "1" );
		checkVersionsEqual( "1Cr", "1Rc" );
		checkVersionsEqual( "1cR", "1rC" );
		checkVersionsEqual( "1m3", "1Milestone3" );
		checkVersionsEqual( "1m3", "1MileStone3" );
		checkVersionsEqual( "1m3", "1MILESTONE3" );
	}

	public void testVersionComparing() {
		checkVersionsOrder( "1", "2" );
		checkVersionsOrder( "1.5", "2" );
		checkVersionsOrder( "1", "2.5" );
		checkVersionsOrder( "1.0", "1.1" );
		checkVersionsOrder( "1.1", "1.2" );
		checkVersionsOrder( "1.0.0", "1.1" );
		checkVersionsOrder( "1.0.1", "1.1" );
		checkVersionsOrder( "1.1", "1.2.0" );

		checkVersionsOrder( "1.0-alpha-1", "1.0" );
		checkVersionsOrder( "1.0-alpha-1", "1.0-alpha-2" );
		checkVersionsOrder( "1.0-alpha-1", "1.0-beta-1" );

		checkVersionsOrder( "1.0-beta-1", "1.0-SNAPSHOT" );
		checkVersionsOrder( "1.0-SNAPSHOT", "1.0" );
		checkVersionsOrder( "1.0-alpha-1-SNAPSHOT", "1.0-alpha-1" );

		checkVersionsOrder( "1.0", "1.0-1" );
		checkVersionsOrder( "1.0-1", "1.0-2" );
		checkVersionsOrder( "1.0.0", "1.0-1" );

		checkVersionsOrder( "2.0-1", "2.0.1" );
		checkVersionsOrder( "2.0.1-klm", "2.0.1-lmn" );
		checkVersionsOrder( "2.0.1", "2.0.1-xyz" );

		checkVersionsOrder( "2.0.1", "2.0.1-123" );
		checkVersionsOrder( "2.0.1-xyz", "2.0.1-123" );

		// Patch versions.
		checkVersionsOrder( "1.0", "1.0-p" );
		checkVersionsOrder( "1.0", "1.0-patch" );
		checkVersionsOrder( "1.0-patch", "1.0-patch-1" );
		checkVersionsOrder( "1.0-patch-1", "1.0-patch-2" );

		// Update versions.
		checkVersionsOrder( "1.0", "1.0-u" );
		checkVersionsOrder( "1.0", "1.0-update" );
		checkVersionsOrder( "1.0-update", "1.0-update-1" );
		checkVersionsOrder( "1.0-update-1", "1.0-update-2" );

		// Java style versions.
		checkVersionsOrder( "1.6", "1.6.0_22" );
		checkVersionsOrder( "1.6.0_22", "1.7" );
	}

	public void testLocaleIndependent() {
		Locale original = Locale.getDefault();
		Locale[] locales = { Locale.ENGLISH, new Locale( "tr" ), Locale.getDefault() };
		try {
			for( Locale locale : locales ) {
				Locale.setDefault( locale );
				checkVersionsEqual( "1-abcdefghijklmnopqrstuvwxyz", "1-ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
			}
		} finally {
			Locale.setDefault( original );
		}
	}

	public void testReuse() {
		MavenVersion version1 = new MavenVersion( "1" );
		version1.parseVersion( "2" );

		Comparable<MavenVersion> version2 = newComparable( "2" );

		assertEquals( "reused instance should be equivalent to new instance", version1, version2 );
	}

	private MavenVersion newComparable( String version ) {
		return new MavenVersion( version );
	}

	private void checkVersionsOrder( String[] strings ) {
		MavenVersion[] versions = new MavenVersion[strings.length];
		for( int index = 0; index < strings.length; index++ ) {
			versions[index] = newComparable( strings[index] );
		}

		for( int lowIndex = 1; lowIndex < strings.length; lowIndex++ ) {
			MavenVersion low = versions[lowIndex - 1];
			for( int highIndex = lowIndex; highIndex < strings.length; highIndex++ ) {
				MavenVersion high = versions[highIndex];
				assertTrue( "expected " + low + " < " + high, low.compareTo( high ) < 0 );
				assertTrue( "expected " + high + " > " + low, high.compareTo( low ) > 0 );
			}
		}
	}

	private void checkVersionsEqual( String string1, String string2 ) {
		MavenVersion version1 = newComparable( string1 );
		MavenVersion version2 = newComparable( string2 );
		assertTrue( "expected " + string1 + " == " + string2, version1.compareTo( version2 ) == 0 );
		assertTrue( "expected " + string2 + " == " + string1, version2.compareTo( version1 ) == 0 );
		assertTrue( "expected same hashcode for " + string1 + " and " + string2, version1.hashCode() == version2.hashCode() );
		assertTrue( "expected " + string1 + ".equals( " + string2 + " )", version1.equals( version2 ) );
		assertTrue( "expected " + string2 + ".equals( " + string1 + " )", version2.equals( version1 ) );
	}

	private void checkVersionsOrder( String string1, String string2 ) {
		MavenVersion version1 = newComparable( string1 );
		MavenVersion version2 = newComparable( string2 );
		assertTrue( "expected " + string1 + " < " + string2, version1.compareTo( version2 ) < 0 );
		assertTrue( "expected " + string2 + " > " + string1, version2.compareTo( version1 ) > 0 );
	}

}
