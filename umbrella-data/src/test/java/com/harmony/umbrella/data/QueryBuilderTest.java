package com.harmony.umbrella.data;

import com.harmony.umbrella.data.domain.ClassRoom;
import com.harmony.umbrella.data.domain.Student;
import com.harmony.umbrella.data.model.SelectionModel;
import com.harmony.umbrella.data.query.JpaQueryBuilder;
import com.harmony.umbrella.data.query.QueryFeature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.List;

/**
 * @author wuxii
 */
public class QueryBuilderTest {

    static EntityManager entityManager = Persistence.createEntityManagerFactory("umbrella").createEntityManager();

    private JpaQueryBuilder<Student> studentQueryBuilder;

    private JpaQueryBuilder<ClassRoom> classRoomQueryBuilder;

    @BeforeClass
    public static void beforeClass() {
        entityManager.getTransaction().begin();
        ClassRoom classRoom0 = newClassRoom("三年级", "一班");
        ClassRoom classRoom1 = newClassRoom("三年级", "二班");

        entityManager.persist(classRoom0);
        entityManager.persist(classRoom1);

        Student s0 = newStudent("Mary", "M", LocalDate.of(1990, 6, 28), classRoom0);
        Student s1 = newStudent("David", "F", LocalDate.of(1991, 1, 1), classRoom0);
        Student s2 = newStudent("Haney", "F", LocalDate.of(1994, 11, 21), classRoom0);
        Student s3 = newStudent("Colin", "M", LocalDate.of(1991, 12, 15), classRoom1);
        Student s4 = newStudent("Angus", "F", LocalDate.of(1992, 3, 19), classRoom1);
        Student s5 = newStudent("Josh", "M", LocalDate.of(1993, 7, 19), classRoom1);

        entityManager.persist(s0);
        entityManager.persist(s1);
        entityManager.persist(s2);
        entityManager.persist(s3);
        entityManager.persist(s4);
        entityManager.persist(s5);

        entityManager.getTransaction().commit();
    }

    @Before
    public void before() {
        studentQueryBuilder = new JpaQueryBuilder<Student>(Student.class, entityManager);
        classRoomQueryBuilder = new JpaQueryBuilder<ClassRoom>(ClassRoom.class, entityManager);
    }

    @Test
    public void testFindAll() {
        List<Student> students = studentQueryBuilder
                .enable(QueryFeature.FULL_TABLE_QUERY)
                .getAllResult();
        Assert.assertEquals(6, students.size());
    }

    @Test
    public void testCount() {
        long countResult = studentQueryBuilder.getCountResult();
        Assert.assertEquals(6, countResult);
    }

    @Test
    public void testEqual() {
        Student student = studentQueryBuilder
                .equal("name", "Mary")
                .getSingleResult();
        Assert.assertNotNull("student named Mary not found", student);
        Assert.assertEquals("Mary", student.getName());
    }

    @Test
    public void testSizeOf() {
        List<Long> result = classRoomQueryBuilder
                .sizeOf("students", 3)
                .groupBy("id")
                .execute()
                .getAllResult(SelectionModel.of("id"), Long.class);
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void testGreatThan() {
        List<Student> students = studentQueryBuilder
                .greatThen("birthday", LocalDate.of(1992, 1, 1))
                .getAllResult();
        System.out.println(students);
    }

    @Test
    public void testIsTrue() {
        List<Student> students = studentQueryBuilder
                .isTrue("enabled")
                .getAllResult();
        System.out.println(students);
    }

    @Test
    public void testBetween() {
        List<Student> students = studentQueryBuilder
                .between("birthday", LocalDate.of(1992, 1, 1), LocalDate.of(1992, 12, 31))
                .getAllResult();
        System.out.println(students);
    }

    @Test
    public void testNotBetween() {
        List<Student> students = studentQueryBuilder
                .notBetween("birthday", LocalDate.of(1992, 1, 1), LocalDate.of(1992, 12, 31))
                .getAllResult();
        System.out.println(students);
    }

    @Test
    public void testCountColumn() {
        long count = classRoomQueryBuilder
                .equal("room", "一班")
                .execute()
                .countDistinct("students.id");
        Assert.assertEquals("class 一班 students count isn't 3", 3l, count);
    }

    @Test
    public void testLike() {
        List<Student> students = studentQueryBuilder
                .like("name", "M")
                .getAllResult();
        System.out.println(students);
    }

    @Test
    public void testNotLike() {
        List<Student> students = studentQueryBuilder
                .notLike("name", "M")
                .getAllResult();
        System.out.println(students);
    }

    @Test
    public void testIn() {
        List<Student> students = studentQueryBuilder
                .in("name", "Haney", "Mary")
                .getAllResult();
        System.out.println(students);
    }

    @Test
    public void testNotIn() {
        List<Student> students = studentQueryBuilder
                .notIn("name", "Haney", "Mary")
                .getAllResult();
        System.out.println(students);
    }

    private static ClassRoom newClassRoom(String grade, String room) {
        return new ClassRoom(grade, room);
    }

    private static Student newStudent(String name, String gender, LocalDate birthday, ClassRoom classRoom) {
        return new Student(name, gender, birthday, classRoom);
    }

}
