import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentDashboardAssignment {
    @JsonProperty
    public Integer id;

    @JsonProperty("student_id")
    public Integer studentId;

    @JsonProperty("content_id")
    public Integer contentId;

    @JsonProperty
    public String solution;

    @JsonProperty
    public Integer mark;
}