package com.harmony.umbrella.data.bond;

import java.util.Collection;

import com.harmony.umbrella.data.domain.Sort;

/**
 * {@linkplain Bond}条件生成类(工厂类)
 * 
 * @author wuxii
 *
 */
public interface BondBuilder {

    /**
     * 生成equal条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    Bond equal(String name, Object value);

    /**
     * 生成not equal条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    Bond notEqual(String name, Object value);

    /**
     * 生成in条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param c
     *            值
     * @return {@linkplain Bond}
     */
    Bond in(String name, Collection<?> c);

    /**
     * 生成in条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param values
     *            值
     * @return {@linkplain Bond}
     */
    Bond in(String name, Object... values);

    /**
     * 生成not in条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param c
     *            值
     * @return {@linkplain Bond}
     */
    Bond notIn(String name, Collection<?> c);

    /**
     * 生成not in条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param values
     *            值
     * @return {@linkplain Bond}
     */
    Bond notIn(String name, Object... values);

    /**
     * 生成is null条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @return {@linkplain Bond}
     */
    Bond isNull(String name);

    /**
     * 生成is not null条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @return {@linkplain Bond}
     */
    Bond isNotNull(String name);

    /**
     * 生成like条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    Bond like(String name, String value);

    /**
     * 生成not like条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    Bond notLike(String name, String value);

    /**
     * 生成great equal的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    Bond ge(String name, Object value);

    /**
     * 生成great than的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    Bond gt(String name, Object value);

    /**
     * 生成less equal的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    Bond le(String name, Object value);

    /**
     * 生成less than的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param value
     *            值
     * @return {@linkplain Bond}
     */
    Bond lt(String name, Object value);

    /**
     * 生成between条件的{@linkplain Bond}
     * 
     * @param name
     *            字段名称
     * @param left
     *            between左边值
     * @param right
     *            between右边值
     * @return {@linkplain Bond}
     */
    Bond between(String name, Object left, Object right);

    /**
     * 字段asc排序
     * 
     * @param name
     * @return {@linkplain Sort}
     */
    Sort asc(String name);

    /**
     * 字段desc排序
     * 
     * @param name
     * @return {@linkplain Sort}
     */
    Sort desc(String name);
}
