package com.huiju.module.fs.eao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.huiju.module.data.BaseEntity;
import com.huiju.module.data.eao.GenericEaoImpl;

/**
 * @author wuxii@foxmail.com
 */
public abstract class GenericCoreEao<T extends BaseEntity<?>, PK extends Serializable> extends GenericEaoImpl<T, PK> {

    @PersistenceContext(unitName = "fs")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }
}
