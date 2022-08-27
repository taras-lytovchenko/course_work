import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HeaderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class BaseTest {
    private final String userNamePrefix = "testapiuser";
    private final String password = "testapipass";
    private String userName;
    private String sessionToken;



    @BeforeTest
    public void setUp() {
        RestAssured.baseURI = "http://www.robotdreams.karpenko.cc/";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
        userName = userNamePrefix + randomAlphanumeric(5);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", userName);
        jsonObject.put("password", password);

        RestAssured.given()
                .body(jsonObject.toString())
                .when()
                .post("/user").then().statusCode(200);

        Response sessionResponse = RestAssured.given()
                .queryParam("username", userName)
                .queryParam("password", password)
                .when()
                .get("/login");
        sessionResponse.then().statusCode(200);
        JSONObject jsonObject1Respone = new JSONObject(sessionResponse.asString());
        sessionToken = jsonObject1Respone.getString("session_token");
        System.out.println("Session token = " + sessionToken);

        RestAssured.config = RestAssured.config.headerConfig(HeaderConfig.headerConfig().overwriteHeadersWithName("user-token"));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("user-token", sessionToken)
                .build();
    }
}