package com.jinnova.smartpad.domain;

import com.jinnova.smartpad.partner.ICatalogField;
import com.jinnova.smartpad.partner.ICatalogItem;
import com.jinnova.smartpad.partner.IDetailManager;

public class CatalogItem {
	
	private ICatalogItem ci;
	
	private int ord;

	public CatalogItem(ICatalogItem ci, int ord) {
		this.ci = ci;
		this.ord = ord;
	}
	
	public String getId() {
		return ci.getId();
	}

	public String getType() {
		return IDetailManager.TYPENAME_CATITEM;
	}
	
	public int getOrd() {
		return ord;
	}
	
	public String getName() {
		return ci.getFieldValue(ICatalogField.ID_NAME);
	}

}
