package com.harmony.umbrella.ws;

import java.io.Serializable;
import java.util.Calendar;

import com.harmony.umbrella.ws.annotation.Key;

/**
 * @author wuxii@foxmail.com
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1950532032126574719L;

	@Key(ordinal = 1)
	private String name;

	@Key(ordinal = 2)
	private int age;

	private String sex;

	private Calendar birthday;

	public User() {
	}

	public User(String name) {
		this.name = name;
	}

	public User(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public User(String name, int age, String sex, Calendar birthday) {
		this.name = name;
		this.age = age;
		this.sex = sex;
		this.birthday = birthday;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Calendar getBirthday() {
		return birthday;
	}

	public void setBirthday(Calendar birthday) {
		this.birthday = birthday;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{name:");
		builder.append(name);
		builder.append(", age:");
		builder.append(age);
		builder.append("}");
		return builder.toString();
	}

}
