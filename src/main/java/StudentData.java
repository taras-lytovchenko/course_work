import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StudentData {
    @JsonProperty("first_name")
    public String firstName;

    @JsonProperty("last_name")
    public String lastName;

    public StudentData(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public StudentData(){

    }

//
//    @JsonProperty("courses")
//    public List<Course> courses;
}