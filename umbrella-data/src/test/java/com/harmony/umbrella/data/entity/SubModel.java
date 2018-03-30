package com.harmony.umbrella.data.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.domain.Persistable;

/**
 * @author wuxii@foxmail.com
 */
@Entity
@Table(name = "TEST_SUBMODEL")
public class SubModel implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "modelId", referencedColumnName = "id")
    private Model model;

    public SubModel() {
    }

    public SubModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean isNew() {
        return getId() == null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "SubModel: {id:" + id + ", name:" + name + "}";
    }

}
