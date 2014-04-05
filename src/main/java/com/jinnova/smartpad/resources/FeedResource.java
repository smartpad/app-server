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
import com.jinnova.smartpad.domain.Catalog;
import com.jinnova.smartpad.domain.CatalogItem;
import com.jinnova.smartpad.domain.JsonResponse;
import com.jinnova.smartpad.domain.Shop;
import com.jinnova.smartpad.partner.ICatalog;
import com.jinnova.smartpad.partner.ICatalogItem;
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
    public JsonResponse getFeed(@QueryParam("verTarget")String verTarget, @QueryParam("verLatest")String verLatest, 
    		@QueryParam("lon")int lon, @QueryParam("lat")int lat, 
    		@QueryParam("offset")int offset, @QueryParam("size")int size) throws SQLException {
    	
    	if (offset < 0) {
    		return new JsonResponse(false, null, "Negative offset: " + offset);
    	}
    	
    	List<Object> feeds = new LinkedList<Object>();
    	IPartnerManager pm = SmartpadCommon.getPartnerManager();
    	IUser lotte = pm.login("lotte", "123abc");
    	
    	int i = 0;
    	i = gen(lotte, feeds, pm.getSystemRootCatalog(), i);
    	
		feeds.add(new Branch(lotte.getBranch(), i++));
		i = gen(lotte, feeds, lotte.getBranch().getRootCatalog(), i);
		
		lotte.getStorePagingList().setPageSize(-1);
		for (IOperation shop : lotte.getStorePagingList().loadPage(lotte, 1).getPageItems()) {
			feeds.add(new Shop(shop, i++));
			i = gen(lotte, feeds, shop.getRootCatalog(), i);
		}
		
    	JsonResponse response = new JsonResponse(true);
    	response.put("v", "a");
    	response.put("t", feeds);
    	return response;
    }
    
    private static int gen(IUser u, List<Object> feeds, ICatalog cat, int i) throws SQLException {
    	
		//feeds.add(new Catalog(cat, i++));
		
    	if (cat.getCatalogSpec() == null) {
			cat.getCatalogItemPagingList().setPageSize(-1);
			for (ICatalogItem ci : cat.getCatalogItemPagingList().loadPage(u, 1).getPageItems()) {
				feeds.add(new CatalogItem(ci, i++));
			}
    	}
		
		cat.getSubCatalogPagingList().setPageSize(-1);
		for (ICatalog sub : cat.getSubCatalogPagingList().loadPage(u, 1).getPageItems()) {
			feeds.add(new Catalog(sub, i++));
			i = gen(u, feeds, sub, i);
		}
    	return i;
    }
    
}