package com.jinnova.smartpad.resources;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jinnova.smartpad.drilling.DetailManager;
import com.jinnova.smartpad.partner.IDetailManager;

@Path("/test")
@Produces(MediaType.TEXT_HTML + ";charset=utf-8")
public class FeedResourceTest extends FeedResource {

    public FeedResourceTest(String defaultSearchNoFound) {
    	super(defaultSearchNoFound);
    }
    
    @Path("feeds")
    @GET
    public String getFeed(@QueryParam("u")String uid, @QueryParam("verTarget")String verTarget, @QueryParam("verLatest")String verLatest, 
    		@QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {

		//return SmartpadCommon.detailManager.drill(null, null, lon, lat);
		String s = super.getFeed(uid, verTarget, verLatest, lon, lat, offset, size);
		return new RenderLinkJob(s).render();
    }
    
	@GET
	@Path("{targetType}/{targetId}/drill")
	public String drill(@QueryParam("u")String uid, @PathParam("targetType") String targetType, @PathParam("targetId") String targetId,
			@QueryParam("lon") String lon, @QueryParam("lat") String lat) throws SQLException {

		//System.out.println("Similarity for branch " + targetId);
		String s = super.drill(uid, targetType, targetId, lon, lat);
		return new RenderLinkJob(s).render();
	}
	
	@GET
	@Path("citem/{targetType}/{targetId}/drill")
	public String drillCitem(@QueryParam("u")String uid, @PathParam("targetType") String targetType, @PathParam("targetId") String targetId,
			@QueryParam("lon") String lon, @QueryParam("lat") String lat) throws SQLException {

		//System.out.println("Similarity for branch " + targetId);
		String s = new DetailManager().drill(uid, IDetailManager.TYPENAME_CATITEM, targetId, targetType, lon, lat);
		return new RenderLinkJob(s).render();
	}
	
    @GET
    @Path("{targetType}/{relation}/{anchorType}/{anchorId}")
    public String more(
    		@QueryParam("clu")int clusterId, 
    		@PathParam("targetType") String targetType,
    		@PathParam("relation") String relation,
    		@PathParam("anchorType") String anchorType,
    		@PathParam("anchorId") String anchorId,
    		
    		@QueryParam("branchId") String branchId,
    		@QueryParam("storeId") String storeId,
    		@QueryParam("catId") String catId,
    		@QueryParam("syscatId") String syscatId,
    		@QueryParam("excludeId") String excludeId,
    		
    		@QueryParam("recur")boolean recursive, @QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {

    	String s = super.more(clusterId, targetType, relation, anchorType, anchorId, 
    			branchId, storeId, catId, syscatId, excludeId, recursive, lon, lat, offset, size);
		return new RenderLinkJob(s).render();
    }
    
}