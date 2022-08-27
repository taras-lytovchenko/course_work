import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Course {
    @JsonProperty
    public String title;

    @JsonProperty
    public String curator;
}