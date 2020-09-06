package com.harmony.umbrella.query;

import java.util.List;

/**
 * @author wuxin
 */
public interface QueryBundle {

    List<QueryBundle> getSubQuery();

}
