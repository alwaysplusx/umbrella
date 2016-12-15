package com.harmony.umbrella.json.vo;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * @author wuxii@foxmail.com
 */
@Entity
public class Person {

    public static final Person me;

    static {
        /*
         * father    monther
         *   |          |
         *   +----|-----+
         *        me
         *        |
         *   +----+-----+
         *   |          |     
         * child1    child2
         * 
         */
        me = new Person(1l, "me");

        me.setFather(new Person(2l, "father"));
        me.setMother(new Person(3l, "mother"));

        me.getMother().setChilds(Arrays.asList(me));
        me.getFather().setChilds(Arrays.asList(me));

        Person child1 = new Person(4l, "child1");
        Person child2 = new Person(5l, "child2");

        child1.setBrothers(Arrays.asList(child2));
        child2.setBrothers(Arrays.asList(child1));
        child1.setFather(me);
        child2.setFather(me);

        me.setChilds(Arrays.asList(child1, child2));
    }

    private Long personId;
    private String username;
    private String password;
    private Person father;
    private Person mother;
    @OneToMany
    private List<Person> childs;
    @OneToMany
    private List<Person> brothers;

    public Person() {
    }

    public Person(Long id, String name) {
        this.personId = id;
        this.username = name;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
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

    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public Person getMother() {
        return mother;
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    public List<Person> getChilds() {
        return childs;
    }

    public void setChilds(List<Person> childs) {
        this.childs = childs;
    }

    public List<Person> getBrothers() {
        return brothers;
    }

    public void setBrothers(List<Person> brothers) {
        this.brothers = brothers;
    }

    @Override
    public String toString() {
        return "Person [username=" + username + "]";
    }

}
