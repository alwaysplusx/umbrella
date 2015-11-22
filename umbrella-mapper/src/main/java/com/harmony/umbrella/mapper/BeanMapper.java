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
package com.harmony.umbrella.mapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.dozer.DozerBeanMapper;

/**
 * dozer 映射工具
 * 
 * @author wuxii@foxmail.com
 */
public class BeanMapper {

    private static Map<String, BeanMapper> mappers = new HashMap<String, BeanMapper>();

    private DozerBeanMapper dozerMapper;

    private BeanMapper(String mappingFile) {
        if (mappingFile == null) {
            this.dozerMapper = new DozerBeanMapper();
        } else {
            this.dozerMapper = new DozerBeanMapper(Arrays.asList(mappingFile));
        }
    }

    public static BeanMapper getInstance() {
        return getInstance(null);
    }

    public static BeanMapper getInstance(String mappingFile) {
        BeanMapper mapper = mappers.get(mappingFile);
        if (mapper == null) {
            synchronized (mappers) {
                mapper = new BeanMapper(mappingFile);
                mappers.put(mappingFile, mapper);
            }
        }
        return mapper;
    }

    public <T> T mapper(Object src, T dest) {
        dozerMapper.map(src, dest);
        return dest;
    }

    public <T> T mapper(Object src, Class<T> destType) {
        return dozerMapper.map(src, destType);
    }

    public <T> T mapper(Object src, T dest, String mapId) {
        dozerMapper.map(src, dest, mapId);
        return dest;
    }

    public <T> T mapper(Object src, Class<T> destType, String mapId) {
        return dozerMapper.map(src, destType, mapId);
    }

}
