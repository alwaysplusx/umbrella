package com.harmony.umbrella.query.jpa;

import com.harmony.umbrella.query.Path;
import com.harmony.umbrella.query.PathFunction;
import com.harmony.umbrella.query.PathFunctionResolver;
import com.harmony.umbrella.query.SortSupplier;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JpaSortBuilder<DOMAIN> implements SortSupplier {

    private SortSupplier sortSupplier;
    private PathFunctionResolver pathFunctionResolver;

    public JpaSortBuilder() {
        this(Sort::unsorted, new PathFunctionResolver());
    }

    private JpaSortBuilder(SortSupplier sortSupplier, PathFunctionResolver pathFunctionResolver) {
        this.sortSupplier = sortSupplier;
        this.pathFunctionResolver = pathFunctionResolver;
    }

    public JpaSortBuilder<DOMAIN> sort(SortSupplier sort) {
        return nextBuilder(sort);
    }

    public JpaSortBuilder<DOMAIN> sort(Sort sort) {
        return nextBuilder(() -> sort);
    }

    @SafeVarargs
    public final JpaSortBuilder<DOMAIN> desc(Path<DOMAIN>... path) {
        return nextBuilder(newSort(Direction.DESC, path));
    }

    @SafeVarargs
    public final JpaSortBuilder<DOMAIN> asc(Path<DOMAIN>... path) {
        return nextBuilder(newSort(Direction.ASC, path));
    }

    @SafeVarargs
    public final <T> JpaSortBuilder<DOMAIN> desc(PathFunction<DOMAIN, T>... pathFunction) {
        return nextBuilder(newSort(Direction.DESC, pathFunction));
    }

    @SafeVarargs
    public final <T> JpaSortBuilder<DOMAIN> asc(PathFunction<DOMAIN, T>... pathFunction) {
        return nextBuilder(newSort(Direction.ASC, pathFunction));
    }

    protected <T> SortSupplier newSort(Direction direction, PathFunction<DOMAIN, T>... path) {
        List<Path<?>> paths = Stream.of(path).map(pathFunctionResolver::resolve).collect(Collectors.toList());
        return newSort(direction, paths);
    }

    protected SortSupplier newSort(Direction direction, Path<DOMAIN>... path) {
        return newSort(direction, Arrays.asList(path));
    }

    protected SortSupplier newSort(Direction direction, List<Path<?>> paths) {
        List<Path<?>> unmodifiablePaths = Collections.unmodifiableList(paths);
        Function<String, Order> directionFunction = direction == Direction.ASC ? Order::asc : Order::desc;
        return () -> Sort.by(
                unmodifiablePaths
                        .stream()
                        .map(Path::getColumn)
                        .map(directionFunction)
                        .collect(Collectors.toList())
        );
    }

    protected JpaSortBuilder<DOMAIN> nextBuilder(SortSupplier nextSort) {
        return new JpaSortBuilder<>(SortSupplier.and(sortSupplier, nextSort), pathFunctionResolver);
    }

    @Override
    public Sort get() {
        return sortSupplier.get();
    }

}
