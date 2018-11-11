package com.harmony.umbrella.data;

import com.harmony.umbrella.data.domain.Student;
import com.harmony.umbrella.data.model.SelectionModel;
import com.harmony.umbrella.data.query.CellResult;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.Result;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wuxii
 */
public class StreamResultTest {

    public static void main(String[] args) {
        JpaQueryBuilder<Student> builder = new JpaQueryBuilder<>();
        List<String> names = builder
                .execute()
                .getResultList(SelectionModel.of("name"))
                .stream()
                .map(Result::firstCellResult)
                .map(CellResult::stringValue)
                .collect(Collectors.toList());
    }

}
