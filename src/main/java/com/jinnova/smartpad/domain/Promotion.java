package com.jinnova.smartpad.domain;

import com.jinnova.smartpad.partner.IPromotion;

public class Promotion {
	
	private IPromotion promo;
	
	private int ord;
	
	public Promotion(IPromotion promo, int ord) {
		this.promo = promo;
		this.ord = ord;
	}
	
	public String getId() {
		return promo.getId();
	}

	public String getType() {
		return "pro";
	}
	
	public int getOrd() {
		return ord;
	}
	
	public String getName() {
		return promo.getName().getName();
	}

}
