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
package com.harmony.umbrella.ws;

import java.util.Map;

/**
 * one sync method one sync callback
 * 
 * @param <T>
 *            同步的实体类
 * @param <R>
 *            同步返回的结果
 * @author wuxii@foxmail.com
 */
public interface SyncCallback<T, R> {

    /**
     * 回调方法之, 执行同步前
     * 
     * @param obj
     *            同步的对象
     */
    void forward(T obj, Map<String, Object> content);

    /**
     * 回调方法之, 同步成功后
     * 
     * @param obj
     *            同步的对象
     * @param result
     *            同步结果
     */
    void success(T obj, R result, Map<String, Object> content);

    /**
     * 回调方法, 同步失败
     * 
     * @param obj
     *            同步对象
     * @param throwable
     *            同步失败原因
     */
    void failed(T obj, Throwable throwable, Map<String, Object> content);

}
