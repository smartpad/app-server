package com.jinnova.smartpad.domain;

import com.jinnova.smartpad.partner.ICatalog;

public class Catalog {
	
	private ICatalog cat;
	
	private int ord;

	public Catalog(ICatalog cat, int ord) {
		this.cat = cat;
		this.ord = ord;
	}
	
	/*public String getId() {
		return branch.get
	}*/

	public String getType() {
		return "cat";
	}
	
	public int getOrd() {
		return ord;
	}
	
	public String getName() {
		return cat.getName().getName();
	}

}
