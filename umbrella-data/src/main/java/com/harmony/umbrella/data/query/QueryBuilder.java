package com.harmony.umbrella.data.query;

import static com.harmony.umbrella.data.CompositionType.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import com.harmony.umbrella.data.CompositionType;
import com.harmony.umbrella.data.Operator;
import com.harmony.umbrella.data.query.specs.ConditionSpecification;
import com.harmony.umbrella.data.query.specs.ExperssionSpecification;
import com.harmony.umbrella.data.util.QueryUtils;

/**
 * 查询构建builder
 * <p>
 * exampel:需要构建如下的sql/jpql
 * <p>
 * select * from User where name='foo' or nickname like '%foo';
 * 
 * <pre>
 * 
 * User user = new QueryBuilder()//
 *         .equal("name", "foo")//
 *         .or()//
 *         .like("nickname", "%foo")//
 *         .getSingleResult();//
 * </pre>
 * <p>
 * <b>注意: 在构建查询时builder有严格的顺序性</b>
 * 
 * @author wuxii@foxmail.com
 */
public class QueryBuilder<T extends QueryBuilder<T, M>, M> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final transient Stack<Bind> queryStack = new Stack<Bind>();
    private final transient List<CompositionSpecification> temp = new ArrayList<CompositionSpecification>();

    protected transient EntityManager entityManager;

    protected boolean autoEnclose = true;
    protected boolean strictMode;

    /**
     * 当前查询条件与上个查询条件的连接条件, 当前查询条件被添加完成后将被清空
     * <p>
     * 清空的情况包括: 1. {@linkplain #addSpecication(Specification)} 2.
     * {@linkplain #start(CompositionType)}
     * 
     */
    protected CompositionType assembleType;

    protected Class<M> entityClass;

    protected Sort sort;
    protected int pageNumber = -1;
    protected int pageSize = -1;

    protected Set<String> grouping;
    protected FetchAttributes fetchAttributes;
    protected JoinAttributes joinAttributes;
    protected Specification specification;

    protected int queryFeature = 0;

    // query property

    public QueryBuilder() {
    }

    public QueryBuilder(EntityManager entityManager) {
        this(null, entityManager);
    }

    public QueryBuilder(Class<M> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }

    /**
     * 设置jpa entityManager
     * 
     * @param entityManager
     *            entity manager
     * @return this
     */
    public T withEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        return (T) this;
    }

    /**
     * 设置查询条件, 通过此方法设置条件后前所有条件将会被清除
     * 
     * @param specification
     *            查询条件
     * @return this
     */
    public T withSpecification(Specification<M> specification) {
        clear();
        this.specification = specification;
        return (T) this;
    }

    /**
     * 设置分页条件, 设置后将清除原排序条件
     * 
     * @param pageable
     *            分页条件
     * @return this
     */
    public T withPageable(Pageable pageable) {
        this.pageNumber = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
        this.sort = pageable.getSort();
        return (T) this;
    }

    /**
     * 启用/禁用自动匹配括号, default is enabled
     * 
     * @return this
     */
    public T autoEnclose(boolean flag) {
        this.autoEnclose = flag;
        return (T) this;
    }

    /**
     * 启用/禁用查询严格校验模式
     * 
     * @param strictMode
     *            严格校验模式flag
     * @return true is strictMode
     */
    public T strictMode(boolean strictMode) {
        this.strictMode = strictMode;
        return (T) this;
    }

    /**
     * 设置查询entity(表)
     * 
     * @param entityClass
     *            实体
     * @return this
     */
    public T from(Class<M> entityClass) {
        this.entityClass = entityClass;
        return (T) this;
    }

    // bundle

    /**
     * 将查询条件打包
     * 
     * @return 查询条件包
     */
    public QueryBundle<M> bundle() {
        finishQuery();
        final QueryBundleImpl<M> o = new QueryBundleImpl<M>();
        final QueryBuilder b = this;
        o.entityClass = b.entityClass;
        o.pageNumber = b.pageNumber;
        o.pageSize = b.pageSize;
        o.sort = b.sort;
        o.specification = b.specification;
        o.fetchAttributes = b.fetchAttributes;
        o.joinAttributes = b.joinAttributes;
        o.queryFeature = b.queryFeature;
        o.grouping = b.grouping;
        return o;
    }

    /**
     * 恢复查询条件
     * 
     * @param bundle
     *            查询条件包
     * @return this
     */
    public T unbundle(QueryBundle<M> bundle) {
        clear();
        this.entityClass = bundle.getEntityClass();
        this.pageNumber = bundle.getPageNumber();
        this.pageSize = bundle.getPageSize();
        this.sort = bundle.getSort();
        this.specification = bundle.getSpecification();
        this.fetchAttributes = bundle.getFetchAttributes();
        this.joinAttributes = bundle.getJoinAttributes();
        this.queryFeature = bundle.getQueryFeature();
        this.grouping = new LinkedHashSet<>(bundle.getGrouping() == null ? Collections.emptyList() : bundle.getGrouping());
        return (T) this;
    }

    // query condition method

    /**
     * 设置equal查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T equal(String name, Object value) {
        return addCondition(name, value, Operator.EQUAL);
    }

    /**
     * 设置not equal查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T notEqual(String name, Object value) {
        return addCondition(name, value, Operator.NOT_EQUAL);
    }

    /**
     * 设置like查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T like(String name, Object value) {
        return addCondition(name, value, Operator.LIKE);
    }

    /**
     * 设置not like查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T notLike(String name, Object value) {
        return addCondition(name, value, Operator.NOT_LIKE);
    }

    /**
     * 设置in查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T in(String name, Object value) {
        return addCondition(name, value, Operator.IN);
    }

    /**
     * 设置in查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T in(String name, Collection<?> value) {
        return addCondition(name, value, Operator.IN);
    }

    /**
     * 设置in查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T in(String name, Object... value) {
        return addCondition(name, value, Operator.IN);
    }

    /**
     * 设置not in查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T notIn(String name, Object value) {
        return addCondition(name, value, Operator.NOT_IN);
    }

    /**
     * 设置not in查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T notIn(String name, Collection<?> value) {
        return addCondition(name, value, Operator.NOT_IN);
    }

    /**
     * 设置not in查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T notIn(String name, Object... value) {
        return addCondition(name, value, Operator.NOT_IN);
    }

    /**
     * 设置between查询条件
     * 
     * @param name
     *            字段
     * @param left
     *            最小值
     * @param right
     *            最大值
     * @return this
     */
    public T between(String name, Object left, Object right) {
        return start(assembleType == null ? AND : assembleType)//
                .addCondition(name, left, Operator.GREATER_THAN_OR_EQUAL)//
                .addCondition(name, right, Operator.LESS_THAN_OR_EQUAL)//
                .end();
    }

    /**
     * 设置not between查询条件
     * 
     * @param name
     *            字段
     * @param left
     *            最小值
     * @param right
     *            最大值
     * @return this
     */
    public T notBetween(String name, Object left, Object right) {
        return start(assembleType == null ? AND : assembleType)//
                .addCondition(name, left, Operator.LESS_THAN)//
                .addCondition(name, right, Operator.GREATER_THAN)//
                .end();
    }

    /**
     * 设置大于条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T greatThen(String name, Object value) {
        return addCondition(name, value, Operator.GREATER_THAN);
    }

    /**
     * 设置大于等于条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T greatEqual(String name, Object value) {
        return addCondition(name, value, Operator.GREATER_THAN_OR_EQUAL);
    }

    /**
     * 设置小于条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T lessThen(String name, Object value) {
        return addCondition(name, value, Operator.LESS_THAN);
    }

    /**
     * 设置小于等于条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @return this
     */
    public T lessEqual(String name, Object value) {
        return addCondition(name, value, Operator.LESS_THAN_OR_EQUAL);
    }

    /**
     * 设置is null条件
     * 
     * @param name
     *            字段
     * @return this
     */
    public T isNull(String name) {
        return addCondition(name, null, Operator.NULL);
    }

    /**
     * 设置is not null条件
     * 
     * @param name
     *            字段
     * @return this
     */
    public T isNotNull(String name) {
        return addCondition(name, null, Operator.NOT_NULL);
    }

    /**
     * 两个字段的条件查询
     * 
     * @param left
     *            字段1
     * @param right
     *            字段2
     * @param operator
     *            条件
     * @return this
     */
    public T expression(String left, String right, Operator operator) {
        return addExpressionCodition(left, right, operator);
    }

    /**
     * 设置条件的连接条件and
     * 
     * @return this
     */
    public T and() {
        this.assembleType = AND;
        return (T) this;
    }

    /**
     * 设置条件的连接条件or
     * 
     * @return this
     */
    public T or() {
        this.assembleType = OR;
        return (T) this;
    }

    /**
     * 增加查询条件
     * 
     * @param name
     *            字段
     * @param value
     *            值
     * @param operator
     *            条件类型
     * @return this
     */
    public T addCondition(String name, Object value, Operator operator) {
        return (T) addSpecication(new ConditionSpecification<>(name, value, operator));
    }

    /**
     * 增加表达式的查询条件
     * 
     * @param left
     *            字段1
     * @param right
     *            字段2
     * @param operator
     *            条件类型
     * @return this
     */
    public T addExpressionCodition(String left, String right, Operator operator) {
        return (T) addSpecication(new ExperssionSpecification<>(left, right, operator));
    }

    /**
     * 在全局条件中增加一条查询条件, 当前的条件与前查询条件的连接类型为{@linkplain #assembleType}.
     * 在添加完成后将清空暂存的连接类型
     * 
     * @param spec
     *            条件
     * @return this
     */
    protected final T addSpecication(Specification spec) {
        final CompositionType type = assembleType;
        this.assembleType = null;
        if (type == null && strictMode) {
            throw new IllegalStateException("assemble type not set, or disabled statict mode");
        }
        currentBind().add(spec, type == null ? AND : type);
        return (T) this;
    }

    protected final Bind currentBind() {
        if (queryStack.isEmpty()) {
            start();
        }
        return queryStack.peek();
    }

    protected void finishQuery() {
        if (!queryStack.isEmpty()) {
            if (!autoEnclose) {
                throw new IllegalStateException("query not finish, please turn enclosed on");
            }
            for (int i = 0, max = queryStack.size(); i < max; i++) {
                end();
            }
        }
        if (specification == null) {
            if (QueryFeature.DISJUNCTION.isEnable(queryFeature) && QueryFeature.CONJUNCTION.isEnable(queryFeature)) {
                throw new IllegalStateException("confusion default condition, " + queryFeature);
            }
            if (QueryFeature.DISJUNCTION.isEnable(queryFeature)) {
                this.specification = QueryUtils.disjunction();
            }
            if (QueryFeature.CONJUNCTION.isEnable(queryFeature)) {
                this.specification = QueryUtils.conjunction();
            }
        }
    }

    // paging

    /**
     * 设置分页条件
     * 
     * @param pageNumber
     *            页码
     * @param pageSize
     *            页面条数
     * @return this
     */
    public T paging(int pageNumber, int pageSize) {
        if (QueryUtils.isPaging(pageNumber, pageSize)) {
            throw new IllegalArgumentException("invalid page setting page=" + pageNumber + ", size=" + pageSize);
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        return (T) this;
    }

    // sort
    /**
     * 设置排序条件
     * 
     * @param sort
     *            排序条件
     * @return this
     */
    public T sort(Sort sort) {
        this.sort = sort;
        return (T) this;
    }

    /**
     * 设置升序排序条件
     * 
     * @param name
     *            排序的字段
     * @return this
     */
    public T asc(String... name) {
        return orderBy(Direction.ASC, name);
    }

    /**
     * 设置降序排序条件
     * 
     * @param name
     *            排序字段
     * @return this
     */
    public T desc(String... name) {
        return orderBy(Direction.DESC, name);
    }

    /**
     * 设置排序条件
     * 
     * @param dir
     *            排序方向
     * @param name
     *            排序字段
     * @return this
     */
    public T orderBy(Direction dir, String... name) {
        Order[] orders = new Order[name.length];
        for (int i = 0, max = orders.length; i < max; i++) {
            orders[i] = new Order(dir, name[i]);
        }
        return (T) orderBy(orders);
    }

    /**
     * 设置排序条件
     * 
     * @param order
     *            排序条件
     * @return this
     * @see Order
     */
    public T orderBy(Order... order) {
        if (order.length > 0) {
            this.sort = (this.sort == null) ? new Sort(order) : this.sort.and(new Sort(order));
        }
        return (T) this;
    }

    // query feature

    /**
     * 设置查询的distinct属性为启用
     * 
     * @return this
     */
    public T distinct() {
        return distinct(true);
    }

    /**
     * 设置查询的distinct属性为禁用
     * 
     * @return this
     */
    public T notDistinct() {
        return distinct(false);
    }

    /**
     * 设置查询的distinct属性
     * 
     * @param distinct
     *            设置状态位
     * @return this
     */
    public T distinct(boolean distinct) {
        return config(QueryFeature.DISTINCT, distinct);
    }

    /**
     * 设置允许空条件的list查询
     * 
     * @return this
     */
    public T allowFullTableQuery() {
        return enable(QueryFeature.FULL_TABLE_QUERY);
    }

    /**
     * 设置禁止空条件的list查询
     * 
     * @return this
     */
    public T forbidFullTableQuery() {
        return disable(QueryFeature.FULL_TABLE_QUERY);
    }

    /**
     * 配置查询属性
     * 
     * @param feature
     *            查询属性
     * @param flag
     *            属性状态
     * @return this
     */
    public T config(QueryFeature feature, boolean flag) {
        this.queryFeature = QueryFeature.config(queryFeature, feature, flag);
        return (T) this;
    }

    /**
     * 启用查询属性
     * 
     * @param feature
     *            查询属性
     * @return this
     */
    public T enable(QueryFeature feature) {
        this.queryFeature = QueryFeature.config(queryFeature, feature, true);
        return (T) this;
    }

    /**
     * 禁用查询属性
     * 
     * @param feature
     *            查询属性
     * @return this
     */
    public T disable(QueryFeature feature) {
        this.queryFeature = QueryFeature.config(queryFeature, feature, false);
        return (T) this;
    }

    /**
     * 设置fetch的字段
     * 
     * @param names
     *            fetch attr
     * @return this
     */
    public T fetch(String... names) {
        for (String name : names) {
            fetch(name, JoinType.INNER);
        }
        return (T) this;
    }

    /**
     * 设置fetch的字段
     * 
     * @param joinType
     *            fetch type
     * @param names
     *            fetch attr
     * @return this
     */
    public T fetch(String name, JoinType joinType) {
        if (this.fetchAttributes == null) {
            this.fetchAttributes = new FetchAttributes();
        }
        this.fetchAttributes.attrs.add(new Attribute(name, joinType));
        return (T) this;
    }

    /**
     * 设置查询时候需要join的表
     * 
     * @param names
     *            join tables
     * @return this
     */
    public T join(String... names) {
        for (String name : names) {
            join(name, JoinType.INNER);
        }
        return (T) this;
    }

    /**
     * 设置查询所需要join的表
     * 
     * @param joinType
     *            join type
     * @param names
     *            join tables
     * @return this
     */
    public T join(String name, JoinType joinType) {
        if (this.joinAttributes == null) {
            this.joinAttributes = new JoinAttributes();
        }
        this.joinAttributes.attrs.add(new Attribute(name, joinType));
        return (T) this;
    }

    // group by

    public T groupBy(String... names) {
        if (this.grouping == null) {
            this.grouping = new LinkedHashSet<>();
        }
        this.grouping.addAll(Arrays.asList(names));
        return (T) this;
    }

    // enclose method

    /**
     * 匹配括号的开始
     * 
     * @return this
     */
    public T start() {
        return start(assembleType == null ? AND : assembleType);
    }

    /**
     * 匹配括号的开始
     * 
     * @param compositionType
     *            与上个查询条件的连接条件
     * @return this
     */
    public T start(CompositionType compositionType) {
        queryStack.push(new Bind(compositionType));
        assembleType = null;
        return (T) this;
    }

    /**
     * 匹配括号的结束
     * 
     * @return this
     */
    public T end() {
        Bind bind = queryStack.pop();
        if (!bind.isEmpty()) {
            temp.add(bind);
        }
        if (queryStack.isEmpty()) {
            CompositionSpecification[] css = temp.toArray(new CompositionSpecification[0]);
            temp.clear();
            for (int i = css.length; i > 0; i--) {
                CompositionSpecification cs = css[i - 1];
                specification = cs.getCompositionType().combine(specification, cs);
            }
        }
        return (T) this;
    }

    /**
     * 清空所有查询构建条件
     */
    public void clear() {
        queryStack.clear();
        temp.clear();
        grouping.clear();
        assembleType = null;
        fetchAttributes = null;
        joinAttributes = null;
        specification = null;
        sort = null;
        entityClass = null;
        pageNumber = 0;
        pageSize = 0;
        queryFeature = 0;
    }

    // result

    /**
     * 执行查询获取查询结果
     * 
     * @return 查询结果
     */
    public QueryResult<M> execute() {
        Assert.notNull(entityManager, "Can't execute query, because entity manager not exists! Please set entity manager before execute.");
        return new QueryResultImpl<M>(entityManager, bundle());
    }

    /**
     * 执行查询获取符合条件的单个结果
     * 
     * @return 查询结果
     */
    public M getSingleResult() {
        return execute().getSingleResult();
    }

    /**
     * 执行查询获取第一个符合条件的结果
     * 
     * @return 符合条件的第一个结果
     */
    public M getFirstResult() {
        return execute().getFirstResult();
    }

    /**
     * 执行查询获取符合条件的所有结果集
     * 
     * @return 符合条件的结果集
     */
    public List<M> getResultList() {
        return execute().getResultList();
    }

    /**
     * 执行查询获取分页查询结果
     * 
     * @return 分页结果集
     */
    public Page<M> getResultPage() {
        return execute().getResultPage();
    }

    /**
     * 执行查询获取符合条件的结果数
     * 
     * @return 符合条件的结果数
     */
    public long getCountResult() {
        return execute().getCountResult();
    }

    public static final class FetchAttributes implements Serializable {

        private static final long serialVersionUID = 1L;

        public final List<Attribute> attrs = new ArrayList<Attribute>();

        private FetchAttributes() {
        }

        public List<Attribute> getAttributes() {
            return attrs;
        }
    }

    public static final class JoinAttributes implements Serializable {

        private static final long serialVersionUID = 1L;

        public final List<Attribute> attrs = new ArrayList<Attribute>();

        private JoinAttributes() {
        }

        public List<Attribute> getAttributes() {
            return attrs;
        }

    }

    public static final class Attribute implements Serializable {

        private static final long serialVersionUID = 1L;

        public final String name;
        public final JoinType joniType;

        private Attribute(String name, JoinType joniType) {
            this.name = name;
            this.joniType = joniType;
        }

        public String getName() {
            return name;
        }

        public JoinType getJoniType() {
            return joniType;
        }
    }

}
