package com.harmony.umbrella.mapper.vo;

/**
 * @author wuxii@foxmail.com
 */
public class B extends VoSupport {

	private static final long serialVersionUID = 1L;

	private C c;

	public C getC() {
		return c;
	}

	public void setC(C c) {
		this.c = c;
	}

	@Override
	public String toString() {
		return getClass().getName() + ":{\"c\":\"" + c + "\"}";
	}

}
