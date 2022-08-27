import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StudentDashboard {
    @JsonProperty
    public Integer id;

    @JsonProperty("first_name")
    public String firstName;

    @JsonProperty("last_name")
    public String lastName;

    @JsonProperty
    public List<Group> groups;

    @JsonProperty
    public List<StudentDashboardAssignment> assignments;
}