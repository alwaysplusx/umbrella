package com.harmony.umbrella.data;

import com.harmony.umbrella.data.domain.ClassRoom;
import com.harmony.umbrella.data.domain.Student;
import com.harmony.umbrella.data.result.CellValue;
import com.harmony.umbrella.data.result.ListResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        studentQueryBuilder = new JpaQueryBuilder<>(Student.class, entityManager);
        classRoomQueryBuilder = new JpaQueryBuilder<>(ClassRoom.class, entityManager);
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
        Student result = studentQueryBuilder
                .equal("name", "Mary")
                .getSingleResult();
        Assert.assertEquals("Mary", result.getName());
    }

    @Test
    public void testSizeOf() {
        ListResult results = classRoomQueryBuilder
                .sizeOf("students", 3)
                .groupBy("id")
                .execute()
                .getAllResult(Selections.of("id"));
        Assert.assertEquals(2, results.size());
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
        LocalDate left = LocalDate.of(1992, 1, 1);
        LocalDate right = LocalDate.of(1992, 12, 31);
        List<Student> students = studentQueryBuilder
                .notBetween("birthday", left, right)
                .getAllResult();
        System.out.println(students);
    }

    @Test
    public void testCountColumn() {
        long count = classRoomQueryBuilder
                .equal("room", "一班")
                .execute()
                .count(Selections.ofCount("students.id", true));
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

    @Test
    public void testDisposableColumn() {
        List<Student> students = studentQueryBuilder.begin("name").equal("foo").getAllResult();
        System.out.println(students);
    }

    @Test
    public void testSelection() {
        List<Map<String, Object>> result = studentQueryBuilder
                .execute()
                .getAllResult(Selections.of("name", "gender", "birthday"))
                .stream()
                .map((sr) -> {
                    Map<String, Object> map = new HashMap<>();
                    for (CellValue cr : sr) {
                        map.put(cr.getName(), cr.getValue());
                    }
                    return map;
                }).collect(Collectors.toList());
        System.out.println(result);
    }

    @Test
    public void testSelectManyPart() {
        List<Map<String, Object>> result = classRoomQueryBuilder
                .execute()
                .getAllResult(Selections.of("grade", "room", "students.name"))
                .stream()
                .map((sr) -> {
                    Map<String, Object> map = new HashMap<>();
                    for (CellValue cr : sr) {
                        map.put(cr.getName(), cr.getValue());
                    }
                    return map;
                }).collect(Collectors.toList());
        System.out.println(result);
    }

    @Test
    public void testResultConverter() {
        List<Student> result = studentQueryBuilder
                .execute()
                .getAllResult(Selections.of("gender", "classRoom.room", "name"))
                .toList(Student.class);
        System.out.println(result);
    }

    private static ClassRoom newClassRoom(String grade, String room) {
        return new ClassRoom(grade, room);
    }

    private static Student newStudent(String name, String gender, LocalDate birthday, ClassRoom classRoom) {
        return new Student(name, gender, birthday, classRoom);
    }

}
