/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.umbrella.ws.proxy;

import java.util.List;
import java.util.Map;

/**
 * 同步客户端部分
 * <p>
 * 适用于一个同步接口的特定业务，所以一个接口方法对应一个{@linkplain Proxy},
 * 在业务上也尽量将相同类型的业务做成一个接口方法，使用不的标识来区分，切莫将相同类型的业务切分开将导致多个Proxy的窘境
 * <p>
 * 如：同步个业务的增加与修改应该作为一个接口使用一个标识位来区分是新增还是修改
 * 
 * @author wuxii@foxmail.com
 */
public interface Proxy<T> {

    /**
     * 不需要同步
     */
    int NO_NEED = -1;

    /**
     * 未同步
     */
    int UNSYNCHRONIZED = 0;

    /**
     * 同步中
     */
    int SYNCHRONIZING = 1;

    /**
     * 同步完成
     */
    int SYNCHRONIZED = 2;

    /**
     * 同步异常
     */
    int EXCEPTION = 3;

    /**
     * 同步对象在{@linkplain com.harmony.umbrella.ws.Context}中的key
     */
    String SYNC_OBJECT = Proxy.class.getName() + ".SYNC_OBJECT";

    /**
     * 将待同步的对象进行同步
     * 
     * @param obj
     *            待同步的对象
     * @return true同步成功
     */
    boolean sync(T obj);

    /**
     * 将待同步的对象进行同步
     * 
     * @param obj
     *            待同步的对象
     * @param properties
     *            同步的可利用属性，会放置在同步的上下文中.(EJB情况下需考虑对传出对象的序列化情况)
     * @return true同步成功
     */
    boolean sync(T obj, Map<String, Object> properties);

    /**
     * 遍历所有待同步的对象，循环同步
     * <p>
     * 效果如下：
     * 
     * <pre>
     * for (T obj : objs) {
     *     sync(obj);
     * }
     * </pre>
     * 
     * @param objs
     *            待同步的objs
     * @see Proxy#sync(Object)
     */
    void sync(List<T> objs);

    /**
     * 遍历所有待同步的对象，循环同步
     * 
     * @param objs
     *            待同步的objs
     * @param properties
     *            同步的可利用属性，会放置在同步的上下文中.(EJB情况下需考虑对传出对象的序列化情况)
     * @see Proxy#sync(List)
     * @see Proxy#sync(Object)
     */
    void sync(List<T> objs, Map<String, Object> properties);

    /**
     * 将所有待同步的对象在一次同步中传输(将所有的待同步对象在一次消息传递中传递出去)
     * 
     * @param objs
     *            待同步的objs
     */
    boolean syncInBatch(List<T> objs);

    /**
     * 将所有待同步的对象在一次同步中传输(将所有的待同步对象在一次消息传递中传递出去)
     * 
     * @param objs
     *            待同步的objs
     * @param properties
     *            同步的可利用属性，会放置在同步的上下文中.(EJB情况下需考虑对传出对象的序列化情况)
     * @see #syncInBatch(List)
     */
    boolean syncInBatch(List<T> objs, Map<String, Object> properties);

}
