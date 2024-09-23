module com.parallelsymmetry.utility {
	// Compile time only
	requires static lombok;

	// Compile and runtime
	requires java.logging;
	requires java.desktop;
	requires java.prefs;
	requires servlet.api;
	requires jdk.management;

	exports com.parallelsymmetry.utility;
	exports com.parallelsymmetry.utility.agent;
	exports com.parallelsymmetry.utility.log;
}
