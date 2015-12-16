/*
 * Copyright 2012-2015 the original author or authors.
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
package com.harmony.umbrella.ws.biz;

import com.harmony.umbrella.biz.BusinessSupport;
import com.harmony.umbrella.data.JpaDao;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.ws.Metadata;
import com.harmony.umbrella.ws.dao.MetadataDao;
import com.harmony.umbrella.ws.persistence.MetadataEntity;

/**
 * @author wuxii@foxmail.com
 */
public class MetadataBusinessBean extends BusinessSupport<MetadataEntity, String> implements MetadataBusiness {

    private MetadataDao metadataDao;

    @Override
    protected JpaDao<MetadataEntity, String> getJpaDao() {
        return metadataDao;
    }

    @Override
    public Metadata loadMetadata(Class<?> serviceClass) {
        Assert.notNull(serviceClass, "service class is null, please set service class");
        return loadMetadata(serviceClass.getName());
    }

    @Override
    public Metadata loadMetadata(String serviceClassName) {
        return findOne(serviceClassName);
    }

}
