package com.jinnova.smartpad.drilling;

import static com.jinnova.smartpad.partner.IDetailManager.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.jinnova.smartpad.Feed;
import com.jinnova.smartpad.db.CatalogDao;
import com.jinnova.smartpad.db.CatalogItemDao;
import com.jinnova.smartpad.db.OperationDao;
import com.jinnova.smartpad.db.PromotionDao;
import com.jinnova.smartpad.partner.ICatalogField;
import com.jinnova.smartpad.partner.PartnerManager;

abstract class ActionLoad {
	
	static final String REL_SIMILAR = "sim";
	
	static final String REL_BELONG = "bel";
	
	static final String REL_SIBLING = "sib";
	
	static final String REL_SEGMENT = "seg";
	
	private static HashMap<String, Class<? extends ActionLoad>> actionClasses;
	
	private static boolean initializing = true;
	
	String branchId;
	
	String storeId;
	
	String catId;
	
	String syscatId;
	
	//HashMap<String, LinkedList<String>> segments;
	List<String> segments;
	
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
	
	boolean recursive = false;
	
	String excludeSyscat;
	
	String excludeCat;
	
	private int layoutOptions = Feed.LAYOPT_NONE;
	
	//private String layoutSyscat;
	//HashMap<String, Object> layoutParams = new HashMap<>();

	private boolean more = true;
	
	String linkPrefix;
	
	static void initialize() {
		if (actionClasses != null) {
			return;
		}
		actionClasses = new HashMap<String, Class<? extends ActionLoad>>();
		register(new ALBranchesBelongToSyscat());
		register(new ALCatalogsBelongToCatalog());
		register(new ALItemBelongToCatalog());
		register(new ALItemBelongToSyscat());
		register(new ALPromotionsBelongToSyscat());
		register(new ALStoresBelongToBranch());
		//register(new ALItemsSegmentedToSyscat());
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
	
	int getLayopts() {
		return this.layoutOptions;
	}
	
	ActionLoad layopts(int layoutOptions) {
		this.layoutOptions = this.layoutOptions | layoutOptions;
		return this;
	}
	
	/*String getLayoutSyscat() {
		return this.layoutSyscat;
	}*/
	
	ActionLoad unshownSyscat(String excludeSyscat) {
		//this.layoutSyscat = layoutSyscat;
		//layoutParams.put(Feed.LAYOUT_PARAM_SYSCAT_EXCLUDE, layoutSyscat);
		this.excludeSyscat = excludeSyscat;
		return this;
	}
	
	ActionLoad unshownCat(String excludeCat) {
		//this.layoutSyscat = layoutSyscat;
		//layoutParams.put(Feed.LAYOUT_PARAM_SYSCAT_EXCLUDE, layoutSyscat);
		this.excludeCat = excludeCat;
		return this;
	}
	
	/*ActionLoad layoutParam(String key, String value) {
		layoutParams.put(key, value);
		return this;
	}*/
	
	ActionLoad(String anchorType, String targetType, String relation) {
		if (!initializing && !actionClasses.containsKey(anchorType + targetType + relation)) {
			throw new RuntimeException("Action not registered: " + anchorType + targetType + relation);
		}
		this.anchorType = anchorType;
		this.targetType = targetType;
		this.relation = relation;
	}
	
	void setParams(String anchorId, String excludeId, boolean recursive, 
			int pageSize, int initialLoadSize, int initialDrillSize) {
		this.anchorId = anchorId;
		this.excludeId = excludeId;
		this.recursive = recursive;
		this.pageSize = pageSize;
		this.initialLoadSize = initialLoadSize;
		this.initialDrillSize = initialDrillSize;
		if (this.initialLoadSize < initialDrillSize) {
			throw new IllegalArgumentException();
		}
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
		if (segments != null) {
			for (String one : segments) {
				buffer.append("&segments=" + one);
			}
		}
		if (gpsLon != null) {
			buffer.append("&lon=" + gpsLon.toPlainString());
		}
		if (gpsLat != null) {
			buffer.append("&lat=" + gpsLat.toPlainString());
		}
		if (excludeSyscat != null) {
			buffer.append("&exsyscat=" + excludeSyscat);
		}
		if (excludeCat != null) {
			buffer.append("&excat=" + excludeCat);
		}
		buffer.append("&recur=" + recursive);
		buffer.append("&layopts=" + layoutOptions);
		
		//buffer.append("&");
		//buffer.append(buildParamSet(layoutParams));
		return buffer.toString();
	}
	
	abstract Object[] load(int offset, int size) throws SQLException;
	
	HashMap<String, Object> getLayoutParams() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(Feed.LAYOUT_PARAM_LINKPREFIX, this.linkPrefix);
		params.put(Feed.LAYOUT_PARAM_SEGMENTS, this.segments);
		params.put(Feed.LAYOUT_PARAM_SYSCAT_EXCLUDE, this.excludeSyscat);
		params.put(Feed.LAYOUT_PARAM_CAT_EXCLUDE, this.excludeCat);
		return params;
	}
	
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
	
	HashMap<String, LinkedList<String>> buildSegmentMap() {
		if (segments == null) {
			return null;
		}
		HashMap<String, LinkedList<String>> segmentMap = new HashMap<>();
		for (String one : segments) {
			int index = one.indexOf(ICatalogField.SEGMENT_PARAM_SEP);
			if (index + 1 >= one.length()) {
				continue;
			}
			String oneKey = one.substring(0, index);
			LinkedList<String> valueList = segmentMap.get(oneKey);
			if (valueList == null) {
				valueList = new LinkedList<>();
				segmentMap.put(oneKey, valueList);
			}
			valueList.add(one.substring(index + 1));
		}
		return segmentMap;
	}
}

class ALBranchesBelongToSyscat extends ActionLoad {
	
	ALBranchesBelongToSyscat() {
		super(TYPENAME_SYSCAT, TYPENAME_BRANCH, REL_BELONG);
	}

	ALBranchesBelongToSyscat(String anchorSyscatId, String excludeBranchId,
			boolean recursive, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(anchorSyscatId, excludeBranchId, recursive, pageSize, initialLoadSize, initialDrillSize);
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		return new OperationDao().iterateBranchesBySyscat(anchorId, excludeId, recursive).toArray(); //TODO size, offset
	}
	
}

/*class ALItemsSegmentedToSyscat extends ActionLoad {
	
	ALItemsSegmentedToSyscat() {
		super(TYPENAME_SYSCAT, TYPENAME_CATITEM, REL_SEGMENT);
	}

	ALItemsSegmentedToSyscat(String syscatId, HashMap<String, LinkedList<String>> segments,
			boolean recursive, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(syscatId, null, recursive, pageSize, initialLoadSize, initialDrillSize);
		super.syscatId = syscatId;
		this.segments = segments;
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		String specId = PartnerManager.instance.getCatalogSpec(syscatId).getSpecId();
		return new CatalogItemDao().iterateItemsBySegment(syscatId, specId, clusterId, segments, recursive, gpsLon, gpsLat, offset, size).toArray();
	}
	
}*/

class ALStoresBelongToBranch extends ActionLoad {
	
	ALStoresBelongToBranch() {
		super(TYPENAME_BRANCH, TYPENAME_STORE, REL_BELONG);
		this.layopts(Feed.LAYOPT_STORE);
	}

	ALStoresBelongToBranch(String anchorBranchId, String excludeStoreId, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(anchorBranchId, excludeStoreId, false, pageSize, initialLoadSize, initialDrillSize);
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		return new OperationDao().iterateStores(anchorId, excludeId, offset, size).toArray();
	}
	
}

class ALCatalogsBelongToCatalog extends ActionLoad {
	
	ALCatalogsBelongToCatalog() {
		super(TYPENAME_CAT, TYPENAME_CAT, REL_BELONG);
	}

	ALCatalogsBelongToCatalog(String parentCatId, String excludeCatId, 
			boolean recursive, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(parentCatId, excludeCatId, recursive, pageSize, initialLoadSize, initialDrillSize);
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		return new CatalogDao().iterateCatalogs(anchorId, excludeId, recursive, offset, size).toArray();
	}
	
}

class ALItemBelongToCatalog extends ActionLoad {
	
	ALItemBelongToCatalog() {
		super(TYPENAME_CAT, TYPENAME_CATITEM, REL_BELONG);
	}

	ALItemBelongToCatalog(String catId, String syscatId, String excludeItemId, 
			boolean recursive, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(catId, excludeItemId, recursive, pageSize, initialLoadSize, initialDrillSize);
		super.syscatId = syscatId;
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		String specId = PartnerManager.instance.getCatalogSpec(syscatId).getSpecId();
		return new CatalogItemDao().iterateItems(anchorId, specId, excludeId, null, false, null, recursive, gpsLon, gpsLat, offset, size).toArray();
	}
	
}

class ALItemBelongToSyscat extends ActionLoad {
	
	ALItemBelongToSyscat() {
		super(TYPENAME_SYSCAT, TYPENAME_CATITEM, REL_BELONG);
	}

	ALItemBelongToSyscat(String syscatId, List<String> segmentList, boolean recursive, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(syscatId, null, recursive, pageSize, initialLoadSize, initialDrillSize);
		this.segments = segmentList;
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		String specId = PartnerManager.instance.getCatalogSpec(anchorId).getSpecId();
		return new CatalogItemDao().iterateItems(anchorId, specId, null, buildSegmentMap(), true, clusterId, recursive, gpsLon, gpsLat, offset, size).toArray();
	}
	
}

class ALPromotionsBelongToSyscat extends ActionLoad {
	
	ALPromotionsBelongToSyscat() {
		super(TYPENAME_BRANCH, TYPENAME_PROMO, REL_BELONG);
	}

	ALPromotionsBelongToSyscat(String syscatId, String excludePromoId, String preferedBranchId, //TODO prefered branch 
			boolean recursive, int clusterId, int pageSize, int initialLoadSize, int initialDrillSize) {
		this();
		setParams(syscatId, excludePromoId, recursive, pageSize, initialLoadSize, initialDrillSize);
		this.clusterId = clusterId;
	}

	@Override
	Object[] load(int offset, int size) throws SQLException {
		
		return new PromotionDao().iteratePromosBySyscat(anchorId, excludeId, recursive, clusterId).toArray();
	}
	
}
