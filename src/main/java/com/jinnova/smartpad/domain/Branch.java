package com.jinnova.smartpad.domain;

import com.jinnova.smartpad.partner.IOperation;

public class Branch {
	
	private IOperation branch;
	
	private int ord;
	
	public Branch(IOperation branch, int ord) {
		this.branch = branch;
		this.ord = ord;
	}
	
	/*public String getId() {
		return branch.get
	}*/

	public String getType() {
		return "branch";
	}
	
	public int getOrd() {
		return ord;
	}
	
	public String getName() {
		return branch.getName().getName();
	}
}
