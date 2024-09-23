module com.parallelsymmetry.utility {
	// Compile time only
	requires static lombok;
	requires static servlet.api;

	// Compile and runtime
	requires java.logging;
	requires java.desktop;
	requires java.prefs;
	requires jdk.management;

	exports com.parallelsymmetry.utility;
	exports com.parallelsymmetry.utility.agent;
	exports com.parallelsymmetry.utility.log;
	exports com.parallelsymmetry.utility.math;
	exports com.parallelsymmetry.utility.product;
	exports com.parallelsymmetry.utility.ui;
	exports com.parallelsymmetry.utility.setting;
	exports com.parallelsymmetry.utility.task;
}
