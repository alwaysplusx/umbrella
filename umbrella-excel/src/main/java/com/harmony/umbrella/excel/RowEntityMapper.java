package com.harmony.umbrella.excel;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.util.ReflectionUtils;

import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.excel.annotation.ExcelColumn;
import com.harmony.umbrella.excel.cell.CellResolverFactory;
import com.harmony.umbrella.util.ClassFilterFeature;
import com.harmony.umbrella.util.MemberUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 通过列与字段的基础配置达到将excel文件中的行数据转为对应的entity
 * <p>
 * 即可以说excel中的一行数据为一个entity对象
 * <p>
 * PS:提供通过注解的方式支持配置
 *
 * @author wuxii@foxmail.com
 * @see ExcelColumn
 */
public class RowEntityMapper<T> implements RowVisitor {

    private Class<T> entityClass;

    // 最终直接使用的是headerIndex->fieldName, headerName->fieldName是中间过程的map
    private final Map<String, String> headerNameToFieldNameMap = new HashMap<String, String>();

    // 如果有设置headerName->fieldName的对应关系则覆盖原来的headerIndex->fieldName
    private final Map<Integer, String> headerIndexToFieldNameMap = new HashMap<Integer, String>();

    private final List<T> result = new ArrayList<T>();

    @SuppressWarnings("rawtypes")
    private final List<CellResolver> cellResolvers = new ArrayList<CellResolver>();

    private int startColumn = 0;

    private int endColumn = -1;

    @SuppressWarnings("rawtypes")
    public RowEntityMapper(Class<T> entityClass, Map<Integer, String> headerIndexToFieldNameMap, List<CellResolver> cellResolvers) {
        this.entityClass = entityClass;
        this.cellResolvers.addAll(cellResolvers);
        this.headerIndexToFieldNameMap.putAll(headerIndexToFieldNameMap);
    }

    @Override
    public void visitHeader(int header, Row row) {
        // 如果有设置headerName->fieldName的对应关系则覆盖原来的headerIndex->fieldName
        // 最终直接使用的是headerIndex->fieldName, headerName->fieldName是中间过程的map
        if (!headerNameToFieldNameMap.isEmpty()) {
            for (int i = startColumn, max = getMaxColumnNumber(row); i < max; i++) {
                String headerName = ExcelUtil.getStringCellValue(row.getCell(i));
                String fieldName = headerNameToFieldNameMap.get(headerName);
                if (StringUtils.isNotBlank(fieldName)) {
                    headerIndexToFieldNameMap.put(i, fieldName);
                }
            }
        }
    }

    @Override
    public boolean visitRow(int rowNum, Row row) {
        T entity = newEntity();
        for (int i = startColumn, max = getMaxColumnNumber(row); i < max; i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                Member member = MemberUtils.accessMember(entityClass, headerIndexToFieldNameMap.get(i));
                CellResolver<?> cellResolver = getCellResolver(member);
                member.set(entity, cellResolver.resolve(cell.getRowIndex(), cell.getColumnIndex(), cell));
            }
        }
        result.add(entity);
        return true;
    }

    public Iterator<T> iteratorEntity() {
        return result.iterator();
    }

    public T[] getEntities() {
        T[] array = (T[]) Array.newInstance(entityClass, result.size());
        return result.toArray(array);
    }

    @SuppressWarnings("rawtypes")
    private CellResolver<?> getCellResolver(Member member) {
        Field field = member.getField();
        ExcelColumn ann = null;
        if (field != null && (ann = field.getAnnotation(ExcelColumn.class)) != null) {
            // 存在定制化的cellResolver
            Class<? extends CellResolver> cellResolverClass = ann.cellResolver();
            if (ClassFilterFeature.NEWABLE.accept(cellResolverClass)) {
                try {
                    return cellResolverClass.newInstance();
                } catch (Exception e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
            }
        }
        CellResolver<?> cellResolver = CellResolverFactory.createCellResolver(field.getType());
        if (cellResolver != null) {
            return cellResolver;
        }
        throw new IllegalArgumentException(member.getName() + " no suitable cell resolver to use");
    }

    private T newEntity() {
        try {
            return entityClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("unable create entity " + entityClass);
        }
    }

    private int getMaxColumnNumber(Row row) {
        if (endColumn == -1) {
            return row.getLastCellNum();
        }
        return endColumn;
    }

    /**
     * 为了方法的调用顺序做的扩展方法,直接通过visitor读取sheet, 将读取后的结构存在visitor中,并最终返回给调用者
     *
     * @param sheet
     *            读取的表格
     */
    public void parse(Sheet sheet) {
        parse(sheet, 0, 1);
    }

    public void parse(Sheet sheet, int header) {
        parse(sheet, header, 0);
    }

    public void parse(Sheet sheet, int header, int startRow) {
        new SheetReader(sheet, header, startRow).read(this);
    }

    // factory method

    /**
     * 检查注解配置,将注解配置的行与excel中的行对应起来
     *
     * @param entityClass
     *            映射为的对象
     * @param <T>
     *            对象泛型
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <T> RowEntityMapper<T> createByClass(Class<T> entityClass) {
        Map<Integer, String> headerIndexToFieldNameMap = new HashMap<Integer, String>();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
            headerIndexToFieldNameMap.put(ann.value(), field.getName());
        }
        if (headerIndexToFieldNameMap.isEmpty()) {
            throw new IllegalArgumentException("entity class " + entityClass.getName() + " not mapped any @ExcelColumn");
        }
        return new RowEntityMapper<T>(entityClass, headerIndexToFieldNameMap, new ArrayList<CellResolver>());
    }

    /**
     * 检查注解配置,将注解配置的行与excel中的行对应起来
     *
     * @param entityClass
     *            映射为的对象
     * @param <T>
     *            对象泛型
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <T> RowEntityMapper<T> createByClass(Class<T> entityClass, List<CellResolver> cellResolvers) {
        Map<Integer, String> headerIndexToFieldNameMap = new HashMap<Integer, String>();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            ExcelColumn ann = field.getAnnotation(ExcelColumn.class);
            headerIndexToFieldNameMap.put(ann.value(), field.getName());
        }
        if (headerIndexToFieldNameMap.isEmpty()) {
            throw new IllegalArgumentException("entity class " + entityClass.getName() + " not mapped any @ExcelColumn");
        }
        return new RowEntityMapper<T>(entityClass, headerIndexToFieldNameMap, cellResolvers);
    }

}
