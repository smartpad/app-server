package com.jinnova.smartpad.resources;

import static com.jinnova.smartpad.partner.IDetailManager.*;

import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jinnova.smartpad.JsonSupport;

class RenderLinkJob {
	
	private String content;
	
	static final String HOST = "http://localhost:9090/w/" + REST_FEEDS + "/";

	RenderLinkJob(String s) {
		this.content = s;
	}
	
	String render() {
		JsonObject json = new JsonParser().parse(content).getAsJsonObject();
		//putLink(json, "ALN");
		//return "<HTML><body><p>" + new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(json) + "</p><body></HTML>";
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><body>");
		render(buffer, json, null, null);
		buffer.append("</html></body>");
		return buffer.toString();
	}
	
	/*private static void putLink(JsonObject json, String property) {
		String link = json.get(property).getAsString();
		json.addProperty(property, "<a href='" + link + "'>" + link + "</a>");
	}*/
	
	private static void render(StringBuffer buffer, JsonObject json, String feedType, JsonObject feedJson) {
		//renderLink(buffer, json);
		//renderProp(buffer, json, "type");
		//renderProp(buffer, json, "name");
		buffer.append("<table><tbody>");
		String nextFeedType = JsonSupport.getAsString(json, FIELD_TYPE);
		if (nextFeedType != null) {
			feedType = nextFeedType;
			feedJson = json;
		}
		for (Entry<String, JsonElement> entry : json.entrySet()) {
			
			buffer.append("<tr><td>");
			if (entry.getValue().isJsonPrimitive()) {
				buffer.append(entry.getKey() + ": ");
				String value = entry.getValue().getAsString();
				String html = value;
				if (FIELD_ACTION_LOADNEXT.equals(entry.getKey())) {
					html = "<a href='" + HOST + value + "'>" + value + "</a>";
				} else if (FIELD_NAME.equals(entry.getKey())) {
					if (TYPENAME_CATITEM.equals(json.get(FIELD_TYPE).getAsString())) {
						html = "<a href='" + HOST + TYPENAME_CATITEM + "/" + json.get(FIELD_SYSCATID).getAsString() + "/" + 
								json.get(FIELD_ID).getAsString() + "/drill" + "'>" + value + "</a>";
					} else {
						html = "<a href='" + HOST + json.get(FIELD_TYPE).getAsString() + "/" + 
								json.get(FIELD_ID).getAsString() + "/drill" + "'>" + value + "</a>";
					}
				} else if (FIELD_SYSCATNAME.equals(entry.getKey())) {
					html = "<a href='" + HOST + TYPENAME_SYSCAT + "/" + json.get(FIELD_SYSCATID).getAsString() + "/drill" + "'>" + value + "</a>";
				} else if (FIELD_BRANCHNAME.equals(entry.getKey())) {
					html = "<a href='" + HOST + TYPENAME_BRANCH + "/" + json.get(FIELD_BRANCHID).getAsString() + "/drill" + "'>" + value + "</a>";
				} else if (FIELD_CATNAME.equals(entry.getKey())) {
					html = "<a href='" + HOST + TYPENAME_CAT + "/" + json.get(FIELD_CATID).getAsString() + "/drill" + "'>" + value + "</a>";
				} else if (FIELD_UP_NAME.equals(entry.getKey())) {
					html = "<a href='" + HOST + json.get(FIELD_TYPE).getAsString() + "/" + json.get(FIELD_UP_ID).getAsString() + "/drill" + "'>" + value + "</a>";
				} else {
					Renderer r = Renderer.renderers.get(feedType);
					if (r != null) {
						html = r.html(feedJson, json, entry.getKey(), value);
					}
				}
				buffer.append(html);
			} else if (entry.getValue().isJsonArray()) {
				JsonArray ja = entry.getValue().getAsJsonArray();
				buffer.append(entry.getKey() + ": (" + ja.size() + ")");
				for (int i = 0; i < ja.size(); i++) {
					buffer.append("<blockquote>");
					render(buffer, ja.get(i).getAsJsonObject(), feedType, feedJson);
					buffer.append("</blockquote>");
				}
			} else if (entry.getValue() == null || entry.getValue().isJsonNull()) {
				buffer.append(entry.getKey() + ": " + entry.getValue());
			} else {
				render(buffer, entry.getValue().getAsJsonObject(), feedType, feedJson);
			}
			buffer.append("</td></tr>");
		}
		buffer.append("</tbody></table>");
	}
	
	/*private static void renderLink(StringBuffer buffer, JsonObject json) {
		String link = json.get("ALN").getAsString();
		buffer.append("<a href='" + link + "'>" + link + "</a>");
	}
	
	private static void renderProp(StringBuffer buffer, JsonObject json, String property) {
		buffer.append("<p>" + property + ": " + json.get(property).getAsString() + "</p");
	}*/

}
