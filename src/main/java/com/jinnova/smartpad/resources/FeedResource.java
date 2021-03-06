package com.jinnova.smartpad.resources;

import static com.jinnova.smartpad.partner.IDetailManager.CLUSPRE;

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

@Path("/" + IDetailManager.REST_FEEDS)
@Produces(MediaType.APPLICATION_JSON)
public class FeedResource {
	
	//String linkPrefix = "smartpad://";
	
	private DetailManager detailManager;
	
    @SuppressWarnings("unused")
	private final String defaultSearchNoFound;

    public FeedResource(String defaultSearchNoFound, String linkPrefix) throws SQLException {
        this.defaultSearchNoFound = defaultSearchNoFound;
        detailManager = new DetailManager(linkPrefix);
    }
    
    //@Path("feeds")
    @GET
    public String getFeed(@QueryParam("u")String uid, @QueryParam("segments") List<String> segments,
    		@QueryParam("verTarget")String verTarget, @QueryParam("verLatest")String verLatest, 
    		@QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {

		//return SmartpadCommon.detailManager.drill(null, null, lon, lat);
		return detailManager.drill(uid, IDetailManager.TYPENAME_SYSCAT, 
				/*PartnerManager.instance.getSystemRootCatalog().getId()*/ CLUSPRE, null, segments, lon, lat);
    }
    
    /**
     * Returns in order the following:
     * 
     * 	- All stores belong to this branch in one compound
     * 	- Some similar branches in one compound
     * 	- Some active promotions in one compound
     * 	- All sub categories of this branch's root category in one compound
     * 	- Some posts from this branch 
     * 	- Feature catelog items from this branch's root category
     *
     */
	@GET
	@Path("{targetType}/{targetId}/" + IDetailManager.REST_DRILL)
	public String drill(@QueryParam("u")String uid, @PathParam("targetType") String targetType,
			@PathParam("targetId") String targetId, @QueryParam("segments") List<String> segments,
			@QueryParam("lon") String lon, @QueryParam("lat") String lat) throws SQLException {

		//System.out.println("Similarity for branch " + targetId);
		return detailManager.drill(uid, targetType, targetId, null, segments, lon, lat);
	}
	
	@GET
	@Path("citem/{targetType}/{targetId}/" + IDetailManager.REST_DRILL)
	public String drillCitem(@QueryParam("u")String uid, @PathParam("targetType") String targetType, @PathParam("targetId") String targetId,
			@QueryParam("segments") List<String> segments,
			@QueryParam("lon") String lon, @QueryParam("lat") String lat) throws SQLException {

		//System.out.println("Similarity for branch " + targetId);
		return detailManager.drill(uid, IDetailManager.TYPENAME_CATITEM, targetId, targetType, segments, lon, lat);
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
    		
    		@QueryParam("recur")boolean recursize, @QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size, 
    		@QueryParam("layopts") @DefaultValue("" + Feed.LAYOPT_NONE) int layoutOptions,
    		@QueryParam("exsyscat") String excludeSyscat,
    		@QueryParam("excat") String excludeCat) throws SQLException {

    	return detailManager.more(clusterId, targetType, anchorType, anchorId, relation, 
    			branchId, storeId, catId, syscatId, excludeId, segments, recursize, lon, lat, 
    			offset, size, layoutOptions, excludeSyscat, excludeCat);
    }
    
}