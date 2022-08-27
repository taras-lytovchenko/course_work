import com.fasterxml.jackson.annotation.JsonProperty;

public class UserSession {
    @JsonProperty("session_token")
    public String sessionToken;

    @JsonProperty
    public String username;

    @JsonProperty("valid_to")
    public String validTo;
}