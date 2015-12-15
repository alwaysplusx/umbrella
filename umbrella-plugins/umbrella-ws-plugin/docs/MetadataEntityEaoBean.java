/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
