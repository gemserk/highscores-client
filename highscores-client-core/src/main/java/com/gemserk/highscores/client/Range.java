package com.gemserk.highscores.client;

import java.util.HashMap;
import java.util.Map;

public enum Range {
	All(1), Month(2), Week(3), Day(4);
	
	public final int scope;
	public final String key;
	
	private static Map<String,Range> byKey;
	
	static {
		byKey = new HashMap<String, Range>();
		for (Range range : Range.values()) {
			byKey.put(range.key, range);
		}
	}
	
	private Range(int scope) {
		this.scope = scope;
		this.key = this.name().toLowerCase();
	}
	
	static public Range getByKey(String key){
		return byKey.get(key);
	}
	
	
}
