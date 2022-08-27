import com.fasterxml.jackson.annotation.JsonProperty;

public class AssignmentDataForStudent {
    @JsonProperty("target_type")
    public String targetType;

    @JsonProperty("student_id")
    public Integer studentId;

    @JsonProperty("content_id")
    public Integer contentId;

    public AssignmentDataForStudent(String targetType, Integer studentId, Integer contentId) {
        this.targetType = targetType;
        this.studentId = studentId;
        this.contentId = contentId;
    }

    @Override
    public String toString() {
        return "AssignmentForGroup{" +
                "target_type='" + targetType + '\'' +
                ", student_id=" + studentId +
                ", content_id=" + contentId +
                '}';
    }
}