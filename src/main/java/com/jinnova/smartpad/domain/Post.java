package com.jinnova.smartpad.domain;

public class Post {
	
	private int ord;
	
	public Post(int ord) {
		this.ord = ord;
	}
	
	public String getId() {
		return "postid";
	}

	public String getType() {
		return "post";
	}
	
	public int getOrd() {
		return ord;
	}

}
