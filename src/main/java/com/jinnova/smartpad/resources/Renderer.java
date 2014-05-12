package com.jinnova.smartpad.resources;

import static com.jinnova.smartpad.partner.IDetailManager.*;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.jinnova.smartpad.partner.CatalogField;
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
		
		if (CatalogField.ATT_GROUPING_VALUE.equals(key)) {
			return "<a href='" + RenderLinkJob.HOST + TYPENAME_SYSCAT + "/" + feedJson.get(FIELD_ID).getAsString() + "/drill" + 
					"?segments=" + json.get(CatalogField.ATT_GROUPING_FIELD).getAsString() + ":" +
					json.get(CatalogField.ATT_GROUPING_VALUEID).getAsString() + "'>" + value + "</a>";
		}
		return value;
	}
	
}