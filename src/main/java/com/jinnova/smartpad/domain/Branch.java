package com.jinnova.smartpad.domain;

import com.jinnova.smartpad.partner.IOperation;

public class Branch {
	
	private IOperation branch;
	
	public Branch(IOperation branch) {
		this.branch = branch;
	}

	public String getType() {
		return "branch";
	}
	
	public String getName() {
		return branch.getName().getName();
	}
}
