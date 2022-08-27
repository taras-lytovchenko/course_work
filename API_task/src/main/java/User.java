import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty
    public String username;

    @JsonProperty
    public String password;

    public User(String name, String password) {
        this.username = name;
        this.password = password;
    }
}