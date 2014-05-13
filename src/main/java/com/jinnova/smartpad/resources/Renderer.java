package com.jinnova.smartpad.resources;

import static com.jinnova.smartpad.partner.IDetailManager.*;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.jinnova.smartpad.partner.IDetailManager;

abstract class Renderer {
	
	static HashMap<String, Renderer> renderers = new HashMap<>();
	
	static {
		renderers.put(IDetailManager.TYPENAME_SYSCAT, new RendererSyscat());
	}

	abstract String html(JsonObject feedJson, JsonObject json, String key, String value);
}


class RendererSyscat extends Renderer {

	@Override
	String html(JsonObject feedJson, JsonObject json, String key, String value) {
		
		if (FIELD_SEGMENT_VALUE.equals(key)) {
			//String segmentFieldId = feedJson.get(FIELD_SEGMENT).getAsJsonObject().get(FIELD_SEGMENT_FIELDID).getAsString();
			/*return "<a href='" + RenderLinkJob.HOST + TYPENAME_SYSCAT + "/" + feedJson.get(FIELD_ID).getAsString() + "/drill" + 
					"?segments=" + json.get(FIELD_SEGMENT_FIELDID).getAsString() + ":" +
					json.get(FIELD_SEGMENT_VALUEID).getAsString() + "'>" + value + "</a>";*/
			return "<a href='/w" + json.get(FIELD_SEGMENT_LINK).getAsString() + "'>" + value + "</a>";
		} else if (FIELD_SEGMENT_LINK.equals(key)) {
			return "";
		}
		return value;
	}
	
}