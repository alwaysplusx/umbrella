package com.harmony.umbrella.validator;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.harmony.umbrella.validator.constraints.Contains;
import com.harmony.umbrella.validator.constraints.FormatDate;

/**
 * @author wuxii@foxmail.com
 */
public class Foo {

    @NotNull
    private String username;
    @Contains(contents = { "a", "b", "c", "d" }, all = true)
    private String work = "ab,v,d";
    private Long age;
    @Size(max = 100, min = 2)
    private List<String> part = new ArrayList<String>();
    @FormatDate(pattern = "yyyy/MM/dd")
    private String birthday;

    public Foo() {
        super();
    }

    public Foo(String username, Long age) {
        super();
        this.username = username;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public List<String> getPart() {
        return part;
    }

    public void setPart(List<String> part) {
        this.part = part;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}
