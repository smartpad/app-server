package com.jinnova.smartpad.resources;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jinnova.smartpad.domain.Branch;
import com.jinnova.smartpad.domain.JsonResponse;
import com.jinnova.smartpad.partner.IOperation;
import com.jinnova.smartpad.partner.IPartnerManager;
import com.jinnova.smartpad.partner.IUser;
import com.jinnova.smartpad.partner.SmartpadCommon;

@Path("/feeds")
@Produces(MediaType.APPLICATION_JSON)
public class FeedResource {
	
    @SuppressWarnings("unused")
	private final String defaultSearchNoFound;

    public FeedResource(String defaultSearchNoFound) {
        this.defaultSearchNoFound = defaultSearchNoFound;
    }
    
    @GET
    public JsonResponse getFeed(@QueryParam("version")String version, @QueryParam("lon")int lon,
    		@QueryParam("lat")int lat, @QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {
    	
    	if (offset < 0) {
    		return new JsonResponse(false, null, "Negative offset: " + offset);
    	}
    	
    	List<Object> feeds = new LinkedList<Object>();
    	/*for (int i = offset; i < size; i++) {
    		Feed item = new Feed(SmartpadCommon.md5(String.valueOf(i)), i);
    		feeds.add(item);
    	}*/
    	IPartnerManager pm = SmartpadCommon.getPartnerManager();
    	IUser lotte = pm.login("lotte", "123abc");
    	IOperation branch = lotte.getBranch();
    	feeds.add(new Branch(branch));
    	JsonResponse response = new JsonResponse(true);
    	response.put("ver", "a");
    	response.put("feeds", feeds);
    	return response;
    }
    
}