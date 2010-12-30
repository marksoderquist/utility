package com.parallelsymmetry.escape.utility;

import junit.framework.TestCase;

public class VersionTest extends TestCase {

	private Version version = Version.parse( "1.2.3-b-4" );

	private Version majorGreater = Version.parse( "2.2.3-b-4" );

	private Version majorLesser = Version.parse( "0.2.3-b-4" );

	private Version minorGreater = Version.parse( "1.3.3-b-4" );

	private Version minorLesser = Version.parse( "1.1.3-b-4" );

	private Version microGreater = Version.parse( "1.2.4-b-4" );

	private Version microLesser = Version.parse( "1.2.2-b-4" );

	private Version revisionGreater = Version.parse( "1.2.3-u-4" );

	private Version revisionLesser = Version.parse( "1.2.3-a-4" );

	private Version buildGreater = Version.parse( "1.2.3-b-5" );

	private Version buildLesser = Version.parse( "1.2.3-b-3" );

	private Version alpha = Version.parse( "1.2.3-a-4" );

	private Version beta = Version.parse( "1.2.3-b-4" );

	private Version update = Version.parse( "1.2.3-u-4" );

	private Version snapshot = Version.parse( "1.2.3-SNAPSHOT" );

	public void testParseInvalidVersions() {
		assertNull( Version.parse( null ) );
		assertNull( Version.parse( "" ) );
		assertNull( Version.parse( "1" ) );
		assertNull( Version.parse( "1.2" ) );
		assertNull( Version.parse( "1.2.3" ) );
		assertNull( Version.parse( "1.2.3.4" ) );
	}

	public void testGetVersion() {
		assertEquals( "Version number incorrect.", "1.2.3-a-4", alpha.getVersion() );
		assertEquals( "Version number incorrect.", "1.2.3-b-4", beta.getVersion() );
		assertEquals( "Version number incorrect.", "1.2.3-u-4", update.getVersion() );
		assertEquals( "Version number incorrect.", "1.2.3-SNAPSHOT", snapshot.getVersion() );
	}

	public void testToHumanString() {
		assertEquals( "Version number incorrect.", "1.2.3 Alpha 4", alpha.toHumanString() );
		assertEquals( "Version number incorrect.", "1.2.3 Beta 4", beta.toHumanString() );
		assertEquals( "Version number incorrect.", "1.2.3 Update 4", update.toHumanString() );
		assertEquals( "Version number incorrect.", "1.2.3 Snapshot", snapshot.toHumanString() );
	}

	public void testToString() {
		assertEquals( "Version number incorrect", "1.2.3 Alpha 4", alpha.toString() );
		assertEquals( "Version number incorrect", "1.2.3 Beta 4", beta.toString() );
		assertEquals( "Version number incorrect", "1.2.3 Update 4", update.toString() );
		assertEquals( "Version number incorrect", "1.2.3 Snapshot", snapshot.toString() );
	}

	public void testGetVersionWithLimit() {
		assertEquals( "Version number incorrect", "1", version.getVersion( 1 ) );
		assertEquals( "Version number incorrect", "1.2", version.getVersion( 2 ) );
		assertEquals( "Version number incorrect", "1.2.3", version.getVersion( 3 ) );
		assertEquals( "Version number incorrect", "1.2.3-b", version.getVersion( 4 ) );
		assertEquals( "Version number incorrect", "1.2.3-b-4", version.getVersion( 5 ) );
	}

	public void testGetSnapshotVersionWithLimit() {
		assertEquals( "Version number incorrect", "1", snapshot.getVersion( 1 ) );
		assertEquals( "Version number incorrect", "1.2", snapshot.getVersion( 2 ) );
		assertEquals( "Version number incorrect", "1.2.3", snapshot.getVersion( 3 ) );
		assertEquals( "Version number incorrect", "1.2.3-SNAPSHOT", snapshot.getVersion( 4 ) );
		assertEquals( "Version number incorrect", "1.2.3-SNAPSHOT", snapshot.getVersion( 5 ) );
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
		assertEquals( "Compare equal versions", -1, Version.compareVersions( version, snapshot ) );
		assertEquals( "Compare equal versions", 1, Version.compareVersions( snapshot, version ) );
	}

	public void testCompareVersions_1() {
		assertEquals( "Compare greater major", -1, Version.compareVersions( version, majorGreater, 1 ) );
		assertEquals( "Compare lesser major", 1, Version.compareVersions( version, majorLesser, 1 ) );
		assertEquals( "Compare greater minor", 0, Version.compareVersions( version, minorGreater, 1 ) );
		assertEquals( "Compare lesser minor", 0, Version.compareVersions( version, minorLesser, 1 ) );
	}

	public void testCompareVersions_2() {
		assertEquals( "Compare greater minor", -1, Version.compareVersions( version, minorGreater, 2 ) );
		assertEquals( "Compare lesser minor", 1, Version.compareVersions( version, minorLesser, 2 ) );
		assertEquals( "Compare greater micro", 0, Version.compareVersions( version, microGreater, 2 ) );
		assertEquals( "Compare lesser micro", 0, Version.compareVersions( version, microLesser, 2 ) );
	}

	public void testCompareVersions_3() {
		assertEquals( "Compare greater micro", -1, Version.compareVersions( version, microGreater, 3 ) );
		assertEquals( "Compare lesser micro", 1, Version.compareVersions( version, microLesser, 3 ) );
		assertEquals( "Compare greater revision", 0, Version.compareVersions( version, revisionGreater, 3 ) );
		assertEquals( "Compare lesser revision", 0, Version.compareVersions( version, revisionLesser, 3 ) );
	}

	public void testCompareVersions_4() {
		assertEquals( "Compare greater revision", -1, Version.compareVersions( version, revisionGreater, 4 ) );
		assertEquals( "Compare lesser revision", 1, Version.compareVersions( version, revisionLesser, 4 ) );
		assertEquals( "Compare greater build", 0, Version.compareVersions( version, buildGreater, 4 ) );
		assertEquals( "Compare lesser build", 0, Version.compareVersions( version, buildLesser, 4 ) );
	}

	public void testCompareVersions_5() {
		assertEquals( "Compare greater build", -1, Version.compareVersions( version, buildGreater, 5 ) );
		assertEquals( "Compare lesser build", 1, Version.compareVersions( version, buildLesser, 5 ) );
		assertEquals( "Compare equal build", 0, Version.compareVersions( version, version, 5 ) );
	}

}
