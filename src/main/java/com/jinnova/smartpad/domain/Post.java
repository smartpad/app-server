package com.jinnova.smartpad.domain;

import com.jinnova.smartpad.partner.IDetailManager;

public class Post {
	
	private int ord;
	
	public Post(int ord) {
		this.ord = ord;
	}
	
	public String getId() {
		return "postid";
	}

	public String getType() {
		return IDetailManager.TYPENAME_POST;
	}
	
	public int getOrd() {
		return ord;
	}

}
