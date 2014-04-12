package com.jinnova.smartpad.domain;

import com.jinnova.smartpad.partner.IDetailManager;
import com.jinnova.smartpad.partner.IOperation;

public class Shop {
	
	private IOperation shop;
	
	private int ord;
	
	public Shop(IOperation shop, int ord) {
		this.shop = shop;
		this.ord = ord;
	}
	
	public String getId() {
		return shop.getId();
	}

	public String getType() {
		return IDetailManager.TYPENAME_STORE;
	}
	
	public int getOrd() {
		return ord;
	}
	
	public String getName() {
		return shop.getName().getName();
	}

}
