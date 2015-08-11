///*
// * Copyright 2002-2015 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.harmony.umbrella.cache.jmx;
//
//import net.sf.ehcache.management.CacheManager;
//
///**
// * @author wuxii@foxmail.com
// */
//public class JmxCachePool extends CacheManager implements JmxCachePoolMBean {
//
//    private net.sf.ehcache.CacheManager cacheManager;
//
//    public JmxCachePool(net.sf.ehcache.CacheManager cacheManager) {
//        super(cacheManager);
//        this.cacheManager = cacheManager;
//    }
//
//    @Override
//    public boolean clear() {
//        return false;
//    }
//
//    @Override
//    public String snapshot() {
//        return null;
//    }
//
//    @Override
//    public boolean exists(String key) {
//        return false;
//    }
//
//    @Override
//    public boolean clear(String key) {
//        return false;
//    }
//
//}
