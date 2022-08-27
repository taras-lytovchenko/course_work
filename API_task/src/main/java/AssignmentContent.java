import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AssignmentContent {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Integer id;

    @JsonProperty
    public String content;

    public AssignmentContent() {}

    public AssignmentContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "AssignmentContentData{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}