package com.jinnova.smartpad.domain;

import com.jinnova.smartpad.partner.IOperation;

public class Shop {
	
	private IOperation shop;
	
	private int ord;
	
	public Shop(IOperation shop, int ord) {
		this.shop = shop;
		this.ord = ord;
	}
	
	/*public String getId() {
		return branch.get
	}*/

	public String getType() {
		return "store";
	}
	
	public int getOrd() {
		return ord;
	}
	
	public String getName() {
		return shop.getName().getName();
	}

}
