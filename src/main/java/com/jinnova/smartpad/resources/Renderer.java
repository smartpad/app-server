package com.jinnova.smartpad.resources;

import java.util.HashMap;

import com.google.gson.JsonObject;
import com.jinnova.smartpad.partner.IDetailManager;

abstract class Renderer {
	
	static HashMap<String, Renderer> renderers = new HashMap<>();
	
	static {
		renderers.put(IDetailManager.TYPENAME_SYSCAT, new RendererSyscat());
	}

	abstract String html(JsonObject feedJson, String key, String value);
}


class RendererSyscat extends Renderer {

	@Override
	String html(JsonObject feedJson, String key, String value) {
		
		//if (CatalogField.ATT_GROUPING_VALUE.equals(key))
		return value;
	}
	
}