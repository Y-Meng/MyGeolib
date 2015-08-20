package com.mcy.geolib;

public class Mgeo {

	static{
		System.loadLibrary("mgeo");
	}
	
	public Mgeo(){}
	
	public native void log(String str);
	public native String getString();
}
