import com.fasterxml.jackson.annotation.JsonProperty;

public class AssignmentDataForStudent {
    @JsonProperty("target_type")
    public String targetType;

    @JsonProperty("student_id")
    public String studentId;

    @JsonProperty("content_id")
    public String contentId;

    public AssignmentDataForStudent(String targetType, String studentId, String contentId) {
        this.targetType = targetType;
        this.studentId = studentId;
        this.contentId = contentId;
    }
}