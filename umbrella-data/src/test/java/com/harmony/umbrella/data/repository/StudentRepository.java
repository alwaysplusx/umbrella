package com.harmony.umbrella.data.repository;

import com.harmony.umbrella.data.domain.Student;
import org.springframework.stereotype.Repository;

/**
 * @author wuxii
 */
@Repository
public interface StudentRepository extends QueryableRepository<Student, Long> {
}
