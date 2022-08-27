import com.fasterxml.jackson.annotation.JsonProperty;

public class StudentDashboardAssignment extends AssignmentDataForStudent {
    @JsonProperty
    public Integer id;

    @JsonProperty
    public String solution;

    @JsonProperty
    public Integer mark;

    public StudentDashboardAssignment(String targetType, Integer studentId, Integer contentId) {
        super(targetType, studentId, contentId);
    }

    public void AssigmentDashboardForStudent(Integer id, String solution, Integer mark, String targetType, Integer studentId, Integer contentId) {
        this.id = id;
        this.solution = solution;
        this.mark = mark;
    }



    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", solution='" + solution + '\'' +
                ", mark=" + mark +
                ", student_id=" + studentId +
                ", content_id=" + contentId +
                '}';
    }
}