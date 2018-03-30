package com.harmony.umbrella.web.persistence;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.domain.Persistable;

/**
 * @author wuxii@foxmail.com
 */
@Entity
@Table(name = "TEST_USER")
public class User implements Serializable, Persistable<String> {

    private static final long serialVersionUID = -4683677790918841843L;

    @Id
    private String username;
    private String password;
    private int age;

    @Override
    public boolean isNew() {
        return getId() == null;
    }

    @Override
    public String getId() {
        return username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
