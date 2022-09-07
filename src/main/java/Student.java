import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

public class Student extends StudentData{
    @JsonProperty
    public Integer id;

    public Student (Integer id, String firstName, String lastName) {
        super(firstName, lastName);
        this.id = id;
    }

    public Student(){}

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
    public static StudentData createStudent(String firstName, String lastName) {
        return new StudentData(firstName, lastName);
    }
}