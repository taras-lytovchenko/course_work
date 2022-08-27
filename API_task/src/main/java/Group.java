import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Group{
    @JsonProperty
    Integer id;

    @JsonProperty
    String name;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty
    List<Student> students;
}