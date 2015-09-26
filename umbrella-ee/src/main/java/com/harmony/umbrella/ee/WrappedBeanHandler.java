/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.ee;

/**
 * 各种不同的容器对session bean的封装不同， 在用handler方式将bean包装拆解
 * 
 * @author wuxii@foxmail.com
 */
public interface WrappedBeanHandler {

    /**
     * 判断对应的bean是否为被包裹的bean
     * 
     * @param bean
     *            待检测的bean
     * @return if return true is wrapped bean
     */
    boolean isWrappedBean(Object bean);

    /**
     * 拆解bean
     * 
     * @param bean
     *            待拆解的bean
     * @return 拆解后的bean
     */
    Object unwrap(Object bean);

}
