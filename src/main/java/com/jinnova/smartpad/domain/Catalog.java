package com.jinnova.smartpad.domain;

import com.jinnova.smartpad.partner.ICatalog;
import com.jinnova.smartpad.partner.IDetailManager;

public class Catalog {
	
	private ICatalog cat;
	
	private int ord;

	public Catalog(ICatalog cat, int ord) {
		this.cat = cat;
		this.ord = ord;
	}
	
	public String getId() {
		return cat.getId();
	}

	public String getType() {
		return IDetailManager.TYPENAME_CAT;
	}
	
	public int getOrd() {
		return ord;
	}
	
	public String getName() {
		return cat.getName().getName();
	}

}
