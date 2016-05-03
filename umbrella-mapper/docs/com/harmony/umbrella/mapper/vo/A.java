package com.harmony.umbrella.mapper.vo;

/**
 * @author wuxii@foxmail.com
 */
public class A extends VoSupport {

	private static final long serialVersionUID = 1L;

	private String name;
	private Integer age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return getClass().getName() + ":{\"name\":\"" + name + "\"}";
	}

}
