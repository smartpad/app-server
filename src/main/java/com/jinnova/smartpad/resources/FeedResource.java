package com.jinnova.smartpad.resources;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jinnova.smartpad.domain.Feed;
import com.jinnova.smartpad.domain.Item;
import com.jinnova.smartpad.domain.JsonResponse;

@Path("/feeds")
@Produces(MediaType.APPLICATION_JSON)
public class FeedResource {
    private final String defaultSearchNoFound;

    public FeedResource(String defaultSearchNoFound) {
        this.defaultSearchNoFound = defaultSearchNoFound;
    }
    
    @GET
    public JsonResponse getFeed(@QueryParam("version")String version, @QueryParam("lon")int lon,
    		@QueryParam("lat")int lat, @QueryParam("offset")int offset, @QueryParam("size")int size) {
    	if (offset < 0 || offset >= size) {
    		return new JsonResponse(false, null, defaultSearchNoFound);
    	}
    	List<Item<Feed>> result = new LinkedList<Item<Feed>>();
    	for (int i = offset; i < size; i++) {
    		Item<Feed> item = new Item<Feed>(i, new Feed(version, lon, lat));
    		result.add(item);
    	}
    	return new JsonResponse(true, result);
    }
}