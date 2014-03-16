package com.jinnova.smartpad.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jinnova.smartpad.domain.JsonResponse;
import com.jinnova.smartpad.domain.UserActivity;

@Path("/activity")
@Produces(MediaType.APPLICATION_JSON)
public class ActivityResource {

    @POST
    //@Path("/{activity}")
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonResponse activity(UserActivity activity) { 
    	try {
    		activity.action();
    		return new JsonResponse(true, "Action complete for user: " + activity.getUserId() + " action: " + activity.getAction().toString());
    	} catch (Exception ex) {
    		return new JsonResponse(false, ex.getMessage());
    	}
    }
}