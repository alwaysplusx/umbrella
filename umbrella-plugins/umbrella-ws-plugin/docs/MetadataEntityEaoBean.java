package com.harmony.umbrella.ws.ext;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.harmony.modules.ejb.eao.GenericEaoImpl;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.ws.Metadata;
import com.harmony.umbrella.ws.MetadataLoader;
import com.harmony.umbrella.ws.persistence.MetadataEntity;
import com.harmony.umbrella.ws.ext.MetadataEntityEaoRemote;

/**
 * @author wuxii@foxmail.com
 */
@Remote({ MetadataLoader.class })
@Stateless(mappedName = "MetadataEntityEaoBean")
public class MetadataEntityEaoBean extends GenericEaoImpl<MetadataEntity, String> implements MetadataLoader, MetadataEntityEaoRemote {

    @Override
    @PersistenceContext(unitName = "harmony-dark")
    public void setEntityManager(EntityManager em) {
        super.setEntityManager(em);
    }

    @Override
    public Metadata loadMetadata(Class<?> serviceClass) {
        Assert.notNull(serviceClass, "service class is null");
        return find(serviceClass.getName());
    }

}
