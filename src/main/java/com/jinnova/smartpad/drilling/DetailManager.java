package com.jinnova.smartpad.drilling;

import static com.jinnova.smartpad.Feed.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jinnova.smartpad.Feed;
import com.jinnova.smartpad.db.CacheDao;
import com.jinnova.smartpad.db.CatalogDao;
import com.jinnova.smartpad.db.CatalogItemDao;
import com.jinnova.smartpad.db.OperationDao;
import com.jinnova.smartpad.db.PromotionDao;
import com.jinnova.smartpad.partner.Catalog;
import com.jinnova.smartpad.partner.CatalogItem;
import com.jinnova.smartpad.partner.IDetailManager;
import com.jinnova.smartpad.partner.Operation;
import com.jinnova.smartpad.partner.PartnerManager;
import com.jinnova.smartpad.partner.Promotion;
import com.jinnova.smartpad.partner.SmartpadConnectionPool;

public class DetailManager implements IDetailManager {
	
	private static final boolean RECURSIVE = true;
	private static final boolean DIRECT = false;
	
	private DetailDriller[] drillers = new DetailDriller[TYPE_COUNT];
	
	private static HashMap<String, Integer> clusterMap = new HashMap<>();
	
	private final String linkPrefix;
	
	public DetailManager(String linkPrefix) throws SQLException {
		this.linkPrefix = linkPrefix;
		initialize();
	}
    
	@Override
    public String drill(String uid, String targetType, String targetId, String targetSyscat, List<String> segments, 
    		String gpsLon, String gpsLat/*, int page, int size*/) throws SQLException {
    	
		int targetTypeNumber = typeNameToNumber(targetType);
		String gpsZone = findGpsZone(gpsLon, gpsLat);
		String cached = CacheDao.query(targetTypeNumber, targetId, gpsZone/*, page*/);
    	if (cached != null) {
    		return cached;
    	}
    	
    	BigDecimal lon;
    	if (gpsLon != null) {
    		lon = new BigDecimal(gpsLon);
    	} else {
    		lon = null;
    	}
    	BigDecimal lat;
    	if (gpsLat != null) {
    		lat = new BigDecimal(gpsLat);
    	} else {
    		lat = null;
    	}
    	Integer clusterId = clusterMap.get(uid);
    	if (clusterId == null) {
    		clusterId = 1;
    	}
    	DrillResult dr = drillers[targetTypeNumber].drill(clusterId, targetId, targetSyscat, segments, lon, lat/*, page, size*/);
    	JsonObject dataJson = new JsonObject();
    	dr.writeJson(dataJson);
    	dataJson.addProperty(FIELD_VERSION, "a");
    	//json.addProperty("page", page);
    	//json.addProperty("size", size);
    	
    	JsonObject resultJson = new JsonObject();
    	resultJson.add(VERSIONING_TARGET, dataJson);
    	cached = resultJson.toString();
    	CacheDao.put(cached, targetTypeNumber, targetId, gpsZone/*, page*/);
    	return cached;
    }
	
	private String findGpsZone(String gpsLon, String gpsLat) {
		return null;
	}

	@Override
	public String more(int clusterId, String targetType, String anchorType, String anchorId, String relation,
			String branchId, String storeId, String catId, String syscatId, String excludeId, List<String> segments,
			boolean recursive, String gpsLon, String gpsLat, int offset, int size, 
			int layoutOptions, String excludeSyscat, String excludeCat) throws SQLException {
		
		ActionLoad action = ActionLoad.createLoad(targetType, anchorType, relation);
		action.clusterId = clusterId;
		action.anchorId = anchorId;
		action.excludeId = excludeId;
		action.syscatId = syscatId;
		action.offset = offset;
		action.pageSize = size;
		action.recursive = recursive;
		action.segments = segments;
		//action.layoutParams.put(Feed.LAYOUT_PARAM_SEGMENTS, segments);
		action.layopts(layoutOptions);
		action.excludeSyscat = excludeSyscat;
		action.excludeCat = excludeCat;
		if (gpsLon != null) {
			action.gpsLon = new BigDecimal(gpsLon);
		}
		if (gpsLat != null) {
			action.gpsLat = new BigDecimal(gpsLat);
		}
		
		Object[] data = action.load();
		//action.layoutParams.put(LAYOUT_PARAM_LINKPREFIX, linkPrefix);
		action.linkPrefix = linkPrefix;
		JsonArray array = new JsonArray();
		HashMap<String, Object> layoutParams = action.getLayoutParams();
		for (int i = 0; i < data.length; i++) {
			array.add(((Feed) data[i]).generateFeedJson(layoutOptions, layoutParams));
		}
		
		JsonObject dataJson = new JsonObject();
		dataJson.add(FIELD_ARRAY, array);
		String nextUrl = action.generateNextLoadUrl();
		if (nextUrl != null) {
			dataJson.addProperty(FIELD_ACTION_LOADNEXT, linkPrefix + "/" + nextUrl);
		}
		//System.out.println("next load: " + actionLoad.generateNextLoadUrl());
    	JsonObject resultJson = new JsonObject();
    	resultJson.add(VERSIONING_TARGET, dataJson);
    	//return new GsonBuilder().create().toJson(resultJson);
		return resultJson.toString();
	}
	
	private DrillResult createDefaultDrills(int clusterId, BigDecimal lon, BigDecimal lat) {
		
		//order statuses
		DrillResult dr = new DrillResult(clusterId, lon, lat, linkPrefix);
		
		//promotion alerts for a specific syscat
		
		return dr;
	}
	
	private static void createSyscatAlerts(DrillResult dr, String syscatId) {
		
		//promotion alerts for a specific syscat
	}
	
	private static void createBranchAlerts(DrillResult dr, String syscatId) {
		
		//promotion alerts for a specific syscat
	}
	
	public void initialize() throws SQLException {
		
		ActionLoad.initialize();
		loadAllConsumers();
		
		drillers[TYPE_SYSCAT] = new DetailDriller() {
			
			@Override
			public DrillResult drill(int clusterId, String syscatId, String targetSyscat,  
					List<String> segments, BigDecimal lon, BigDecimal lat) throws SQLException {
				
				DrillResult dr = createDefaultDrills(clusterId, lon, lat);
				createSyscatAlerts(dr, syscatId);
				
				if (!IDetailManager.SYSTEM_CAT_ALL.equals(syscatId)) {
					Catalog cat = (Catalog) PartnerManager.instance.getSystemCatalog(syscatId);
					dr.add(cat);
					dr.layoutOptions = LAYOPT_WITHPARENT | LAYOPT_WITHSEGMENTS | LAYOPT_WITHSEGMENTS_REMOVER;
					dr.layoutParams.put(Feed.LAYOUT_PARAM_SEGMENTS, segments);
				}
				
				dr.add(new ALBranchesBelongToSyscat(syscatId, null, RECURSIVE, 5, 5, 5)
					.layopts(LAYOPT_WITHSYSCAT | LAYOPT_NAMELINK).unshownSyscat(syscatId));
				
				dr.add(new ALCatalogsBelongToCatalog(syscatId, null, DIRECT, 5, 5, 5).layopts(LAYOPT_NAMELINK));
				
				dr.add(new ALItemBelongToSyscat(syscatId, segments, RECURSIVE, 10, 10, 10)
					.layopts(LAYOPT_WITHBRANCH | LAYOPT_WITHSYSCAT)
					.unshownSyscat(syscatId));
				return dr;
			}
		};
		drillers[TYPE_BRANCH] = new DetailDriller() {
			
			@Override
			public DrillResult drill(int clusterId, String branchId, String targetSyscat, 
					 List<String> segments, BigDecimal lon, BigDecimal lat/*, int page, int size*/) throws SQLException {
				
				OperationDao odao = new OperationDao();
				Operation branch = odao.loadBranch(branchId);
				String syscatId = branch.getSyscatId();
				DrillResult dr = createDefaultDrills(clusterId, lon, lat);
				createSyscatAlerts(dr, syscatId);
				
				//details of this branch
				dr.add(branch);
				dr.layoutOptions = LAYOPT_WITHSYSCAT | LAYOPT_WITHDETAILS;

				//At most 5 stores belong to this branch and 3 similar branches
				dr.add(TYPENAME_COMPOUND_BRANCHSTORE, 
						new ALStoresBelongToBranch(branchId, null, 10, 8, 5).layopts(LAYOPT_NAMELINK | LAYOPT_STORE), 
						new ALBranchesBelongToSyscat(syscatId, branchId, DIRECT, 10, 8, 3).layopts(LAYOPT_NAMELINK));
				
				//5 active promotions by syscat, this branch first 
				dr.add(new ALPromotionsBelongToSyscat(syscatId, null, branchId, DIRECT, clusterId, 10, 5, 5));
				
				//10 sub categories of this branch's root category in one compound
				dr.add(new ALCatalogsBelongToCatalog(branchId, null, DIRECT, 10, 10, 10).layopts(LAYOPT_NAMELINK | LAYOPT_PRIVATECAT));
				
				//catelog items from this branch's root category
				dr.add(new ALItemBelongToCatalog(branchId, syscatId, null, RECURSIVE, 20, 20, 20).layopts(Feed.LAYOPT_WITHCAT));
				return dr;
			}
		};
		drillers[TYPE_STORE] = new DetailDriller() {

			@Override
			public DrillResult drill(int clusterId, String targetId, String targetSyscat, 
					 List<String> segments, BigDecimal lon, BigDecimal lat/*, int page, int size*/) throws SQLException {
				
				Operation targetStore = new OperationDao().loadStore(targetId);
				DrillResult dr = createDefaultDrills(clusterId, lon, lat);
				createSyscatAlerts(dr, targetStore.getSyscatId());
				
				dr.add(targetStore);
				dr.layoutOptions = LAYOPT_WITHBRANCH | LAYOPT_WITHSYSCAT | LAYOPT_STORE | LAYOPT_WITHDETAILS;
				
				//5 other stores belong in same branch with this store, and 3 similar branches
				String syscatId = targetStore.getSyscatId();
				dr.add(TYPENAME_COMPOUND_BRANCHSTORE, 
						new ALStoresBelongToBranch(targetStore.getBranchId(), targetId, 10, 10, 10).layopts(LAYOPT_NAMELINK), 
						new ALBranchesBelongToSyscat(targetStore.getSyscatId(), targetStore.getBranchId(), DIRECT, 10, 8, 3)
							.layopts(LAYOPT_NAMELINK | LAYOPT_WITHSYSCAT)
							.unshownSyscat(syscatId));
				
				//sub catalogs
				dr.add(TYPENAME_COMPOUND,
						new ALCatalogsBelongToCatalog(targetId, null, DIRECT, 10, 10, 10)
							.layopts(LAYOPT_NAMELINK | LAYOPT_PRIVATECAT),
						new ALCatalogsBelongToCatalog(targetStore.getBranchId(), null, DIRECT, 10, 10, 10)
							.layopts(LAYOPT_NAMELINK | LAYOPT_PRIVATECAT));
				
				//Some active promotions from this branch in one compound
				dr.add(new ALPromotionsBelongToSyscat(syscatId, null, targetStore.getBranchId(), DIRECT, clusterId, 10, 10, 10));
				
				//catelog items from this store's root category
				dr.add(new ALItemBelongToCatalog(targetId, syscatId, null, RECURSIVE, 20, 20, 20).layopts(Feed.LAYOPT_WITHCAT));
				//catelog items from this branch's root category
				dr.add(new ALItemBelongToCatalog(targetStore.getBranchId(), syscatId, null, RECURSIVE, 20, 20, 20).layopts(Feed.LAYOPT_WITHCAT));
				return dr;
			}
			
		};
		drillers[TYPE_CAT] = new DetailDriller() {

			@Override
			public DrillResult drill(int clusterId, String targetId, String targetSyscat,
					 List<String> segments,BigDecimal lon, BigDecimal lat/*, int page, int size*/) throws SQLException {
				
				//5 sub cats, 3 sibling cats 
				/*String syscatId;
				Catalog cat = (Catalog) PartnerManager.instance.getSystemCatalog(targetId);
				if (cat != null) {
					syscatId = targetId;
				} else {
					cat = (Catalog) new CatalogDao().loadCatalog(targetId, false);
					syscatId = cat.getSystemCatalogId();
				}*/
				Catalog cat = (Catalog) new CatalogDao().loadCatalog(targetId, false);
				
				//cat is null if targetId is in an unmanaged branch  
				/*if (cat == null) {
					Operation branch = new OperationDao().loadBranch(targetId);
					if (branch != null) {
						cat = (Catalog) branch.getRootCatalog();
					}
				}*/
				String syscatId = cat.getSystemCatalogId();

				DrillResult dr = createDefaultDrills(clusterId, lon, lat);
				createBranchAlerts(dr, syscatId);
				
				dr.add(cat);
				dr.layoutOptions = LAYOPT_WITHSYSCAT | LAYOPT_WITHBRANCH | LAYOPT_PRIVATECAT;
				
				/*dr.add(TYPENAME_COMPOUND, 
						new ALCatalogsBelongToCatalog(targetId, null, DIRECT, 10, 8, 5), 
						new ALCatalogsBelongToCatalog(cat.getParentCatalogId(), targetId, DIRECT, 10, 8, 3));*/
				
				//5 active promotions from this branch in one compound
				dr.add(new ALPromotionsBelongToSyscat(syscatId, null, cat.branchId, DIRECT, clusterId, 10, 5, 5));
				
				//5 other stores, 3 similar branches
				//ja = StoreDriller.findStoresOfBranch(cat.branchId, cat.storeId, 0, 8);
				dr.add(TYPENAME_COMPOUND_BRANCHSTORE, 
						new ALStoresBelongToBranch(cat.branchId, cat.storeId, 10, 8, 5).layopts(LAYOPT_NAMELINK), 
						new ALBranchesBelongToSyscat(syscatId, cat.branchId, DIRECT, 10, 8, 3).layopts(LAYOPT_NAMELINK));
				
				//5 feature items from this catalog
				dr.add(new ALItemBelongToCatalog(targetId, syscatId, null, RECURSIVE, 10, 5, 5));
				
				//remain items from this catalog
				//dr.add(new ALItemBelongToCatalog(targetId, null, RECURSIVE, 10, 5, 5));
				return dr;
			}
			
		};
		
		drillers[TYPE_CATITEM] = new DetailDriller() {
			
			@Override
			public DrillResult drill(int clusterId, String targetId, String targetSyscat,
					 List<String> segments, BigDecimal lon, BigDecimal lat/*, int page, int size*/) throws SQLException {
				//5 sibling cats, 3 similar branches 
				//String itemId = targetId.substring(0, targetId.indexOf('_'));
				//String syscatId = targetId.substring(itemId.length() + 1);
				CatalogItem catItem = new CatalogItemDao().loadCatalogItem(targetId, PartnerManager.instance.getCatalogSpec(targetSyscat));
				//Catalog cat = new CatalogDao().loadCatalog(catItem.getCatalogId(), false);

				DrillResult dr = createDefaultDrills(clusterId, lon, lat);
				createBranchAlerts(dr, catItem.getBranchId());
				
				dr.add(catItem);
				dr.layoutOptions = LAYOPT_WITHBRANCH | LAYOPT_WITHSYSCAT | LAYOPT_WITHCAT | LAYOPT_WITHDETAILS;
				
				if (SYSTEM_BRANCH_ID.equals(catItem.getStoreId())) {
					//dr.add(new ALCatalogsBelongToCatalog(catItem.getSyscatId(), null, RECURSIVE, 10, 8, 5));
					Catalog syscat = (Catalog) PartnerManager.instance.getSystemCatalog(catItem.getSyscatId());
					dr.add(TYPENAME_COMPOUND, 
							new ALCatalogsBelongToCatalog(catItem.getSyscatId(), null, RECURSIVE, 10, -1, -1)
								.layopts(LAYOPT_NAMELINK),
							new ALCatalogsBelongToCatalog(syscat.getParentCatalogId(), catItem.getSyscatId(), RECURSIVE, 10, -1, -1)
								.layopts(LAYOPT_NAMELINK).unshownSyscat(catItem.getSyscatId()));
					
					dr.add(new ALItemBelongToSyscat(catItem.getSyscatId(), null, RECURSIVE, 10, 10, 10)
						.layopts(LAYOPT_WITHBRANCH | LAYOPT_WITHSYSCAT).unshownSyscat(catItem.getSyscatId()));
					
				} else {
					//dr.add(new ALCatalogsBelongToCatalog(catItem.getCatalogId(), null, RECURSIVE, 10, 8, 5));
					dr.add(TYPENAME_COMPOUND, 
							new ALCatalogsBelongToCatalog(catItem.getCatalogId(), null, RECURSIVE, 10, 20, 20)
								.layopts(LAYOPT_NAMELINK | LAYOPT_PRIVATECAT),
							new ALCatalogsBelongToCatalog(catItem.getParentCatId(), catItem.getCatalogId(), RECURSIVE, 10, 8, 5)
								.layopts(LAYOPT_NAMELINK | LAYOPT_PRIVATECAT).unshownCat(catItem.getCatalogId()));
					dr.add(new ALItemBelongToCatalog(catItem.getCatalogId(), catItem.getSyscatId(), targetId, RECURSIVE, 10, 10, 10)
						.layopts(LAYOPT_WITHCAT).unshownCat(catItem.getCatalogId()));
				}
				return dr;
				/*Catalog cat = (Catalog) new CatalogDao().loadCatalog(targetId, false);
				JsonArray ja = findSubCatalogs(targetId, null, 8);
				JsonArray ja2 = findSubCatalogs(cat.getParentCatalogId(), targetId, 8);
				dr.add(IDetailManager.TYPENAME_CAT, ja, 5, ja2, 3);
				
				//5 active promotions from this branch in one compound
				ja = PromotionDriller.findOperationPromotions(new String[] {cat.branchId}, 5);
				dr.add(IDetailManager.TYPENAME_PROMO, ja, 5);
				
				//5 feature items from this catalog
				ja = CatalogItemDriller.findCatalogItems(cat, null, 5);
				dr.add(IDetailManager.TYPENAME_CATITEM, ja, 5);
				
				//5 other stores, 3 similar branches
				ja = StoreDriller.findStoresOfBranch(cat.branchId, cat.storeId, 0, 8);
				ja2 = BranchDriller.findBranchesSimilar(cat.branchId, 8);
				dr.add(IDetailManager.TYPENAME_COMPOUND_BRANCHSTORE, ja, 5, ja2, 3);
				return dr.toString();*/
			}
		};
		
		drillers[TYPE_PROMO] = new DetailDriller() {
			
			@Override
			public DrillResult drill(int clusterId, String targetId, String targetSyscat,
					 List<String> segments, BigDecimal lon, BigDecimal lat) throws SQLException {

				Promotion p = new PromotionDao().load(targetId);
				DrillResult dr = createDefaultDrills(clusterId, lon, lat);
				createBranchAlerts(dr, p.branchId);
				
				dr.add(p);
				dr.add(new ALPromotionsBelongToSyscat(p.syscatId, targetId, p.branchId, RECURSIVE, clusterId, 10, 10, 10));
				return dr;
				
				//At most 5 stores belong to this branch and 3 similar branches
				/*DrillResult dr = new DrillResult();
				JsonArray ja = StoreDriller.findStoresOfBranch(branchId, null, 0, 8);
				JsonArray ja2 = findBranchesSimilar(branchId, 8);
				dr.add(IDetailManager.TYPENAME_COMPOUND_BRANCHSTORE, ja, 5, ja2, 3);
				
				//5 active promotions from this branch in one compound
				ja = PromotionDriller.findOperationPromotions(new String[] {branchId}, 5);
				dr.add(IDetailManager.TYPENAME_COMPOUND_PROMOS, ja, 5);
				
				//10 sub categories of this branch's root category in one compound
				ja = CatalogDriller.findSubCatalogs(branchId, null, 10);
				dr.add(IDetailManager.TYPENAME_COMPOUND_CAT, ja, 10);
				
				//Feature catelog items from this branch's root category
				Operation targetBranch = (Operation) new OperationDao().loadBranch(branchId);
				ja = CatalogItemDriller.findCatalogItems((Catalog) targetBranch.getRootCatalog(), null, 20);
				dr.add(IDetailManager.TYPENAME_COMPOUND_CITEM, ja, 20);
				
				//Gson gson = new GsonBuilder().setPrettyPrinting().create();
				//return gson.toJson(dr);
				return dr.toString();*/
			}
		};
	}
	
	private static int typeNameToNumber(String name) {
		
		/*if (TYPENAME_NO == name) {
			return TYPE_NO;
		} else*/ if (TYPENAME_BRANCH.equals(name)) {
			return TYPE_BRANCH;
		} else if (TYPENAME_STORE.equals(name)) {
			return TYPE_STORE;
		} else if (TYPENAME_CAT.equals(name)) {
			return TYPE_CAT;
		} else if (TYPENAME_CATITEM.equals(name)) {
			return TYPE_CATITEM;
		} else if (TYPENAME_PROMO.equals(name)) {
			return TYPE_PROMO;
		} else if (TYPENAME_SYSCAT.equals(name)) {
			return TYPE_SYSCAT;
		} else {
			throw new RuntimeException(name);
		}
	}
	
	private static void loadAllConsumers() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = SmartpadConnectionPool.instance.dataSource.getConnection();
			stmt = conn.createStatement();
			String sql = "select consumer_id, cluster_id from consumers";
			System.out.println("SQL: " + sql);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				clusterMap.put(rs.getString("consumer_id"), rs.getInt("cluster_id"));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
}
