package com.jinnova.smartpad.domain;

public class Feed {
	
	private final String id;
	private String version;
	private int lon;
	private int lat;
	
	public Feed(String id, String version, int lon, int lat) {
		this.id = id;
		this.version = version;
		this.lon = lon;
		this.lat = lat;
	}

	public String getId() {
		return this.id;
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