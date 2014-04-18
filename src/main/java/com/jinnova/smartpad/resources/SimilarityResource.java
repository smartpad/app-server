package com.jinnova.smartpad.resources;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jinnova.smartpad.domain.JsonResponse;
import com.jinnova.smartpad.domain.Shop;
import com.jinnova.smartpad.partner.IDetailManager;
import com.jinnova.smartpad.partner.IOperation;
import com.jinnova.smartpad.partner.IPartnerManager;
import com.jinnova.smartpad.partner.IUser;
import com.jinnova.smartpad.partner.SmartpadCommon;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class SimilarityResource {
    
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
    @Path("branch/{targetId}/drill")
    public String getBranchSimilars(@PathParam("targetId") String targetId,
    		@QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("page")List<Integer> page, @QueryParam("size") int size) throws SQLException {
    	
    	System.out.println("Similarity for branch " + targetId);
    	//return DBQuery.query("branch", targetId, page);
    	return SmartpadCommon.detailManager.getDetail(IDetailManager.TYPE_BRANCH, targetId, lon, lat, page.get(0), size);
    	/*if (offset < 0) {
    		return new JsonResponse(false, null, "Negative offset: " + offset);
    	}

    	List<Object> feeds = new LinkedList<Object>();
    	IPartnerManager pm = SmartpadCommon.getPartnerManager();
    	IUser lotte = pm.login("lotte", "123abc");
    	int i = 0;
    	FeedResource.gen(lotte, feeds, lotte.getBranch(), i);
    	
    	JsonResponse response = new JsonResponse(true);
    	response.put("v", "a");
    	response.put("t", feeds);
    	return response;*/
    }
    
    @GET
    @Path("store/{targetId}/drill")
    public String getStoreSimiliars(@PathParam("targetId") String targetId,
    		@QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("page")int page, @QueryParam("size") int size) throws SQLException {
    	
    	System.out.println("Similarity for store " + targetId);
    	return SmartpadCommon.detailManager.getDetail(IDetailManager.TYPE_STORE, targetId, lon, lat, page, size);
    	/*if (offset < 0) {
    		return new JsonResponse(false, null, "Negative offset: " + offset);
    	}

    	List<Object> feeds = new LinkedList<Object>();
    	IPartnerManager pm = SmartpadCommon.partnerManager;
    	IUser lotte = pm.login("lotte", "123abc");
    	int i = 0;
    	FeedResource.gen(lotte, feeds, lotte.getBranch(), i);
    	
    	JsonResponse response = new JsonResponse(true);
    	response.put("v", "a");
    	response.put("t", feeds);
    	return response;*/
    }
    
    @GET
    @Path("cat/{targetId}/drill")
    public JsonResponse getCatalogSimiliars(@PathParam("targetId") String targetId,
    		@QueryParam("lon")int lon, @QueryParam("lat")int lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {
    	
    	System.out.println("Similarity for branch " + targetId);
    	if (offset < 0) {
    		return new JsonResponse(false, null, "Negative offset: " + offset);
    	}

    	List<Object> feeds = new LinkedList<Object>();
    	IPartnerManager pm = SmartpadCommon.partnerManager;
    	IUser lotte = pm.login("lotte", "123abc");
    	int i = 0;
    	FeedResource.gen(lotte, feeds, lotte.getBranch().getRootCatalog(), i);
    	
    	JsonResponse response = new JsonResponse(true);
    	response.put("v", "a");
    	response.put("t", feeds);
    	return response;
    }
    
    @GET
    @Path("citem/{targetId}/drill")
    public JsonResponse getCatalogItemSimiliars(@PathParam("targetId") String targetId,
    		@QueryParam("lon")int lon, @QueryParam("lat")int lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {
    	
    	System.out.println("Similarity for branch " + targetId);
    	if (offset < 0) {
    		return new JsonResponse(false, null, "Negative offset: " + offset);
    	}

    	List<Object> feeds = new LinkedList<Object>();
    	IPartnerManager pm = SmartpadCommon.partnerManager;
    	IUser lotte = pm.login("lotte", "123abc");
    	int i = 0;
    	for (IOperation store : lotte.getStorePagingList().loadPage(lotte, 1).getPageEntries()) {
    		feeds.add(new Shop(store, i++));
    	}
    	
    	JsonResponse response = new JsonResponse(true);
    	response.put("v", "a");
    	response.put("t", feeds);
    	return response;
    }
    
    @GET
    @Path("pro/{targetId}/drill")
    public JsonResponse getFeed(@PathParam("targetId") String targetId,
    		@QueryParam("lon")int lon, @QueryParam("lat")int lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {
    	
    	System.out.println("Similarity for promotion " + targetId);
    	if (offset < 0) {
    		return new JsonResponse(false, null, "Negative offset: " + offset);
    	}

    	List<Object> feeds = new LinkedList<Object>();
    	IPartnerManager pm = SmartpadCommon.partnerManager;
    	IUser lotte = pm.login("lotte", "123abc");
    	int i = 0;
    	FeedResource.gen(lotte, feeds, pm.getSystemRootCatalog(), i);
    	
    	JsonResponse response = new JsonResponse(true);
    	response.put("v", "a");
    	response.put("t", feeds);
    	return response;
    }

}
