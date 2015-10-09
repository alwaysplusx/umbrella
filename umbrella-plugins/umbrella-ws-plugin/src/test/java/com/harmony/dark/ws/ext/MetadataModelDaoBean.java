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
package com.harmony.dark.ws.ext;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.harmony.umbrella.data.dao.JpaDaoSupport;
import com.harmony.umbrella.ws.Metadata;
import com.harmony.umbrella.ws.MetadataLoader;

/**
 * @author wuxii@foxmail.com
 */
@Remote({ MetadataLoader.class, MetadataModelDao.class })
@Stateless(mappedName = "MetadataLoaderBean")
public class MetadataModelDaoBean extends JpaDaoSupport<MetadataModel, String> implements MetadataModelDao {

    @PersistenceContext(unitName = "harmony-dark")
    private EntityManager entityManager;

    @Override
    public Metadata loadMetadata(Class<?> serviceClass) {
        return findById(serviceClass.getName());
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

}
