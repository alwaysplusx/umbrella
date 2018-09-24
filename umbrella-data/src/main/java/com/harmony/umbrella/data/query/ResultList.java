package com.harmony.umbrella.data.query;

import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author wuxii
 */
public class ResultList implements Iterable<Result> {

    private final List<Result> results;

    ResultList(List<Result> resultList) {
        Assert.notNull(resultList, "result list is not allow null");
        this.results = resultList;
    }

    public int size() {
        return results.size();
    }

    public <T> List<T> convert(Class<T> resultClass) {
        return convert(ResultConverter.convertFor(resultClass));
    }

    public <T> List<T> convert(Function<Result, T> converter) {
        return results.stream().map(converter).collect(Collectors.toList());
    }

    @Override
    public Iterator<Result> iterator() {
        return results.iterator();
    }

    public Stream<Result> stream() {
        return results.stream();
    }
}
