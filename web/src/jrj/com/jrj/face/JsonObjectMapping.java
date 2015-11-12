package com.jrj.face;


public interface JsonObjectMapping<T> {

	public abstract T map(String json);
}