package com.harmony.umbrella.examples.data.dao;

import com.harmony.umbrella.data.dao.JpaDao;
import com.harmony.umbrella.examples.data.persistence.Student;

/**
 * @author wuxii@foxmail.com
 */
public interface StudentDao extends JpaDao<Student, Long> {

}
