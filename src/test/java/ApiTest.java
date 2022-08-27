import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;

public class ApiTest extends BaseTest {

    @Test
    public void createAndGetStudent() {
        Response studentCreateResponse = RestAssured.given()
                .body(new StudentData("Taras", "Karpenko"))
                .post("/students");
        studentCreateResponse.then().statusCode(200);
        Student student = studentCreateResponse.as(Student.class);

        Response studentGetResponse = RestAssured.get("/students/{id}", student.id);
        Student receivedStudent = studentGetResponse.as(Student.class);
        Assert.assertEquals(receivedStudent.firstName, "Taras");
        Assert.assertEquals(receivedStudent.lastName, "Karpenko");

        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
        Assert.assertEquals(allStudents.length, 1);
        System.out.println(Arrays.asList(allStudents));
    }

    @Test
    public void testStudentSearch() {
        RestAssured.given()
                .body(new StudentData("Samantha", "Berrous"))
                .post("/students")
                .then().statusCode(200);
        RestAssured.given()
                .body(new StudentData("Peter", "Berrous"))
                .post("/students")
                .then().statusCode(200);
        RestAssured.given()
                .body(new StudentData("Victor", "Olafson"))
                .post("/students")
                .then().statusCode(200);
        Response response = RestAssured.given()
                .queryParam("last_name", "erro")
                .when().get("/students");
        response.then().statusCode(200);
        Student[] searchResult = response.as(Student[].class);
        Assert.assertEquals(searchResult.length, 2);
        Set<String> names = Arrays.stream(searchResult).map(s -> s.firstName).collect(Collectors.toSet());
        Assert.assertEquals(names, Set.of("Samantha", "Peter"));
    }

    @Test
    public void testStudentDashboard() {
        RestAssured.given()
                .body(new StudentData("Samantha", "Smith"))
                .post("/students")
                .then().statusCode(200);
        RestAssured.given()
                .body(new StudentData("Peter", "Smith"))
                .post("/students")
                .then().statusCode(200);
        RestAssured.given()
                .body(new StudentData("Victor", "Olafson"))
                .post("/students")
                .then().statusCode(200);

        List<Student> allStudents = Arrays.asList(
                RestAssured.get("/students")
                        .then().statusCode(200)
                        .extract().as(Student[].class)
        );
        List<Integer> studentIds = allStudents.stream().map(student -> student.id).collect(Collectors.toList());

        System.out.println(studentIds.size());
        Group newGroup = RestAssured
                .given().body(new GroupData("Math", studentIds))
                .post("/groups")
                .then().statusCode(200)
                .body("name", equalTo("Math"))
                .body("students", hasSize(studentIds.size()))
                .extract().as(Group.class);
    }
}