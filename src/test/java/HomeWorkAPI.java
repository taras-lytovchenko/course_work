import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.*;
import java.util.stream.Collectors;

public class HomeWorkAPI extends BaseTest {
    @Test
    public void createAndGetStudents() {
    // Student_1
        Response stud1CreateResponse = RestAssured.given()
                .body(new StudentData("Mike", "Milann"))
                .post("/students");
        stud1CreateResponse.then().statusCode(200);
        Student stud1 = stud1CreateResponse.as(Student.class);
        Response stud1GetResponse = RestAssured.get("/students/{id}", stud1.id);
        Student receivedStud1 = stud1GetResponse.as(Student.class);
        Assert.assertEquals(receivedStud1.firstName, "Mike");
        Assert.assertEquals(receivedStud1.lastName, "Milann");
    // Student_2
        Response stud2CreateResponse = RestAssured.given()
                .body(new StudentData("Ella", "Maven"))
                .post("/students");
        stud2CreateResponse.then().statusCode(200);
        Student stud2 = stud2CreateResponse.as(Student.class);
        Response st2GetResponse = RestAssured.get("/students/{id}", stud2.id);
        Student receivedStud2 = st2GetResponse.as(Student.class);
        Assert.assertEquals(receivedStud2.firstName, "Ella");
        Assert.assertEquals(receivedStud2.lastName, "Maven");
    // Student_3
        Response stud3CreateResponse = RestAssured.given()
                .body(new StudentData("Sara", "Ganen"))
                .post("/students");
        stud3CreateResponse.then().statusCode(200);
        Student stud3 = stud3CreateResponse.as(Student.class);
        Response stud3GetResponse = RestAssured.get("/students/{id}", stud3.id);
        Student receivedStud3 = stud3GetResponse.as(Student.class);
        Assert.assertEquals(receivedStud3.firstName, "Sara");
        Assert.assertEquals(receivedStud3.lastName, "Ganen");
    // Show all information about students
        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
        Assert.assertEquals(allStudents.length, 3);
    //    System.out.println(Arrays.asList(allStudents));
    }
    @Test
    public void testSearchStudentByFirstName() {
        Response response = RestAssured.given()
                .queryParam("name", "Mike")
                .when().get("/students");
        response.then().statusCode(200);
        Student[] searchResult = response.as(Student[].class);
        Assert.assertEquals(searchResult.length, 1);
        Set<String> names = Arrays.stream(searchResult).map(s -> s.firstName).collect(Collectors.toSet());
        Assert.assertEquals(names, Set.of("Mike"));
    }

    @Test
    public void testGetStudentsList() {
        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
        Assert.assertEquals(allStudents.length, 3);
    }
    @Test
    public void createNewGroup() {
        List<Student> allStudents = Arrays.asList(
                RestAssured.get("/students")
                        .then().statusCode(200)
                        .extract().as(Student[].class)
        );
        List<Integer> studentsIds = allStudents.stream().map(student -> student.id).collect(Collectors.toList());
        Response groupResponse = RestAssured.given()
                .body(new GroupData("theBestGroup", studentsIds))
                .post("/groups");
        groupResponse.then().statusCode(200);
        Group group = groupResponse.as(Group.class);
        Assert.assertEquals(group.name, "theBestGroup");
        Group[] groups = RestAssured.get("/groups").as(Group[].class);
        Assert.assertEquals(groups.length, 1);
    //    System.out.println(group.name);
    //    System.out.println(group.students);
    }
    @Test
    public void createJobTemplate() {
        Response createJobTemplate = RestAssured.given()
                .body(new AssignmentContent("CreateApiTest"))
                .post("/content");
        createJobTemplate.then().statusCode(200);
        AssignmentContent content = createJobTemplate.as(AssignmentContent.class);
        Assert.assertEquals(content.content, "CreateApiTest");
    }
    @Test
    public void assignmentForStudents() {
        Integer student;
        Integer content;
        Student[] getAllStudents = RestAssured.get("/students").as(Student[].class);
        RestAssured.given()
                .body(new AssignmentContent("CreateApiTest"))
                .post("/content")
                .then().statusCode(200);
        AssignmentContent[] allAssignmentContent = RestAssured.get("/content").as(AssignmentContent[].class);
        int start_process = 0;
        int end_process = getAllStudents.length / 2;
        for (int i = 0; i < allAssignmentContent.length; i++) {
            if (i == 1) {start_process = getAllStudents.length / 2; end_process = getAllStudents.length;}
            for (int s = start_process; s < end_process; s++) {
                content = allAssignmentContent[i].id;
                student = getAllStudents[s].id;
                StudentDashboardAssignment assignmentForStudent = RestAssured.given()
                        .body(new AssignmentDataForStudent("student", student, content))
                        .post("/assignments")
                        .then().statusCode(200)
                        .extract().as(StudentDashboardAssignment.class);
                Assert.assertEquals(assignmentForStudent.studentId, student);
                Assert.assertEquals(assignmentForStudent.contentId, content);
            }
        }
    }
}
