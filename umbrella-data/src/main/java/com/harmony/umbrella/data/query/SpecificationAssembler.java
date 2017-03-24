package com.harmony.umbrella.data.query;

import static com.harmony.umbrella.data.query.SpecificationAssembler.SpecificationType.*;
import static com.harmony.umbrella.data.util.QueryUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.query.QueryResult.Selections;
import com.harmony.umbrella.data.query.specs.FetchSpecification;
import com.harmony.umbrella.data.query.specs.GrouppingSpecification;
import com.harmony.umbrella.data.query.specs.JoinSpecification;
import com.harmony.umbrella.data.query.specs.SelectionSpecification;
import com.harmony.umbrella.data.query.specs.SortSpecification;
import com.harmony.umbrella.data.util.QueryUtils;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class SpecificationAssembler<T> {

    private static final Log log = Logs.getLog();
    protected final QueryBundle<T> bundle;

    public SpecificationAssembler(QueryBundle<T> bundle) {
        this.bundle = bundle;
    }

    public Pageable getPageable() {
        int page = bundle.getPageNumber();
        int size = bundle.getPageSize();
        if (page < 0 || size < 0) {
            return null;
        }
        Sort sort = bundle.getSort();
        return sort == null ? new PageRequest(page, size) : new PageRequest(page, size, sort);
    }

    public boolean isEnable(QueryFeature feature) {
        return QueryFeature.isEnabled(bundle.getQueryFeature(), feature);
    }

    public Specification<T> assembly(SpecificationType... assemblies) {
        return assembly(null, assemblies);
    }

    public Specification<T> assembly(Selections<T> selections, SpecificationType... assemblies) {
        if (assemblies.length == 0) {
            return null;
        }
        List<SpecificationType> assemblyList = Arrays.asList(assemblies);
        List<Specification<T>> specs = new ArrayList(assemblies.length);
        if (selections != null) {
            specs.add(new SelectionSpecification<>(selections));
        }
        if (assemblyList.contains(CONDITION)) {
            if (!addSpec(specs, conditonSpec())) {
                log.warn("query condition not found");
            }
        }
        if (assemblyList.contains(FETCH)) {
            if (!addSpec(specs, fetchSpec())) {
                log.debug("query fetch attribute not found");
            }
        }
        if (assemblyList.contains(JOIN)) {
            if (!addSpec(specs, joinSpec())) {
                log.debug("query join attribute not found");
            }
        }
        if (assemblyList.contains(SORT)) {
            if (!addSpec(specs, sortSpec())) {
                log.debug("query sort not found");
            }
        }
        if (assemblyList.contains(GROUP)) {
            if (!addSpec(specs, groupingSpec())) {
                log.debug("query grouping not found");
            }
        }
        if (specs.isEmpty()) {
            return null;
        }
        return QueryUtils.all(specs.toArray(new Specification[specs.size()]));
    }

    private Specification<T> fetchSpec() {
        return bundle.getFetchAttributes() == null ? null : new FetchSpecification<T>(bundle.getFetchAttributes().attrs);
    }

    private Specification<T> joinSpec() {
        return bundle.getJoinAttributes() == null ? null : new JoinSpecification<T>(bundle.getJoinAttributes().attrs);
    }

    private Specification<T> groupingSpec() {
        return bundle.getGrouping() == null ? null : new GrouppingSpecification<>(select(bundle.getGrouping()));
    }

    private Specification<T> conditonSpec() {
        return bundle.getCondition();
    }

    private Specification<T> sortSpec() {
        return bundle.getSort() == null ? null : new SortSpecification<T>(bundle.getSort());
    }

    private boolean addSpec(List<Specification<T>> specs, Specification<T> spec) {
        return spec == null ? false : specs.add(spec);
    }

    public static enum SpecificationType {
        CONDITION, FETCH, JOIN, GROUP, SORT
    }

}
