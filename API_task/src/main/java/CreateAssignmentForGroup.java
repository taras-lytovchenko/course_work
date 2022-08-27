import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateAssignmentForGroup {

    @JsonProperty("target_type")
    public final String target = "group";

    @JsonProperty("group_id")
    public String groupId;

    @JsonProperty("content_id")
    public String contentId;

    public CreateAssignmentForGroup() {}

    public CreateAssignmentForGroup(String groupId, String contentId) {
        this.groupId = groupId;
        this.contentId = contentId;
    }
}