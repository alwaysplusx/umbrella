package com.harmony.umbrella.mapper.vo;

/**
 * @author wuxii@foxmail.com
 */
public class C extends VoSupport {

	private static final long serialVersionUID = 1L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "{\"name\":\"" + name + "\"}";
	}
}
