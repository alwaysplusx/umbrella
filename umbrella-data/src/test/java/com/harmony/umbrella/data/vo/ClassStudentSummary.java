package com.harmony.umbrella.data.vo;

/**
 * @author wuxii
 */
public class ClassStudentSummary {

    private String grade;
    private String room;
    private Long count;

    public ClassStudentSummary() {

    }

    public ClassStudentSummary(String grade, String room, Long count) {
        this.grade = grade;
        this.room = room;
        this.count = count;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return count + " people in " + grade + room;
    }
}
