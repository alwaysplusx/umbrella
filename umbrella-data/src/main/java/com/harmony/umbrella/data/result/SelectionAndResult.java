package com.harmony.umbrella.data.result;

/**
 * @author wuxii
 */
public interface SelectionAndResult extends Iterable<ColumnResult> {

    ColumnResult get(int index);

    ColumnResult get(String name);

}