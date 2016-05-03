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
