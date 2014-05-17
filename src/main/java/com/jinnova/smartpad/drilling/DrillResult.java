package com.jinnova.smartpad.drilling;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jinnova.smartpad.Feed;
import com.jinnova.smartpad.partner.IDetailManager;

class DrillResult {
	
	private ArrayList<DrillSection> allSections = new ArrayList<>(10);
	
	private LinkedList<Feed> feeds = new LinkedList<Feed>();
	
	//layout options for above feeds
	int layoutOptions = Feed.LAYOPT_NONE;
	
	HashMap<String, Object> layoutParams = new HashMap<>();
	
	private final int clusterId;
	private final BigDecimal lon;
	private final BigDecimal lat;
	
	private final String linkPrefix;
		
	public DrillResult(int clusterId, BigDecimal lon, BigDecimal lat, String linkPrefix) {
		this.clusterId = clusterId;
		this.lon = lon;
		this.lat = lat;
		this.linkPrefix = linkPrefix;
	}
	
	void add(ActionLoad actionLoad) throws SQLException {
		actionLoad.clusterId = this.clusterId;
		actionLoad.gpsLon = this.lon;
		actionLoad.gpsLat = this.lat;
		allSections.add(new DrillSectionSimple(actionLoad, linkPrefix));
	}
	
	void add(String sectionType, ActionLoad load1, ActionLoad load2) throws SQLException {
		load1.clusterId = this.clusterId;
		load1.gpsLon = this.lon;
		load1.gpsLat = this.lat;
		load2.clusterId = this.clusterId;
		load2.gpsLon = this.lon;
		load2.gpsLat = this.lat;
		allSections.add(new DrillSectionTwin(sectionType, 
				new DrillSectionSimple(load1, linkPrefix), new DrillSectionSimple(load2, linkPrefix)));
	}
	
	void add(Feed f) {
		feeds.add(f);
	}
	
	public void writeJson(JsonObject resultJson) {
		
		boolean flatten = false;
		LinkedList<JsonObject> jsonList = new LinkedList<>();
		for (int i = allSections.size() - 1; i >= 0; i--) {
			DrillSection oneSection = allSections.get(i);
			if (!flatten) {
				flatten = oneSection.copyTo(jsonList);
				if (flatten) {
					if (oneSection instanceof DrillSectionSimple) {
						String nextUrl = ((DrillSectionSimple) oneSection).actionLoad.generateNextLoadUrl();
						if (nextUrl != null) {
							resultJson.addProperty(IDetailManager.FIELD_ACTION_LOADNEXT, linkPrefix + "/" + nextUrl);
						}
					} else {
						String nextUrl = ((DrillSectionTwin) oneSection).flatenSection.actionLoad.generateNextLoadUrl();
						if (nextUrl != null) {
							resultJson.addProperty(IDetailManager.FIELD_ACTION_LOADNEXT, linkPrefix + "/" + nextUrl);
						}
					}
				}
			} else if (oneSection instanceof DrillSectionSimple && ((DrillSectionSimple) oneSection).isForcedFlatten()) {
				oneSection.copyTo(jsonList);
			} else {
				JsonObject oneJson = oneSection.getJson();
				if (oneJson != null) {
					jsonList.add(oneJson);
				}
			}
		}
		
		layoutParams.put(Feed.LAYOUT_PARAM_LINKPREFIX, linkPrefix);
		for (Feed f : feeds) {
			jsonList.add(f.generateFeedJson(layoutOptions, layoutParams));
		}
		
		JsonArray ja = new JsonArray();
		for (int i = jsonList.size() - 1; i >= 0; i--) {
			ja.add(jsonList.get(i));
		}
		resultJson.add(IDetailManager.FIELD_ARRAY, ja);
	}
}

interface DrillSection {
	
	boolean copyTo(LinkedList<JsonObject> jsonList);
	
	JsonObject getJson();
}

class DrillSectionSimple implements DrillSection {
	
	//private String sectionType;
	
	Object[] ja;
	int expectedSize;
	//private int layoutOptions = Feed.LAYOPT_NONE;
	
	ActionLoad actionLoad;
	
	private boolean forcedFlatten = false;
	
	private final String linkPrefix;
	
	DrillSectionSimple(String sectionType, Object[] ja, int expectedSize, ActionLoad load, String linkPrefix) {
		//this.sectionType = sectionType;
		this.ja = ja;
		this.expectedSize = expectedSize;
		this.actionLoad = load;
		this.linkPrefix = linkPrefix;
	}
	
	DrillSectionSimple(ActionLoad load, String linkPrefix) throws SQLException {
		//this.sectionType = load.targetType;
		//this.sectionType = IDetailManager.TYPENAME_COMPOUND;
		this.ja = load.loadFirstEntries();
		this.expectedSize = load.getInitialDrillSize();
		this.actionLoad = load;
		this.linkPrefix = linkPrefix;
	}
	
	DrillSectionSimple forceFlatten() {
		this.forcedFlatten = true;
		return this;
	}
	
	boolean isForcedFlatten() {
		return forcedFlatten;
	}
	
	@Override
	public JsonObject getJson() {
		
		if (ja == null || ja.length == 0) {
			return null;
		}
		
		int layOpts = actionLoad.getLayopts();
		//String layoutSyscat = actionLoad.getLayoutSyscat();
		actionLoad.linkPrefix = linkPrefix;
		HashMap<String, Object> layoutParams = actionLoad.getLayoutParams();
		if (ja.length == 1) {
			return ((Feed) ja[0]).generateFeedJson(layOpts, layoutParams);
		}
		
		JsonArray array = new JsonArray();
		int actualCount = Math.min(expectedSize, ja.length);
		for (int i = 0; i < actualCount; i++) {
			array.add(((Feed) ja[i]).generateFeedJson(layOpts, layoutParams));
		}
		
		JsonObject json = new JsonObject();
		//json.addProperty(IDetailManager.FIELD_TYPE, sectionType);
		json.addProperty(IDetailManager.FIELD_TYPE, IDetailManager.TYPENAME_COMPOUND);
		json.addProperty(IDetailManager.FIELD_TYPENUM, IDetailManager.TYPE_COMPOUND);
		json.add(IDetailManager.FIELD_ARRAY, array);
		if (ja.length >= expectedSize && actionLoad != null) {
			actionLoad.setOffset(actualCount);
			json.addProperty(IDetailManager.FIELD_ACTION_LOADNEXT, linkPrefix + "/" + actionLoad.generateNextLoadUrl());
		}
		System.out.println("next load: " + actionLoad.generateNextLoadUrl());
		return json;
	}
	
	boolean isEmpty() {
		return ja == null || ja.length == 0;
	}
	
	@Override
	public boolean copyTo(LinkedList<JsonObject> jsonList) {

		if (ja == null) {
			return false;
		}
		
		boolean copied = false;
		int actualCount = Math.min(expectedSize, ja.length);
		int layOpts = actionLoad.getLayopts();
		//String layoutSyscat = actionLoad.getLayoutSyscat();
		actionLoad.linkPrefix = linkPrefix;
		HashMap<String, Object> layoutParams = actionLoad.getLayoutParams();
		for (int i = 0; i < actualCount; i++) {
			jsonList.add(((Feed) ja[i]).generateFeedJson(layOpts, layoutParams));
			copied = true;
		}
		actionLoad.setOffset(actualCount);
		return copied;
	}
}

class DrillSectionTwin implements DrillSection {
	
	String sectionType;
	DrillSectionSimple section1, section2;
	
	DrillSectionSimple flatenSection;
	
	DrillSectionTwin(String sectionType, DrillSectionSimple section1, DrillSectionSimple section2) {
		this.sectionType = sectionType;
		this.section1 = section1;
		this.section2 = section2;
	}
	
	@Override
	public boolean copyTo(LinkedList<JsonObject> jsonList) {
		
		//flatten if needed
		if (section1.isEmpty()) {
			section2.expectedSize = section2.ja.length;
			flatenSection = section2;
			return section2.copyTo(jsonList);
		}
		if (section2.isEmpty()) {
			section1.expectedSize = section1.ja.length;
			flatenSection = section1;
			return section1.copyTo(jsonList);
		}
		
		//adjust sizes
		if (section1.ja.length < section1.expectedSize) {
			section2.expectedSize += section1.expectedSize - section1.ja.length;
		} else if (section2.ja.length < section2.expectedSize) {
			section1.expectedSize += section2.expectedSize - section2.ja.length;
		}
		
		boolean copied = section1.copyTo(jsonList);
		if (copied) {
			flatenSection = section1;
		}
		if (section2.copyTo(jsonList)) {
			flatenSection = section2;
			copied = true;
		}
		
		return copied;
		
	}
	
	@Override
	public JsonObject getJson() {
		
		LinkedList<JsonObject> jsonList = new LinkedList<>();
		if (!copyTo(jsonList)) {
			return null;
		}
		
		if (jsonList.size() == 1) {
			//flatten
			return jsonList.get(0);
		}
		
		JsonObject json = new JsonObject();
		json.addProperty(IDetailManager.FIELD_TYPE, sectionType);
		json.addProperty(IDetailManager.FIELD_TYPENUM, IDetailManager.TYPE_COMPOUND);
		
		JsonArray ja = new JsonArray();
		for (JsonObject o : jsonList) {
			ja.add(o);
		}
		json.add(IDetailManager.FIELD_ARRAY, ja);
		return json;
	}
}