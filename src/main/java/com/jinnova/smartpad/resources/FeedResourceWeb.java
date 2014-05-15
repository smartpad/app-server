package com.jinnova.smartpad.resources;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jinnova.smartpad.Feed;
import com.jinnova.smartpad.drilling.DetailManager;
import com.jinnova.smartpad.partner.IDetailManager;

@Path("/" + IDetailManager.REST_FEEDS + "/w")
@Produces(MediaType.TEXT_HTML + ";charset=utf-8")
public class FeedResourceWeb extends FeedResource {

    public FeedResourceWeb(String defaultSearchNoFound) {
    	super(defaultSearchNoFound);
    }
    
    //@Path("feeds")
    @GET
    @Override
    public String getFeed(@QueryParam("u")String uid, @QueryParam("segments") List<String> segments,
    		@QueryParam("verTarget")String verTarget, @QueryParam("verLatest")String verLatest, 
    		@QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {

		//return SmartpadCommon.detailManager.drill(null, null, lon, lat);
		String s = super.getFeed(uid, segments, verTarget, verLatest, lon, lat, offset, size);
		return new RenderLinkJob(s).render();
    }
    
	@GET
	@Path("{targetType}/{targetId}/" + IDetailManager.REST_DRILL)
	@Override
	public String drill(@QueryParam("u")String uid, @PathParam("targetType") String targetType,
			@PathParam("targetId") String targetId, @QueryParam("segments") List<String> segments,
			@QueryParam("lon") String lon, @QueryParam("lat") String lat) throws SQLException {

		//System.out.println("Similarity for branch " + targetId);
		String s = super.drill(uid, targetType, targetId, segments, lon, lat);
		return new RenderLinkJob(s).render();
	}
	
	@GET
	@Path("citem/{targetType}/{targetId}/" + IDetailManager.REST_DRILL)
	@Override
	public String drillCitem(@QueryParam("u")String uid, @PathParam("targetType") String targetType, @PathParam("targetId") String targetId,
			@QueryParam("segments") List<String> segments, @QueryParam("lon") String lon, @QueryParam("lat") String lat) throws SQLException {

		//System.out.println("Similarity for branch " + targetId);
		String s = new DetailManager().drill(uid, IDetailManager.TYPENAME_CATITEM, targetId, targetType, segments, lon, lat);
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
			@QueryParam("segments") List<String> segments,
    		
    		@QueryParam("recur") boolean recursive, @QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("offset") int offset, @QueryParam("size")int size, 
    		@QueryParam("layopts") @DefaultValue("" + Feed.LAYOPT_NONE) int layoutOptions,
    		@QueryParam("laysc") String layoutSyscat) throws SQLException {

    	String s = super.more(clusterId, targetType, relation, anchorType, anchorId, 
    			branchId, storeId, catId, syscatId, excludeId, segments, recursive, lon, lat, offset, size, layoutOptions, layoutSyscat);
		return new RenderLinkJob(s).render();
    }
    
}