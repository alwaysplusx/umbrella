package com.harmony.umbrella.data.result;

import javax.persistence.criteria.Selection;

public interface ColumnResult {

    Selection<?> getSelection();

    Object getResult();

    Class<?> getJavaType();

    String getName();

    int getIndex();

}