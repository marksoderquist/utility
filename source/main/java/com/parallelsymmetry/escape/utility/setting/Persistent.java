package com.parallelsymmetry.escape.utility.setting;

public interface Persistent<T> {

	T loadSettings( Settings settings );

	T saveSettings( Settings settings );

}
