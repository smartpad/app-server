package com.jinnova.smartpad.domain;


public class Feed {
	
	private String version;
	private int lon;
	private int lat;
	
	public Feed(String version, int lon, int lat) {
		this.version = version;
		this.lon = lon;
		this.lat = lat;
	}

	public String getVersion() {
		return version;
	}

	public int getLon() {
		return lon;
	}

	public int getLat() {
		return lat;
	}

}