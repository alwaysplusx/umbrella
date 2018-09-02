package com.harmony.umbrella.data.util;

import com.harmony.umbrella.data.query.QueryException;
import com.harmony.umbrella.data.result.ColumnResult;
import com.harmony.umbrella.data.result.SelectionAndResult;
import org.springframework.util.Assert;

import javax.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询工具类
 */
public abstract class QueryUtils {

    private QueryUtils() {
    }

    public static StringExpression toStringExpression(String name) {
        Assert.notNull(name, "expression not allow null");

        String function = null;
        String expression = name;

        int left = name.indexOf("(");
        int right = name.indexOf(")");
        if (left != -1 && right != -1) {
            function = name.substring(0, left).trim();
            expression = name.substring(left + 1, name.length() - 1).trim();
        }

        return new StringExpression(function, expression);
    }

    public static SelectionAndResult toSelectionAndResult(List<Selection> selections, Object result) {
        if (selections.size() == 1) {
            return new SelectionAndResultImpl(new ColumnResultImpl(0, selections.get(0), result));
        }

        if (!result.getClass().isArray() || selections.size() != ((Object[]) result).length) {
            throw new QueryException("select result not match " + selections.size());
        }

        int size = selections.size();
        List<ColumnResult> columnResults = new ArrayList<>(selections.size());
        Object[] resultArray = (Object[]) result;
        for (int i = 0; i < size; i++) {
            columnResults.add(new ColumnResultImpl(i, selections.get(i), resultArray[i]));
        }
        return new SelectionAndResultImpl(columnResults);
    }

}
