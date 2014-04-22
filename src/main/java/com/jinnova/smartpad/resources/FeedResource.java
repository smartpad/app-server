package com.jinnova.smartpad.resources;

import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jinnova.smartpad.partner.SmartpadCommon;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class FeedResource {
	
    @SuppressWarnings("unused")
	private final String defaultSearchNoFound;

    public FeedResource(String defaultSearchNoFound) {
        this.defaultSearchNoFound = defaultSearchNoFound;
    }
    
    @Path("feeds")
    @GET
    public String getFeed(@QueryParam("verTarget")String verTarget, @QueryParam("verLatest")String verLatest, 
    		@QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {

		return SmartpadCommon.detailManager.drill(null, null, lon, lat);
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
	@Path("{targetType}/{targetId}/drill")
	public String drill(@PathParam("targetType") String targetType, @PathParam("targetId") String targetId,
			@QueryParam("lon") String lon, @QueryParam("lat") String lat) throws SQLException {

		//System.out.println("Similarity for branch " + targetId);
		return SmartpadCommon.detailManager.drill(targetType, targetId, lon, lat);
	}
	
    @GET
    @Path("{targetType}/{relation}/{anchorType}/{anchorId}")
    public String more(@PathParam("targetType") String targetType,
    		@PathParam("relation") String relation,
    		@PathParam("anchorType") String anchorType,
    		@PathParam("anchorId") String anchorId,
    		
    		@QueryParam("branchId") String branchId,
    		@QueryParam("storeId") String storeId,
    		@QueryParam("catId") String catId,
    		@QueryParam("syscatId") String syscatId,
    		@QueryParam("excludeId") String excludeId,
    		
    		@QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {

    	return SmartpadCommon.detailManager.more(targetType, anchorType, anchorId, relation, 
    			branchId, storeId, catId, syscatId, excludeId, lon, lat, offset, size);
    }
    
    /*@Path("feeds")
    @GET
    public String getFeed(@QueryParam("verTarget")String verTarget, @QueryParam("verLatest")String verLatest, 
    		@QueryParam("lon")String lon, @QueryParam("lat")String lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {
    	
    	if (offset < 0) {
    		return new JsonResponse(false, null, "Negative offset: " + offset);
    	}

    	int i = 0;
    	List<Object> feeds = new LinkedList<Object>();
    	feeds.add(new Post(i++));
    	
    	IPartnerManager pm = SmartpadCommon.partnerManager;
    	IUser lotte = pm.login("lotte", "123abc");
    	
    	i = gen(lotte, feeds, pm.getSystemRootCatalog(), i);
    	
		feeds.add(new Branch(lotte.getBranch(), i++));
    	i = gen(lotte, feeds, lotte.getBranch(), i);
		i = gen(lotte, feeds, lotte.getBranch().getRootCatalog(), i);
		
		lotte.getStorePagingList().setPageSize(-1);
		for (IOperation shop : lotte.getStorePagingList().loadPage(lotte, 1).getPageEntries()) {
			feeds.add(new Shop(shop, i++));
	    	i = gen(lotte, feeds, shop, i);
			i = gen(lotte, feeds, shop.getRootCatalog(), i);
		}
		
    	JsonResponse response = new JsonResponse(true);
    	response.put("v", "a");
    	response.put("t", feeds);
    	return response;
    }*/
    
    /*private static int gen(IUser u, List<Object> feeds, IOperation op, int i) throws SQLException {
    	op.getPromotionPagingList().setPageSize(-1);
    	for (IPromotion promo : op.getPromotionPagingList().loadPage(u, 1).getPageEntries()) {
    		feeds.add(new Promotion(promo, i++));
    	}
    	return i;
    }
    
    private static int gen(IUser u, List<Object> feeds, ICatalog cat, int i) throws SQLException {
    	
		//feeds.add(new Catalog(cat, i++));
		
    	if (cat.getCatalogSpec() == null) {
			cat.getCatalogItemPagingList().setPageSize(-1);
			for (ICatalogItem ci : cat.getCatalogItemPagingList().loadPage(u, 1).getPageEntries()) {
				feeds.add(new CatalogItem(ci, i++));
			}
    	}
		
		cat.getSubCatalogPagingList().setPageSize(-1);
		for (ICatalog sub : cat.getSubCatalogPagingList().loadPage(u, 1).getPageEntries()) {
			feeds.add(new Catalog(sub, i++));
			i = gen(u, feeds, sub, i);
		}
    	return i;
    }*/
    
}