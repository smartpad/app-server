package com.jinnova.smartpad.domain;

public class Item<T> {
	
	private int index;
	private T value;
	
	public Item(int index, T value) {
		super();
		this.index = index;
		this.value = value;
	}
	public int getIndex() {
		return index;
	}
	public T getValue() {
		return value;
	}
	
}
