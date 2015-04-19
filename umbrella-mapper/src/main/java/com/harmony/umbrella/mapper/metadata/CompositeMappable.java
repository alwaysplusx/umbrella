/*
 * Copyright 2013-2015 wuxii@foxmail.com.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.harmony.umbrella.mapper.metadata;

/**
 * 组合映射对象<p>{@linkplain ClassMappable}对象类有些字段既可以表示单个{@linkplain RowMappable}又可以
 * 表示为更深层的字段
 * 
 * <pre> 
 * class A { 
 *     private B b;
 * }
 * class B{
 *     private String name;
 * }
 * </pre>
 * 类似的A中的B则可以表示为混合的映射对象
 * 
 * @author wuxii@foxmail.com
 */
public interface CompositeMappable extends ClassMappable, RowMappable {

}
