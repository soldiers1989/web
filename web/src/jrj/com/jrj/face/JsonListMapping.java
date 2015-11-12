package com.jrj.face;

import java.util.List;

import com.jrj.common.page.PageInfo;

public interface JsonListMapping<T> {
	
	public abstract List<T> map(String json);
	
	public abstract List<T> map(String json,PageInfo page);
}