package com.parallelsymmetry.escape.utility;

import java.util.Locale;

import junit.framework.TestCase;

public class VersionTest extends TestCase {

	private static final String[] VERSIONS_QUALIFIER = { "1-alpha2snapshot", "1-alpha2", "1-alpha-123", "1-beta-2", "1-beta123", "1-m2", "1-m11", "1-rc", "1-cr2", "1-rc123", "1-SNAPSHOT", "1", "1-sp", "1-sp2", "1-sp123", "1-abc", "1-def", "1-pom-1", "1-1-snapshot", "1-1", "1-2", "1-123" };

	private static final String[] VERSIONS_NUMBER = { "2.0", "2-1", "2.0.a", "2.0.0.a", "2.0.2", "2.0.123", "2.1.0", "2.1-a", "2.1b", "2.1-c", "2.1-1", "2.1.0.1", "2.2", "2.123", "11.a2", "11.a11", "11.b2", "11.b11", "11.m2", "11.m11", "11", "11.a", "11b", "11c", "11m" };

	private Version version = new Version( "3.4.5-b-06" );

	private Version majorGreater = new Version( "4.4.5-b-06" );

	private Version majorLesser = new Version( "2.4.5-b-06" );

	private Version minorGreater = new Version( "3.5.3-b-04" );

	private Version minorLesser = new Version( "3.3.3-b-04" );

	private Version microGreater = new Version( "3.4.6-b-04" );

	private Version microLesser = new Version( "3.4.4-b-04" );

	private Version revisionGreater = new Version( "3.4.5-u-04" );

	private Version revisionLesser = new Version( "3.4.5-a-04" );

	private Version buildGreater = new Version( "3.4.5-b-07" );

	private Version buildLesser = new Version( "3.4.5-b-05" );

	private Version alpha = new Version( "3.4.5-a-06" );

	private Version beta = new Version( "3.4.5-b-06" );

	private Version release = new Version( "3.4.5-u-00" );

	private Version update = new Version( "3.4.5-u-06" );

	private Version snapshot = new Version( "3.4.5-SNAPSHOT" );

	public void testIsSnapshot() {
		assertEquals( true, new Version( "1-alpha2snapshot" ).isSnapshot() );
		assertEquals( false, new Version( "1-alpha2" ).isSnapshot() );
		assertEquals( false, new Version( "1-alpha-123" ).isSnapshot() );
		assertEquals( false, new Version( "1-beta-2" ).isSnapshot() );
		assertEquals( false, new Version( "1-beta123" ).isSnapshot() );
		assertEquals( false, new Version( "1-m2" ).isSnapshot() );
		assertEquals( false, new Version( "1-m11" ).isSnapshot() );
		assertEquals( false, new Version( "1-rc" ).isSnapshot() );
		assertEquals( false, new Version( "1-cr2" ).isSnapshot() );
		assertEquals( false, new Version( "1-rc123" ).isSnapshot() );
		assertEquals( true, new Version( "1-SNAPSHOT" ).isSnapshot() );
		assertEquals( false, new Version( "1" ).isSnapshot() );
		assertEquals( false, new Version( "1-sp" ).isSnapshot() );
		assertEquals( false, new Version( "1-sp2" ).isSnapshot() );
		assertEquals( false, new Version( "1-sp123" ).isSnapshot() );
		assertEquals( false, new Version( "1-abc" ).isSnapshot() );
		assertEquals( false, new Version( "1-def" ).isSnapshot() );
		assertEquals( false, new Version( "1-pom-1" ).isSnapshot() );
		assertEquals( true, new Version( "1-1-snapshot" ).isSnapshot() );
		assertEquals( false, new Version( "1-1" ).isSnapshot() );
		assertEquals( false, new Version( "1-2" ).isSnapshot() );
		assertEquals( false, new Version( "1-123" ).isSnapshot() );

		assertEquals( false, new Version( "2.0" ).isSnapshot() );
		assertEquals( false, new Version( "2-1" ).isSnapshot() );
		assertEquals( false, new Version( "2.0.a" ).isSnapshot() );
		assertEquals( false, new Version( "2.0.0.a" ).isSnapshot() );
		assertEquals( false, new Version( "2.0.2" ).isSnapshot() );
		assertEquals( false, new Version( "2.0.123" ).isSnapshot() );
		assertEquals( false, new Version( "2.1.0" ).isSnapshot() );
		assertEquals( false, new Version( "2.1-a" ).isSnapshot() );
		assertEquals( false, new Version( "2.1b" ).isSnapshot() );
		assertEquals( false, new Version( "2.1-c" ).isSnapshot() );
		assertEquals( false, new Version( "2.1-1" ).isSnapshot() );
		assertEquals( false, new Version( "2.1.0.1" ).isSnapshot() );
		assertEquals( false, new Version( "2.2" ).isSnapshot() );
		assertEquals( false, new Version( "2.123" ).isSnapshot() );
		assertEquals( false, new Version( "11.a2" ).isSnapshot() );
		assertEquals( false, new Version( "11.a11" ).isSnapshot() );
		assertEquals( false, new Version( "11.b2" ).isSnapshot() );
		assertEquals( false, new Version( "11.b11" ).isSnapshot() );
		assertEquals( false, new Version( "11.m2" ).isSnapshot() );
		assertEquals( false, new Version( "11.m11" ).isSnapshot() );
		assertEquals( false, new Version( "11" ).isSnapshot() );
		assertEquals( false, new Version( "11.a" ).isSnapshot() );
		assertEquals( false, new Version( "11b" ).isSnapshot() );
		assertEquals( false, new Version( "11c" ).isSnapshot() );
		assertEquals( false, new Version( "11m" ).isSnapshot() );
	}

	public void testIsAlpha() {
		assertEquals( true, new Version( "1-alpha2snapshot" ).isAlpha() );
		assertEquals( true, new Version( "1-alpha2" ).isAlpha() );
		assertEquals( true, new Version( "1-alpha-123" ).isAlpha() );
		assertEquals( false, new Version( "1-beta-2" ).isAlpha() );
		assertEquals( false, new Version( "1-beta123" ).isAlpha() );
		assertEquals( false, new Version( "1-m2" ).isAlpha() );
		assertEquals( false, new Version( "1-m11" ).isAlpha() );
		assertEquals( false, new Version( "1-rc" ).isAlpha() );
		assertEquals( false, new Version( "1-cr2" ).isAlpha() );
		assertEquals( false, new Version( "1-rc123" ).isAlpha() );
		assertEquals( false, new Version( "1-SNAPSHOT" ).isAlpha() );
		assertEquals( false, new Version( "1" ).isAlpha() );
		assertEquals( false, new Version( "1-sp" ).isAlpha() );
		assertEquals( false, new Version( "1-sp2" ).isAlpha() );
		assertEquals( false, new Version( "1-sp123" ).isAlpha() );
		assertEquals( false, new Version( "1-abc" ).isAlpha() );
		assertEquals( false, new Version( "1-def" ).isAlpha() );
		assertEquals( false, new Version( "1-pom-1" ).isAlpha() );
		assertEquals( false, new Version( "1-1-snapshot" ).isAlpha() );
		assertEquals( false, new Version( "1-1" ).isAlpha() );
		assertEquals( false, new Version( "1-2" ).isAlpha() );
		assertEquals( false, new Version( "1-123" ).isAlpha() );

		assertEquals( false, new Version( "2.0" ).isAlpha() );
		assertEquals( false, new Version( "2-1" ).isAlpha() );
		assertEquals( true, new Version( "2.0.a" ).isAlpha() );
		assertEquals( true, new Version( "2.0.0.a" ).isAlpha() );
		assertEquals( false, new Version( "2.0.2" ).isAlpha() );
		assertEquals( false, new Version( "2.0.123" ).isAlpha() );
		assertEquals( false, new Version( "2.1.0" ).isAlpha() );
		assertEquals( true, new Version( "2.1-a" ).isAlpha() );
		assertEquals( false, new Version( "2.1b" ).isAlpha() );
		assertEquals( false, new Version( "2.1-c" ).isAlpha() );
		assertEquals( false, new Version( "2.1-1" ).isAlpha() );
		assertEquals( false, new Version( "2.1.0.1" ).isAlpha() );
		assertEquals( false, new Version( "2.2" ).isAlpha() );
		assertEquals( false, new Version( "2.123" ).isAlpha() );
		assertEquals( true, new Version( "11.a2" ).isAlpha() );
		assertEquals( true, new Version( "11.a11" ).isAlpha() );
		assertEquals( false, new Version( "11.b2" ).isAlpha() );
		assertEquals( false, new Version( "11.b11" ).isAlpha() );
		assertEquals( false, new Version( "11.m2" ).isAlpha() );
		assertEquals( false, new Version( "11.m11" ).isAlpha() );
		assertEquals( false, new Version( "11" ).isAlpha() );
		assertEquals( true, new Version( "11.a" ).isAlpha() );
		assertEquals( false, new Version( "11b" ).isAlpha() );
		assertEquals( false, new Version( "11c" ).isAlpha() );
		assertEquals( false, new Version( "11m" ).isAlpha() );
	}

	public void testIsBeta() {
		assertEquals( false, new Version( "1-alpha2snapshot" ).isBeta() );
		assertEquals( false, new Version( "1-alpha2" ).isBeta() );
		assertEquals( false, new Version( "1-alpha-123" ).isBeta() );
		assertEquals( true, new Version( "1-beta-2" ).isBeta() );
		assertEquals( true, new Version( "1-beta123" ).isBeta() );
		assertEquals( false, new Version( "1-m2" ).isBeta() );
		assertEquals( false, new Version( "1-m11" ).isBeta() );
		assertEquals( false, new Version( "1-rc" ).isBeta() );
		assertEquals( false, new Version( "1-cr2" ).isBeta() );
		assertEquals( false, new Version( "1-rc123" ).isBeta() );
		assertEquals( false, new Version( "1-SNAPSHOT" ).isBeta() );
		assertEquals( false, new Version( "1" ).isBeta() );
		assertEquals( false, new Version( "1-sp" ).isBeta() );
		assertEquals( false, new Version( "1-sp2" ).isBeta() );
		assertEquals( false, new Version( "1-sp123" ).isBeta() );
		assertEquals( false, new Version( "1-abc" ).isBeta() );
		assertEquals( false, new Version( "1-def" ).isBeta() );
		assertEquals( false, new Version( "1-pom-1" ).isBeta() );
		assertEquals( false, new Version( "1-1-snapshot" ).isBeta() );
		assertEquals( false, new Version( "1-1" ).isBeta() );
		assertEquals( false, new Version( "1-2" ).isBeta() );
		assertEquals( false, new Version( "1-123" ).isBeta() );

		assertEquals( false, new Version( "2.0" ).isBeta() );
		assertEquals( false, new Version( "2-1" ).isBeta() );
		assertEquals( false, new Version( "2.0.a" ).isBeta() );
		assertEquals( false, new Version( "2.0.0.a" ).isBeta() );
		assertEquals( false, new Version( "2.0.2" ).isBeta() );
		assertEquals( false, new Version( "2.0.123" ).isBeta() );
		assertEquals( false, new Version( "2.1.0" ).isBeta() );
		assertEquals( false, new Version( "2.1-a" ).isBeta() );
		assertEquals( true, new Version( "2.1b" ).isBeta() );
		assertEquals( false, new Version( "2.1-c" ).isBeta() );
		assertEquals( false, new Version( "2.1-1" ).isBeta() );
		assertEquals( false, new Version( "2.1.0.1" ).isBeta() );
		assertEquals( false, new Version( "2.2" ).isBeta() );
		assertEquals( false, new Version( "2.123" ).isBeta() );
		assertEquals( false, new Version( "11.a2" ).isBeta() );
		assertEquals( false, new Version( "11.a11" ).isBeta() );
		assertEquals( true, new Version( "11.b2" ).isBeta() );
		assertEquals( true, new Version( "11.b11" ).isBeta() );
		assertEquals( false, new Version( "11.m2" ).isBeta() );
		assertEquals( false, new Version( "11.m11" ).isBeta() );
		assertEquals( false, new Version( "11" ).isBeta() );
		assertEquals( false, new Version( "11.a" ).isBeta() );
		assertEquals( true, new Version( "11b" ).isBeta() );
		assertEquals( false, new Version( "11c" ).isBeta() );
		assertEquals( false, new Version( "11m" ).isBeta() );
	}

	public void testCompareVersions() {
		assertEquals( "Compare equal versions", 0, Version.compareVersions( version, version ) );

		assertEquals( "Compare greater major", -1, Version.compareVersions( version, majorGreater ) );
		assertEquals( "Compare lesser major", 1, Version.compareVersions( version, majorLesser ) );

		assertEquals( "Compare greater minor", -1, Version.compareVersions( version, minorGreater ) );
		assertEquals( "Compare lesser minor", 1, Version.compareVersions( version, minorLesser ) );

		assertEquals( "Compare greater micro", -1, Version.compareVersions( version, microGreater ) );
		assertEquals( "Compare lesser micro", 1, Version.compareVersions( version, microLesser ) );

		assertEquals( "Compare greater revision", -1, Version.compareVersions( version, revisionGreater ) );
		assertEquals( "Compare lesser revision", 1, Version.compareVersions( version, revisionLesser ) );

		assertEquals( "Compare greater build", -1, Version.compareVersions( version, buildGreater ) );
		assertEquals( "Compare lesser build", 1, Version.compareVersions( version, buildLesser ) );
	}

	public void testCompareVersionWithSnapshot() {
		assertEquals( "Compare snapshot versions", 1, Version.compareVersions( version, snapshot ) );
		assertEquals( "Compare snapshot versions", -1, Version.compareVersions( snapshot, version ) );
	}

	public void testToString() {
		assertEquals( "unknown", new Version( null ).toString() );

		assertEquals( "3.4.5-a-06", alpha.toString() );
		assertEquals( "3.4.5-b-06", beta.toString() );
		assertEquals( "3.4.5-u-00", release.toString() );
		assertEquals( "3.4.5-u-06", update.toString() );
		assertEquals( "3.4.5-SNAPSHOT", snapshot.toString() );

		for( String string : VERSIONS_NUMBER ) {
			assertEquals( string, new Version( string ).toString() );
		}
		for( String string : VERSIONS_QUALIFIER ) {
			assertEquals( string, new Version( string ).toString() );
		}
	}

	public void testToHumanString() {
		assertEquals( "2.0", new Version( "2.0" ).toHumanString() );
		assertEquals( "2-1", new Version( "2-1" ).toHumanString() );
		assertEquals( "2.0 Alpha", new Version( "2.0.a" ).toHumanString() );
		assertEquals( "2.0.0 Alpha", new Version( "2.0.0.a" ).toHumanString() );
		assertEquals( "2.0.2", new Version( "2.0.2" ).toHumanString() );
		assertEquals( "2.0.123", new Version( "2.0.123" ).toHumanString() );
		assertEquals( "2.1.0", new Version( "2.1.0" ).toHumanString() );
		assertEquals( "2.1 Alpha", new Version( "2.1-a" ).toHumanString() );
		assertEquals( "2.1 Beta", new Version( "2.1b" ).toHumanString() );
		assertEquals( "2.1 c", new Version( "2.1-c" ).toHumanString() );
		assertEquals( "2.1-1", new Version( "2.1-1" ).toHumanString() );
		assertEquals( "2.1.0.1", new Version( "2.1.0.1" ).toHumanString() );
		assertEquals( "2.2", new Version( "2.2" ).toHumanString() );
		assertEquals( "2.123", new Version( "2.123" ).toHumanString() );
		assertEquals( "11 Alpha 2", new Version( "11.a2" ).toHumanString() );
		assertEquals( "11 Alpha 11", new Version( "11.a11" ).toHumanString() );
		assertEquals( "11 Beta 2", new Version( "11.b2" ).toHumanString() );
		assertEquals( "11 Beta 11", new Version( "11.b11" ).toHumanString() );
		assertEquals( "11 Milestone 2", new Version( "11.m2" ).toHumanString() );
		assertEquals( "11 Milestone 11", new Version( "11.m11" ).toHumanString() );
		assertEquals( "11", new Version( "11" ).toHumanString() );
		assertEquals( "11 Alpha", new Version( "11.a" ).toHumanString() );
		assertEquals( "11 Beta", new Version( "11b" ).toHumanString() );
		assertEquals( "11 c", new Version( "11c" ).toHumanString() );
		assertEquals( "11 Milestone", new Version( "11m" ).toHumanString() );

		assertEquals( "1 Alpha 2 SNAPSHOT", new Version( "1-alpha2snapshot" ).toHumanString() );
		assertEquals( "1 Alpha 2", new Version( "1-alpha2" ).toHumanString() );
		assertEquals( "1 Alpha 123", new Version( "1-alpha-123" ).toHumanString() );
		assertEquals( "1 Beta 2", new Version( "1-beta-2" ).toHumanString() );
		assertEquals( "1 Beta 123", new Version( "1-beta123" ).toHumanString() );
		assertEquals( "1 Milestone 2", new Version( "1-m2" ).toHumanString() );
		assertEquals( "1 Milestone 11", new Version( "1-m11" ).toHumanString() );
		assertEquals( "1 Release Candidate", new Version( "1-rc" ).toHumanString() );
		assertEquals( "1 Release Candidate 2", new Version( "1-cr2" ).toHumanString() );
		assertEquals( "1 Release Candidate 123", new Version( "1-rc123" ).toHumanString() );
		assertEquals( "1 SNAPSHOT", new Version( "1-SNAPSHOT" ).toHumanString() );
		assertEquals( "1", new Version( "1" ).toHumanString() );
		assertEquals( "1 Service Pack", new Version( "1-sp" ).toHumanString() );
		assertEquals( "1 Service Pack 2", new Version( "1-sp2" ).toHumanString() );
		assertEquals( "1 Service Pack 123", new Version( "1-sp123" ).toHumanString() );
		assertEquals( "1 abc", new Version( "1-abc" ).toHumanString() );
		assertEquals( "1 def", new Version( "1-def" ).toHumanString() );
		assertEquals( "1 pom 1", new Version( "1-pom-1" ).toHumanString() );
		assertEquals( "1-1 SNAPSHOT", new Version( "1-1-snapshot" ).toHumanString() );
		assertEquals( "1-1", new Version( "1-1" ).toHumanString() );
		assertEquals( "1-2", new Version( "1-2" ).toHumanString() );
		assertEquals( "1-123", new Version( "1-123" ).toHumanString() );

		assertEquals( "3.4.5 Alpha 06", alpha.toHumanString() );
		assertEquals( "3.4.5 Beta 06", beta.toHumanString() );
		assertEquals( "3.4.5 Update 00", release.toHumanString() );
		assertEquals( "3.4.5 Update 06", update.toHumanString() );
		assertEquals( "3.4.5 SNAPSHOT", snapshot.toHumanString() );
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

	private Version newComparable( String version ) {
		return new Version( version );
	}

	private void checkVersionsOrder( String[] strings ) {
		Version[] versions = new Version[strings.length];
		for( int index = 0; index < strings.length; index++ ) {
			versions[index] = newComparable( strings[index] );
		}

		for( int lowIndex = 1; lowIndex < strings.length; lowIndex++ ) {
			Version low = versions[lowIndex - 1];
			for( int highIndex = lowIndex; highIndex < strings.length; highIndex++ ) {
				Version high = versions[highIndex];
				assertTrue( "expected " + low + " < " + high, low.compareTo( high ) < 0 );
				assertTrue( "expected " + high + " > " + low, high.compareTo( low ) > 0 );
			}
		}
	}

	private void checkVersionsEqual( String string1, String string2 ) {
		Version version1 = newComparable( string1 );
		Version version2 = newComparable( string2 );
		assertTrue( "expected " + string1 + " == " + string2, version1.compareTo( version2 ) == 0 );
		assertTrue( "expected " + string2 + " == " + string1, version2.compareTo( version1 ) == 0 );
		assertTrue( "expected same hashcode for " + string1 + " and " + string2, version1.hashCode() == version2.hashCode() );
		assertTrue( "expected " + string1 + ".equals( " + string2 + " )", version1.equals( version2 ) );
		assertTrue( "expected " + string2 + ".equals( " + string1 + " )", version2.equals( version1 ) );
	}

	private void checkVersionsOrder( String string1, String string2 ) {
		Version version1 = newComparable( string1 );
		Version version2 = newComparable( string2 );
		assertTrue( "expected " + string1 + " < " + string2, version1.compareTo( version2 ) < 0 );
		assertTrue( "expected " + string2 + " > " + string1, version2.compareTo( version1 ) > 0 );
	}

}
