import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ApiTests extends BaseTest {

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
    public void testCreateAndUpdateStudent() {
        Response stud4CreateResponse = RestAssured.given()
                .body(new StudentData("Savid", "Kromkan"))
                .post("/students");
        stud4CreateResponse.then().statusCode(200);
        Student stud4 = stud4CreateResponse.as(Student.class);
        Response stud4GetResponse = RestAssured.get("/students/{id}", stud4.id);
        Student receivedStud4 = stud4GetResponse.as(Student.class);
        Assert.assertEquals(receivedStud4.firstName, "Savid");
        Assert.assertEquals(receivedStud4.lastName, "Kromkan");

        RestAssured.given()
                .body(new StudentData("ChangedName", "ChangedLastName"))
                .put("/students/{id}", stud4.id)
                .then().statusCode(200);

        Student studentGetResponse = RestAssured.get("/students/{id}", stud4.id).as(Student.class);
        Assert.assertEquals(studentGetResponse.firstName, "ChangedName");
        Assert.assertEquals(studentGetResponse.lastName, "ChangedLastName");
//        System.out.println(Arrays.asList(studentGetResponse));
    }

    @Test
    public void testSearchStudentByFirstName() {
        Response response = RestAssured.given()
                .queryParam("name", "Sara")
                .when().get("/students");
        response.then().statusCode(200);
        Student[] searchResult = response.as(Student[].class);
        Assert.assertEquals(searchResult.length, 1);
        Set<String> names = Arrays.stream(searchResult).map(s -> s.firstName).collect(Collectors.toSet());
        Assert.assertEquals(names, Set.of("Sara"));
    }

    @Test
    public void testSearchStudentByLastName() {
        Response response = RestAssured.given()
                .queryParam("last_name", "Milann")
                .when().get("/students");
        response.then().statusCode(200);
        Student[] searchResult = response.as(Student[].class);
        Assert.assertEquals(searchResult.length, 1);
        Set<String> lastnames = Arrays.stream(searchResult).map(s -> s.lastName).collect(Collectors.toSet());
        Assert.assertEquals(lastnames, Set.of("Milann"));
    }

    @Test
    public void testGetStudentsList() {
        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
        Assert.assertEquals(allStudents.length, 4);
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
    public void zassignmentForStudents() {
        Integer student;
        Integer content;

        //Назначение заданий и загрузка решения к нему
        Student[] getAllStudents = RestAssured.get("/students").as(Student[].class);
        Assert.assertEquals(getAllStudents.length, 6);
        RestAssured.given()
                .body(new AssignmentContent("CreateApiTest"))
                .post("/content")
                .then().statusCode(200);
        AssignmentContent[] allAssignmentContent = RestAssured.get("/content").as(AssignmentContent[].class);
        for (int i = 0; i < getAllStudents.length; i++) {
            content = allAssignmentContent[0].id;
            student = getAllStudents[i].id;


            JSONObject joAssignmPost = new JSONObject();
            joAssignmPost.put("target_type", "student");
            joAssignmPost.put("student_id", student);
            joAssignmPost.put("content_id", content);

            StudentDashboardAssignment assignmentForStudent = RestAssured.given()
                    .body(joAssignmPost.toString())
                    .post("/assignments")
                    .then().statusCode(200)
                    .extract().as(StudentDashboardAssignment.class);

//            System.out.println(assignmentForStudent.solution);

            Assert.assertEquals(assignmentForStudent.studentId, student);
            Assert.assertEquals(assignmentForStudent.contentId, content);

            JSONObject solutionPost = new JSONObject();
            solutionPost.put("solution", "Test");

            StudentSolution solutionForStudent = RestAssured.given()
                    .body(solutionPost.toString())
                    .post("assignments/{id}/solution", assignmentForStudent.id.toString())
                    .then().statusCode(200)
                    .extract().as(StudentSolution.class);

            // Простановка оценки для задания
            JSONObject markPost = new JSONObject();
            markPost.put("mark", 5);

            StudentSolution markForStudent = RestAssured.given()
                    .body(markPost.toString())
                    .post("assignments/{id}/mark", assignmentForStudent.id.toString())
                    .then().statusCode(200)
                    .extract().as(StudentSolution.class);

        }
    }

    @Test
    public void zassi1gnmentForStudents() {
        Integer student;
        Integer content;
        Response stud1CreateResponse = RestAssured.given()
                .body(new StudentData("Zina", "Sory"))
                .post("/students");
        stud1CreateResponse.then().statusCode(200);
        Student stud1 = stud1CreateResponse.as(Student.class);
        Response stud1GetResponse = RestAssured.get("/students/{id}", stud1.id);
        Student receivedStud1 = stud1GetResponse.as(Student.class);
        Assert.assertEquals(receivedStud1.firstName, "Zina");
        // Student_2
        Response stud2CreateResponse = RestAssured.given()
                .body(new StudentData("Gor", "Ramen"))
                .post("/students");
        stud2CreateResponse.then().statusCode(200);
        Student stud2 = stud2CreateResponse.as(Student.class);
        Response st2GetResponse = RestAssured.get("/students/{id}", stud2.id);
        Student receivedStud2 = st2GetResponse.as(Student.class);
        Assert.assertEquals(receivedStud2.firstName, "Gor");
        Assert.assertEquals(receivedStud2.lastName, "Ramen");

        //Assigning tasks
        Student[] getAllStudents = RestAssured.get("/students").as(Student[].class);
        Assert.assertEquals(getAllStudents.length, 6);
        RestAssured.given()
                .body(new AssignmentContent("CreateApiTest"))
                .post("/content")
                .then().statusCode(200);
        AssignmentContent[] allAssignmentContent = RestAssured.get("/content").as(AssignmentContent[].class);

        for (int i = 0; i < getAllStudents.length; i++) {
            content = allAssignmentContent[0].id;
            student = getAllStudents[i].id;

            JSONObject joAssignmPost = new JSONObject();
            joAssignmPost.put("target_type", "student");
            joAssignmPost.put("student_id", student);
            joAssignmPost.put("content_id", content);

            StudentDashboardAssignment assignmentForStudent = RestAssured.given()
                    .body(joAssignmPost.toString())
                    .post("/assignments")
                    .then().statusCode(200)
                    .extract().as(StudentDashboardAssignment.class);

//            System.out.println(assignmentForStudent.solution);

            Assert.assertEquals(assignmentForStudent.studentId, student);
            Assert.assertEquals(assignmentForStudent.contentId, content);

            // Автоматическое оценивание заданий без оценок (проверить что задания, у которых есть решение, получат 5, а задания без решения получат 1)
            if (getAllStudents[i].firstName.equals("Gor")) {
                JSONObject solutionPost = new JSONObject();
                solutionPost.put("solution", "Test");

                StudentSolution solutionForStudent = RestAssured.given()
                        .body(solutionPost.toString())
                        .post("assignments/{id}/solution", assignmentForStudent.id.toString())
                        .then().statusCode(200)
                        .extract().as(StudentSolution.class);

                JSONObject markPost = new JSONObject();
                markPost.put("mark", 5);

                StudentSolution markForStudent = RestAssured.given()
                        .body(markPost.toString())
                        .post("assignments/{id}/mark", assignmentForStudent.id.toString())
                        .then().statusCode(200)
                        .extract().as(StudentSolution.class);

                for (int y = 0; y < getAllStudents.length; y++) {

                    // Проверить, что автоматическое оценивание не затрагивает уже выставленные оценки
                    if (markForStudent.solution == null && markForStudent.mark == null) {
                        content = allAssignmentContent[0].id;
                        student = getAllStudents[i].id;

                        JSONObject joAssignmPostRepeat = new JSONObject();
                        joAssignmPost.put("target_type", "student");
                        joAssignmPost.put("student_id", student);
                        joAssignmPost.put("content_id", content);

                        StudentDashboardAssignment assignmentForStudentRepeat = RestAssured.given()
                                .body(joAssignmPost.toString())
                                .post("/assignments")
                                .then().statusCode(200)
                                .extract().as(StudentDashboardAssignment.class);

                        Assert.assertNull(assignmentForStudent.mark);
                    }
                    else if (markForStudent.mark == null) {

                        int oldMark = assignmentForStudent.mark;

                        content = allAssignmentContent[0].id;
                        student = getAllStudents[i].id;

                        JSONObject joAssignmPostRepeat = new JSONObject();
                        joAssignmPost.put("target_type", "student");
                        joAssignmPost.put("student_id", student);
                        joAssignmPost.put("content_id", content);

                        StudentDashboardAssignment assignmentForStudentRepeat = RestAssured.given()
                                .body(joAssignmPost.toString())
                                .post("/assignments")
                                .then().statusCode(200)
                                .extract().as(StudentDashboardAssignment.class);

                        Assert.assertEquals(assignmentForStudent.mark, oldMark);
                    }
                }
            }
        }
    }
}
