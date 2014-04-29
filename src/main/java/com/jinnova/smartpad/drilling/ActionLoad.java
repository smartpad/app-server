package com.jinnova.smartpad.drilling;

import static com.jinnova.smartpad.partner.IDetailManager.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;

import com.jinnova.smartpad.CachedPagingList;
import com.jinnova.smartpad.db.CatalogItemDao;
import com.jinnova.smartpad.db.OperationDao;
import com.jinnova.smartpad.partner.Catalog;
import com.jinnova.smartpad.partner.ICatalog;
import com.jinnova.smartpad.partner.ICatalogSort;
import com.jinnova.smartpad.partner.ICatalogSpec;
import com.jinnova.smartpad.partner.IPromotion;
import com.jinnova.smartpad.partner.IPromotionSort;
import com.jinnova.smartpad.partner.Operation;
import com.jinnova.smartpad.partner.PartnerManager;

abstract class ActionLoad {
	
	static final String REL_SIMILAR = "sim";
	
	static final String REL_BELONG = "bel";
	
	static final String REL_BELONG_DIRECTLY = "beld";
	
	static final String REL_BELONG_RECURSIVELY = "beli";
	
	static final String REL_SIBLING = "sib";
	
	String branchId;
	
	String storeId;
	
	String catId;
	
	String syscatId;
	
	int clusterId;
	
	private final String anchorType;

	final String targetType;
	
	private final String relation;
	
	String anchorId;
	
	String excludeId;
	
	int offset;
	
	int pageSize;
	
	BigDecimal gpsLon;
	
	BigDecimal gpsLat;
	
	private int initialLoadSize;
	
	private int initialDrillSize;

	private boolean more = true;
	
	private static HashMap<String, Class<? extends ActionLoad>> actionClasses;
	
	private static boolean initializing = true;
	
	boolean recursive = false;
	
	static void initialize() {
		actionClasses = new HashMap<String, Class<? extends ActionLoad>>();
		register(new ALBranchesBelongDirectlyToSyscat());
		register(new ALBranchesBelongRecursivelyToSyscat());
		register(new ALCatalogsBelongDirectlyToCatalog());
		register(new ALItemBelongToCatalog());
		register(new ALItemBelongRecursivelyToSyscat());
		register(new ALPromotionsBelongDirectlyToSyscat());
		register(new ALStoresBelongToBranch());
		initializing = false;
	}
	
	private static void register(ActionLoad load) {
		String key = load.anchorType + load.targetType + load.relation;
		if (actionClasses.containsKey(key)) {
			throw new RuntimeException(key);
		}
		actionClasses.put(key, load.getClass());
	}
	
	static ActionLoad createLoad(String targetType, String anchorType, String relation) throws SQLException {
		
		Class<? extends ActionLoad> c = actionClasses.get(anchorType + targetType + relation);
		try {
			return c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	int getInitialLoadSize() {
		return initialLoadSize;
	}

	int getInitialDrillSize() {
		return initialDrillSize;
	}
	
	void setOffset(int offset) {
		this.offset = offset;
	}
	
	ActionLoad(String anchorType, String targetType, String relation) {
		if (!initializing && !actionClasses.containsKey(anchorType + targetType + relation)) {
			throw new RuntimeException("Action not registered: " + anchorType + targetType + relation);
		}
		this.anchorType = anchorType;
		this.targetType = targetType;
		this.relation = relation;
	}
	
	void setParams(String anchorId, String excludeId, int pageSize, int initialLoadSize, int initialDrillSize) {
		this.anchorId = anchorId;
		this.excludeId = excludeId;
		this.pageSize = pageSize;
		this.initialLoadSize = initialLoadSize;
		this.initialDrillSize = initialDrillSize;
	}
	
	void setParams(String anchorId, String excludeId, boolean recursive, 
			int pageSize, int initialLoadSize, int initialDrillSize) {
		this.anchorId = anchorId;
		this.excludeId = excludeId;
		this.recursive = recursive;
		this.pageSize = pageSize;
		this.initialLoadSize = initialLoadSize;
		this.initialDrillSize = initialDrillSize;
	}
	
	ActionLoad exclude(String excludeId) {
		this.excludeId = excludeId;
		return this;
	}

	String generateNextLoadUrl() {
		if (!more) {
			return null;
		}
		StringBuffer buffer = new StringBuffer(targetType + "/" + relation + "/" + anchorType + 
				"/" + anchorId + "?offset=" + offset + "&size="  + pageSize);
		//if (clusterId != null) {
			buffer.append("&clu=" + clusterId);
		//}
		if (branchId != null) {
			buffer.append("&branchId=" + branchId);
		}
		if (storeId != null) {
			buffer.append("&storeId=" + storeId);
		}
		if (catId != null) {
			buffer.append("&catId=" + catId);
		}
		if (syscatId != null) {
			buffer.append("&syscatId=" + syscatId);
		}
		if (excludeId != null) {
			buffer.append("&excludeId=" + excludeId);
		}
		if (gpsLon != null) {
			buffer.append("&lon=" + gpsLon.toPlainString());
		}
		if (gpsLat != null) {
			buffer.append("&lat=" + gpsLat.toPlainString());
		}
		buffer.append("&recur=" + recursive);
		return buffer.toString();
	}
	
	abstract Object[] load(int offset, int size) throws SQLException;
	
	Object[] loadFirstEntries() throws SQLException {
		return load(0, initialLoadSize);
	}
	
	//abstract Object[] loadFirstEntries(int initialLoadSize) throws SQLException;
	
	final Object[] load() throws SQLException {
		Object[] result = load(offset, pageSize);
		if (result == null || result.length < pageSize) {
			more = false;
		}
		offset += result.length;
		return result;
	}
}

class ALBranchesBelongDirectlyToSyscat extends ActionLoad {
	
	ALBranchesBelongDirectlyToSyscat() {
		super(TYPENAME_SYSCAT, TYPENAME_BRANCH, REL_BELONG_DIRECTLY);
	}

	ALBranchesBelongDirectlyToSyscat(String anchorSyscatId, String excludeBranchId,
			int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(anchorSyscatId, excludeBranchId, pageSize, initialLoadSize, initialDrillSize);
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		return new OperationDao().iterateBranchesBySyscatDirectly(anchorId, excludeId).toArray(); //TODO size, offset
	}
	
}

class ALBranchesBelongRecursivelyToSyscat extends ActionLoad {
	
	ALBranchesBelongRecursivelyToSyscat() {
		super(TYPENAME_SYSCAT, TYPENAME_BRANCH, REL_BELONG_RECURSIVELY);
	}

	ALBranchesBelongRecursivelyToSyscat(String anchorSyscatId, String excludeBranchId, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(anchorSyscatId, excludeBranchId, pageSize, initialLoadSize, initialDrillSize);
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		return new OperationDao().iterateBranchesBySyscatRecursively(anchorId, excludeId).toArray(); //TODO size, offset
	}
	
}

class ALStoresBelongToBranch extends ActionLoad {
	
	ALStoresBelongToBranch() {
		super(TYPENAME_BRANCH, TYPENAME_STORE, REL_BELONG_DIRECTLY);
	}

	ALStoresBelongToBranch(String anchorBranchId, String excludeStoreId, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(anchorBranchId, excludeStoreId, pageSize, initialLoadSize, initialDrillSize);
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		return new OperationDao().iterateStores(anchorId, excludeId, offset, size).toArray();
	}
	
}

class ALCatalogsBelongDirectlyToCatalog extends ActionLoad {
	
	ALCatalogsBelongDirectlyToCatalog() {
		super(TYPENAME_CAT, TYPENAME_CAT, REL_BELONG_DIRECTLY);
	}

	ALCatalogsBelongDirectlyToCatalog(String catId, String excludeCatId, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(catId, excludeCatId, pageSize, initialLoadSize, initialDrillSize);
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		String syscatId = null; //don't need to parse spec 
		CachedPagingList<ICatalog, ICatalogSort> paging = Catalog.createSubCatalogPagingList(null, null, anchorId, syscatId, null);
		paging.setPageSize(size);
		return paging.loadFromOffset(offset).getPageEntries();
	}
	
}

class ALItemBelongToCatalog extends ActionLoad {
	
	ALItemBelongToCatalog() {
		super(TYPENAME_CAT, TYPENAME_CATITEM, REL_BELONG);
	}

	ALItemBelongToCatalog(String catId, String excludeItemId, 
			boolean recursive, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(catId, excludeItemId, recursive, pageSize, initialLoadSize, initialDrillSize);
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		return new CatalogItemDao().iterateItemsByCatalog(recursive, anchorId, excludeId, gpsLon, gpsLat, offset, size).toArray();
	}
	
}

class ALItemBelongRecursivelyToSyscat extends ActionLoad {
	
	ALItemBelongRecursivelyToSyscat() {
		super(TYPENAME_SYSCAT, TYPENAME_CATITEM, REL_BELONG_RECURSIVELY);
	}

	ALItemBelongRecursivelyToSyscat(String syscatId, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(syscatId, null, pageSize, initialLoadSize, initialDrillSize);
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		ICatalogSpec spec = PartnerManager.instance.getCatalogSpec(anchorId);
		return new CatalogItemDao().iterateCatalogItems(clusterId, spec, anchorId, gpsLon, gpsLat, offset, size).toArray();
	}
	
}

class ALPromotionsBelongDirectlyToSyscat extends ActionLoad {
	
	ALPromotionsBelongDirectlyToSyscat() {
		super(TYPENAME_BRANCH, TYPENAME_PROMO, REL_BELONG_DIRECTLY);
	}

	ALPromotionsBelongDirectlyToSyscat(String syscatId, String preferedBranchId, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(syscatId, null, pageSize, initialLoadSize, initialDrillSize);
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		CachedPagingList<IPromotion, IPromotionSort> paging = 
				Operation.createPromotionPagingList(null, null, anchorId, null); //TODO gps
		paging.setPageSize(size);
		return paging.loadFromOffset(offset).getPageEntries();
	}
	
}
