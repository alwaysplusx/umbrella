package com.harmony.umbrella.data.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.harmony.umbrella.util.TimeUtils;

/**
 * @author wuxii@foxmail.com
 */
@Entity
@Table(name = "TEST_MODEL")
public class Model implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private String name;
    private String code;

    private String content;
    private int ordinal;

    private Calendar createDate;

    @OneToMany(mappedBy = "model")
    private List<SubModel> subModels;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

    public List<SubModel> getSubModels() {
        return subModels;
    }

    public void setSubModels(List<SubModel> subModels) {
        this.subModels = subModels;
    }

    @Override
    public String toString() {
        return "Model: {id:" + id + ", name:" + name + ", code:" + code + ", content:" + content + ", ordinal:" + ordinal + ", createDate:"
                + TimeUtils.parseText(createDate, "yyyy-MM-dd") + ", subModels:" + subModels + "}";
    }

}
