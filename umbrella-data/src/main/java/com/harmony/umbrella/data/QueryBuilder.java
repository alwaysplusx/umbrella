package com.harmony.umbrella.data;

import com.harmony.umbrella.data.result.QueryResultImpl;
import com.harmony.umbrella.data.specs.GeneralSpecification;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.NullHandling;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.criteria.JoinType;
import java.io.Serializable;
import java.util.*;

import static com.harmony.umbrella.data.CompositionType.AND;
import static com.harmony.umbrella.data.CompositionType.OR;

/**
 * 查询构建builder
 * <p>
 * exampel:需要构建如下的sql/jpql
 * <p>
 * select * from User where name='foo' or nickname like '%foo';
 *
 * <pre>
 *
 * User user = new QueryBuilder()
 *         .equal("name", "foo")
 *         .or()
 *         .like("nickname", "%foo")
 *         .getSingleResult();
 * </pre>
 *
 * <b>注意: 在构建查询时builder有严格的顺序性</b>
 *
 * @author wuxii@foxmail.com
 */
public class QueryBuilder<T extends QueryBuilder<T, M>, M> implements Serializable {

    private static final long serialVersionUID = 1L;

    private static Pageable defaultPageable = PageRequest.of(0, 20);

    public static void setDefaultPageable(Pageable pageable) {
        Assert.notNull(pageable, "default page not allow null");
        defaultPageable = pageable;
    }

    /**
     * 查询条件栈, 一个括号内的查询条件即为一个栈值
     */
    private final transient Stack<LinkedSpecification> queryStack = new Stack<>();
    /**
     * 一个括号内暂存的组合查询条件, 在括号结束后将情况temp中的查询条件
     */
    private final transient List<CompositionSpecification> temp = new ArrayList<>();

    protected transient EntityManager entityManager;

    protected boolean strictMode;

    /**
     * 当前查询条件与上个查询条件的连接条件, 当前查询条件被添加完成后将被清空
     * <p>
     * 清空的情况包括: 1. {@linkplain #addSpecification(Specification)} 2. {@linkplain #begin(CompositionType)}
     */
    protected CompositionType compositionType;

    protected Class<M> domainClass;

    protected Sort sort;
    protected int pageNumber = defaultPageable.getPageNumber();
    protected int pageSize = defaultPageable.getPageSize();

    protected Selections grouping;
    protected FetchAttributes fetchAttributes;
    protected JoinAttributes joinAttributes;
    protected Specification<M> specification;

    protected int queryFeature = 0;

    // query property

    public QueryBuilder() {
    }

    public QueryBuilder(Class<M> domainClass) {
        this.domainClass = domainClass;
    }

    public QueryBuilder(EntityManager entityManager) {
        this(null, entityManager);
    }

    public QueryBuilder(Class<M> entityClass, EntityManager entityManager) {
        this.domainClass = entityClass;
        this.entityManager = entityManager;
    }

    /**
     * 设置jpa entityManager
     *
     * @param entityManager entity manager
     * @return this
     */
    public T withEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        return (T) this;
    }

    /**
     * 设置查询条件, 通过此方法设置条件后前所有条件将会被清除
     *
     * @param specification 查询条件
     * @return this
     */
    public T withSpecification(Specification<M> specification) {
        this.queryStack.clear();
        this.temp.clear();
        this.specification = specification;
        return (T) this;
    }

    /**
     * 设置分页条件, 设置后将清除原排序条件
     *
     * @param pageable 分页条件
     * @return this
     */
    public T withPageable(Pageable pageable) {
        this.pageNumber = pageable.getPageNumber();
        this.pageSize = pageable.getPageSize();
        this.sort = pageable.getSort();
        return (T) this;
    }

    /**
     * 启用/禁用查询严格校验模式, 严格模式用于校验当前的{@linkplain QueryBuilder#compositionType}
     *
     * @param strictMode 严格校验模式flag
     * @return true is strictMode
     */
    public T strictMode(boolean strictMode) {
        this.strictMode = strictMode;
        return (T) this;
    }

    /**
     * 设置查询entity(表)
     *
     * @param entityClass 实体
     * @return this
     */
    public T from(Class<M> entityClass) {
        this.domainClass = entityClass;
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
        o.domainClass = b.domainClass;
        o.pageNumber = b.pageNumber;
        o.pageSize = b.pageSize;
        o.sort = b.sort;
        o.condition = b.specification;
        o.fetchAttributes = b.fetchAttributes;
        o.joinAttributes = b.joinAttributes;
        o.queryFeature = b.queryFeature;
        o.grouping = b.grouping;
        return o;
    }

    /**
     * 恢复查询条件
     *
     * @param bundle 查询条件包
     * @return this
     */
    public T unbundle(QueryBundle<M> bundle) {
        clear();
        this.domainClass = bundle.getDomainClass();
        this.pageNumber = bundle.getPageNumber();
        this.pageSize = bundle.getPageSize();
        this.sort = bundle.getSort();
        this.specification = bundle.getSpecification();
        this.fetchAttributes = bundle.getFetchAttributes();
        this.joinAttributes = bundle.getJoinAttributes();
        this.queryFeature = bundle.getQueryFeature();
        this.grouping = bundle.getGrouping();
        return (T) this;
    }

    // query specification method

    /**
     * 设置equal查询条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T equal(String name, Object value) {
        return addCondition(name, value, Operator.EQUAL);
    }

    /**
     * 设置not equal查询条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T notEqual(String name, Object value) {
        return addCondition(name, value, Operator.NOT_EQUAL);
    }

    /**
     * 设置like查询条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T like(String name, Object value) {
        return like(name, value, true);
    }

    /**
     * like查询, 根据入参wild判断是否添加通配(如果值中已经含有通配%则不添加通配)
     *
     * @param name  字段
     * @param value 值
     * @param wild  是否对值进行通配
     * @return this
     */
    public T like(String name, Object value, boolean wild) {
        if (wild) {
            value = appendLikeWild(value);
        }
        return addCondition(name, value, Operator.LIKE);
    }

    /**
     * 设置not like查询条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T notLike(String name, Object value) {
        return notLike(name, value, true);
    }

    /**
     * 设置not like查询. 根据入参wild判断是否添加通配(如果值中已经含有通配%则不添加通配)
     *
     * @param name  字段
     * @param value 值
     * @param wild  是否开启通配
     * @return this
     */
    public T notLike(String name, Object value, boolean wild) {
        if (wild) {
            value = appendLikeWild(value);
        }
        return addCondition(name, value, Operator.NOT_LIKE);
    }

    /**
     * 设置in查询条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T in(String name, Object value) {
        return addCondition(name, value, Operator.IN);
    }

    /**
     * 设置in查询条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T in(String name, Collection<?> value) {
        return addCondition(name, value, Operator.IN);
    }

    /**
     * 设置in查询条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T in(String name, Object... value) {
        return addCondition(name, value, Operator.IN);
    }

    /**
     * 设置not in查询条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T notIn(String name, Object value) {
        return addCondition(name, value, Operator.NOT_IN);
    }

    /**
     * 设置not in查询条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T notIn(String name, Collection<?> value) {
        return addCondition(name, value, Operator.NOT_IN);
    }

    /**
     * 设置not in查询条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T notIn(String name, Object... value) {
        return addCondition(name, value, Operator.NOT_IN);
    }

    /**
     * 设置between查询条件
     *
     * @param name  字段
     * @param left  最小值
     * @param right 最大值
     * @return this
     */
    public T between(String name, Comparable left, Comparable right) {
        return addSpecification(GeneralSpecification.between(name, left, right));
    }

    /**
     * 设置not between查询条件
     *
     * @param name  字段
     * @param left  最小值
     * @param right 最大值
     * @return this
     */
    public T notBetween(String name, Comparable left, Comparable right) {
        return addSpecification(GeneralSpecification.notBetween(name, left, right));
    }

    /**
     * 设置大于条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T greatThen(String name, Comparable value) {
        return addCondition(name, value, Operator.GREATER_THAN);
    }

    /**
     * 设置大于等于条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T greatEqual(String name, Comparable value) {
        return addCondition(name, value, Operator.GREATER_THAN_OR_EQUAL);
    }

    /**
     * 设置小于条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T lessThen(String name, Comparable value) {
        return addCondition(name, value, Operator.LESS_THAN);
    }

    /**
     * 设置小于等于条件
     *
     * @param name  字段
     * @param value 值
     * @return this
     */
    public T lessEqual(String name, Comparable value) {
        return addCondition(name, value, Operator.LESS_THAN_OR_EQUAL);
    }

    /**
     * 设置is null条件
     *
     * @param name 字段
     * @return this
     */
    public T isNull(String name) {
        return addCondition(name, null, Operator.NULL);
    }

    /**
     * 设置is not null条件
     *
     * @param name 字段
     * @return this
     */
    public T isNotNull(String name) {
        return addCondition(name, null, Operator.NOT_NULL);
    }

    /**
     * 设置size of条件
     *
     * @param name 字段
     * @param size size
     * @return this
     */
    public T sizeOf(String name, long size) {
        return addCondition(name, size, Operator.SIZE_OF);
    }

    /**
     * 设置not size of条件
     *
     * @param name 字段
     * @param size size
     * @return
     */
    public T notSizeOf(String name, long size) {
        return addCondition(name, size, Operator.NOT_SIZE_OF);
    }

    /**
     * 设置is true条件
     *
     * @param name 字段
     * @return
     */
    public T isTrue(String name) {
        return addCondition(name, null, Operator.TRUE);
    }

    /**
     * 设置is false条件
     *
     * @param name 字段
     * @return
     */
    public T isFalse(String name) {
        return (T) addCondition(name, null, Operator.FALSE);
    }

    /**
     * 设置条件的连接条件and
     *
     * @return this
     */
    public T and() {
        this.compositionType = AND;
        return (T) this;
    }

    /**
     * 设置条件的连接条件or
     *
     * @return this
     */
    public T or() {
        this.compositionType = OR;
        return (T) this;
    }

    public T and(Specification<T> spec) {
        return and().addSpecification(spec);
    }

    public T or(Specification<T> spec) {
        return or().addSpecification(spec);
    }

    /**
     * 添加查询条件
     *
     * @param name     字段
     * @param value    值
     * @param operator 条件类型
     * @return this
     */
    public T addCondition(String name, Object value, ExpressionOperator operator) {
        return (T) addSpecification(GeneralSpecification.of(name, value, operator));
    }

    /**
     * 在全局条件中增加一条查询条件, 当前的条件与前查询条件的连接类型为{@linkplain #compositionType}. 在添加完成后将清空暂存的连接类型
     *
     * @param spec 条件
     * @return this
     */
    public T addSpecification(Specification spec) {
        getCurrentSpecification().add(spec, getAndResetCompositionType());
        return (T) this;
    }

    /**
     * 获取查询条件栈中的查询条件. (同一个括号内的查询条件)
     *
     * @return Bind
     */
    protected final LinkedSpecification getCurrentSpecification() {
        if (queryStack.isEmpty()) {
            begin();
        }
        return queryStack.peek();
    }

    protected final CompositionType getAndResetCompositionType() {
        // TODO 确保多次获取的type
        if (this.compositionType == null && strictMode && !queryStack.isEmpty() && !queryStack.get(0).isEmpty()) {
            throw new QueryException("composition type not set, or disabled strict mode");
        }
        CompositionType type = this.compositionType == null ? CompositionType.AND : this.compositionType;
        this.compositionType = null;
        return type;
    }

    /**
     * 结束查询, 将所有查询栈中的查询条件组合成为规范化的查询条件specification
     */
    protected final void finishQuery() {
        if (!queryStack.isEmpty()) {
            endAll();
        }
    }

    // paging

    /**
     * 设置分页条件
     *
     * @param pageNumber 页码
     * @param pageSize   页面条数
     * @return this
     */
    public T paging(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        return (T) this;
    }

    // sort

    /**
     * 设置排序条件
     *
     * @param sort 排序条件
     * @return this
     */
    public T sort(Sort sort) {
        this.sort = sort;
        return (T) this;
    }

    public T asc(String name, NullHandling nullHandling) {
        return orderBy(new Order(Direction.ASC, name, nullHandling));
    }

    /**
     * 设置升序排序条件
     *
     * @param name 排序的字段
     * @return this
     */
    public T asc(String... name) {
        return orderBy(Direction.ASC, name);
    }

    /**
     * 设置升序排序条件
     *
     * @param name 排序的字段
     * @return this
     */
    public T asc(List<String> name) {
        return orderBy(Direction.ASC, name);
    }

    /**
     * 降序排序
     *
     * @param name         需要排序的字段
     * @param nullHandling 空处理方式
     * @return this
     */
    public T desc(String name, NullHandling nullHandling) {
        return orderBy(new Order(Direction.DESC, name, nullHandling));
    }

    /**
     * 设置降序排序条件
     *
     * @param name 排序字段
     * @return this
     */
    public T desc(String... name) {
        return orderBy(Direction.DESC, name);
    }

    /**
     * 设置降序排序条件
     *
     * @param name 排序字段
     * @return this
     */
    public T desc(List<String> name) {
        return orderBy(Direction.DESC, name);
    }

    /**
     * 设置排序条件
     *
     * @param dir  排序方向
     * @param name 排序字段
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
     * @param dir  排序方向
     * @param name 排序字段
     * @return this
     */
    public T orderBy(Direction dir, List<String> name) {
        Order[] orders = new Order[name.size()];
        for (int i = 0, max = orders.length; i < max; i++) {
            orders[i] = new Order(dir, name.get(i));
        }
        return (T) orderBy(orders);
    }

    /**
     * 设置排序条件
     *
     * @param order 排序条件
     * @return this
     * @see Order
     */
    public T orderBy(Order... order) {
        if (order.length > 0) {
            this.sort = (this.sort == null) ? Sort.by(order) : this.sort.and(Sort.by(order));
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
     * @param distinct 设置状态位
     * @return this
     */
    public T distinct(boolean distinct) {
        return config(QueryFeature.DISTINCT, distinct);
    }

    /**
     * 配置查询属性
     *
     * @param feature 查询属性
     * @param flag    属性状态
     * @return this
     */
    public T config(QueryFeature feature, boolean flag) {
        this.queryFeature = QueryFeature.config(queryFeature, feature, flag);
        return (T) this;
    }

    /**
     * 启用查询属性
     *
     * @param feature 查询属性
     * @return this
     */
    public T enable(QueryFeature... feature) {
        for (QueryFeature f : feature) {
            this.queryFeature = QueryFeature.config(queryFeature, f, true);
        }
        return (T) this;
    }

    /**
     * 启用查询属性
     *
     * @param feature 查询属性
     * @return this
     */
    public T enable(Collection<QueryFeature> feature) {
        for (QueryFeature f : feature) {
            this.queryFeature = QueryFeature.config(queryFeature, f, true);
        }
        return (T) this;
    }

    /**
     * 禁用查询属性
     *
     * @param feature 查询属性
     * @return this
     */
    public T disable(QueryFeature... feature) {
        for (QueryFeature f : feature) {
            this.queryFeature = QueryFeature.config(queryFeature, f, false);
        }
        return (T) this;
    }

    /**
     * 设置fetch的字段
     *
     * @param names fetch attr
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
     * @param name     fetch attr
     * @param joinType fetch type
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
     * @param names join tables
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
     * @param name     join tables
     * @param joinType join type
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

    /**
     * 设置分组条件
     *
     * @param names 分组的字段
     * @return this
     */
    public T groupBy(String... names) {
        return groupBy(Arrays.asList(names));
    }

    /**
     * 设置分组条件
     *
     * @param names 分组的字段
     * @return this
     */
    public T groupBy(Collection<String> names) {
        this.grouping = Selections.of(names.toArray(new String[0]));
        return (T) this;
    }

    public T groupBy(Selections selections) {
        this.grouping = selections;
        return (T) this;
    }

    // enclose method

    /**
     * 匹配括号的开始
     *
     * @return this
     */
    public T begin() {
        return begin(getAndResetCompositionType());
    }

    /**
     * 匹配括号的开始
     *
     * @param compositionType 与上个查询条件的连接条件
     * @return this
     */
    public T begin(CompositionType compositionType) {
        queryStack.push(new LinkedSpecification(compositionType));
        return (T) this;
    }

    /**
     * 匹配括号的结束
     *
     * @return this
     */
    public T end() {
        LinkedSpecification bind = queryStack.pop();
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
     * 关闭所有打开的括号
     *
     * @return this
     */
    public T endAll() {
        while (!queryStack.isEmpty()) {
            end();
        }
        return (T) this;
    }

    /**
     * 清空所有查询构建条件
     */
    public void clear() {
        queryStack.clear();
        temp.clear();
        grouping = null;
        compositionType = null;
        fetchAttributes = null;
        joinAttributes = null;
        specification = null;
        sort = null;
        domainClass = null;
        pageNumber = defaultPageable.getPageNumber();
        pageSize = defaultPageable.getPageSize();
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
        return new QueryResultImpl<>(entityManager, bundle());
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
        return execute().getListResult();
    }

    /**
     * 获取符合条件的所有结果集(不分页)
     *
     * @return 符合条件的所有结果集
     */
    public List<M> getAllResult() {
        return execute().getAllResult();
    }

    /**
     * 执行查询获取符合条件的结果数
     *
     * @return 符合条件的结果数
     */
    public long getCountResult() {
        return execute().count();
    }

    protected static String appendLikeWild(Object value) {
        String val = value.toString();
        return val.contains("%") ? val : "%" + val + "%";
    }

    public static final class FetchAttributes implements Serializable, Iterable<Attribute> {

        private static final long serialVersionUID = 1L;

        private final List<Attribute> attrs = new ArrayList<>();

        private FetchAttributes() {
        }

        public List<Attribute> getAttributes() {
            return attrs;
        }

        @Override
        public Iterator<Attribute> iterator() {
            return attrs.iterator();
        }
    }

    public static final class JoinAttributes implements Serializable, Iterable<Attribute> {

        private static final long serialVersionUID = 1L;

        private final List<Attribute> attrs = new ArrayList<>();

        private JoinAttributes() {
        }

        public List<Attribute> getAttributes() {
            return attrs;
        }

        @Override
        public Iterator<Attribute> iterator() {
            return attrs.iterator();
        }

    }

    public static final class Attribute implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String name;
        private final JoinType joniType;

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
