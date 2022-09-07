import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.stream.Collectors;

public class ApiTests extends BaseTest {

    @Test
    public void createAndGetStudents() {
        // Student_1
        Student createdFirstStudent = createStudent("Mike",  "Milann");
        Response getFirstStudent = RestAssured.get("/students/{id}", createdFirstStudent.id);
        Student receivedFirstStudent = getFirstStudent.as(Student.class);
        Assert.assertEquals(receivedFirstStudent.firstName, createdFirstStudent.firstName);
        Assert.assertEquals(receivedFirstStudent.lastName, createdFirstStudent.lastName);
        // Student_2
        Student createdSecondStudent = createStudent("Ella", "Maven");
        Response getSecondStudent = RestAssured.get("/students/{id}", createdSecondStudent.id);
        Student receivedSecondStudent = getSecondStudent.as(Student.class);
        Assert.assertEquals(receivedSecondStudent.firstName, createdSecondStudent.firstName);
        Assert.assertEquals(receivedSecondStudent.lastName, createdSecondStudent.lastName);
        // Student_3
        Student createdThirdStudent = createStudent("Sara", "Ganen");
        Response getThirdStudent = RestAssured.get("/students/{id}", createdThirdStudent.id);
        Student receivedThirdStudent = getThirdStudent.as(Student.class);
        Assert.assertEquals(receivedThirdStudent.firstName, createdThirdStudent.firstName);
        Assert.assertEquals(receivedThirdStudent.lastName, createdThirdStudent.lastName);
        // Show all information about students
        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
        Assert.assertEquals(allStudents.length, 3);
//            System.out.println(Arrays.asList(allStudents));
    }

    @Test
    public void testCreateAndUpdateStudent() {
        Student createdFourthStudent = createStudent("Savid", "Kromkan");
        Response getFourthStudent = RestAssured.get("/students/{id}", createdFourthStudent.id);
        Student receivedFourthStudent = getFourthStudent.as(Student.class);
        Assert.assertEquals(receivedFourthStudent.firstName, createdFourthStudent.firstName);
        Assert.assertEquals(receivedFourthStudent.lastName, createdFourthStudent.lastName);

        RestAssured.given()
                .body(new StudentData("ChangedName", "ChangedLastName"))
                .put("/students/{id}", receivedFourthStudent.id)
                .then().statusCode(200);

        Student studentGetResponse = RestAssured.get("/students/{id}", receivedFourthStudent.id).as(Student.class);
        Assert.assertEquals(studentGetResponse.firstName, "ChangedName");
        Assert.assertEquals(studentGetResponse.lastName, "ChangedLastName");
//        System.out.println(Arrays.asList(studentGetResponse));
    }

    @Test
    public void testSearchStudentByFirstName() {
        Student createdFifthStudent = createStudent("Sarancha", "Geity");
        Response response = RestAssured.given()
                .queryParam("name", createdFifthStudent.firstName)
                .when().get("/students");
        response.then().statusCode(200);
        Student[] searchResult = response.as(Student[].class);
        Assert.assertEquals(searchResult.length, 1);
        Set<String> names = Arrays.stream(searchResult).map(s -> s.firstName).collect(Collectors.toSet());
        Assert.assertEquals(names, Set.of(createdFifthStudent.firstName));
    }

    @Test
    public void testSearchStudentByLastName() {
        Student createdSixthStudent = createStudent("Eddi", "Gilbert");
        Response response = RestAssured.given()
                .queryParam("last_name", createdSixthStudent.lastName)
                .when().get("/students");
        response.then().statusCode(200);
        Student[] searchResult = response.as(Student[].class);
        Assert.assertEquals(searchResult.length, 1);
        Set<String> lastnames = Arrays.stream(searchResult).map(s -> s.lastName).collect(Collectors.toSet());
        Assert.assertEquals(lastnames, Set.of(createdSixthStudent.lastName));
    }

    @Test
    public void zlasttestGetStudentsList() {

        Student[] allStudents = RestAssured.get("/students").as(Student[].class);
        Assert.assertEquals(allStudents.length, 8);
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
    public void testAssignmentForStudents() {
        Integer studentId;
        Integer contentId;

        //Назначение заданий и загрузка решения к нему
        Student[] getAllStudents = RestAssured.get("/students").as(Student[].class);
        Assert.assertEquals(getAllStudents.length, 3);
        RestAssured.given()
                .body(new AssignmentContent("CreateApiTest"))
                .post("/content")
                .then().statusCode(200);
        AssignmentContent[] allAssignmentContent = RestAssured.get("/content").as(AssignmentContent[].class);
        for (int i = 0; i < getAllStudents.length; i++) {
            contentId = allAssignmentContent[0].id;
            studentId = getAllStudents[i].id;


            JSONObject jsonData = new JSONObject();
            jsonData.put("target_type", "student");
            jsonData.put("student_id", studentId);
            jsonData.put("content_id", contentId);

            StudentDashboardAssignment assignmentForStudent = RestAssured.given()
                    .body(jsonData.toString())
                    .post("/assignments")
                    .then().statusCode(200)
                    .extract().as(StudentDashboardAssignment.class);

//            System.out.println(assignmentForStudent.solution);

            Assert.assertEquals(assignmentForStudent.studentId, studentId);
            Assert.assertEquals(assignmentForStudent.contentId, contentId);

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
    public void testMarkStudents() {
        Integer student;
        Integer content;
        Student createdSeventhStudent = createStudent("Zina", "Sory");
        Response getSeventhStudent = RestAssured.get("/students/{id}", createdSeventhStudent.id);
        Student receivedSeventhStudent = getSeventhStudent.as(Student.class);
        Assert.assertEquals(receivedSeventhStudent.firstName, createdSeventhStudent.firstName);
        Assert.assertEquals(receivedSeventhStudent.lastName, createdSeventhStudent.lastName);
        // Student_2
        Student createdEightStudent = createStudent("Gor", "Ramen");
        Response getEightStudent = RestAssured.get("/students/{id}", createdEightStudent.id);
        Student receivedEightStudent = getEightStudent.as(Student.class);
        Assert.assertEquals(receivedEightStudent.firstName, createdEightStudent.firstName);
        Assert.assertEquals(receivedEightStudent.lastName, createdEightStudent.lastName);

        //Assigning tasks
        Student[] getAllStudents = RestAssured.get("/students").as(Student[].class);
        Assert.assertEquals(getAllStudents.length, 6);
        RestAssured.given()
                .body(new AssignmentContent("NewCreateApiTest"))
                .post("/content")
                .then().statusCode(200);
        AssignmentContent[] allAssignmentContent = RestAssured.get("/content").as(AssignmentContent[].class);

        for (int i = 0; i < getAllStudents.length; i++) {
            content = allAssignmentContent[0].id;
            student = getAllStudents[i].id;

            JSONObject jsonData = new JSONObject();
            jsonData.put("target_type", "student");
            jsonData.put("student_id", student);
            jsonData.put("content_id", content);

            StudentDashboardAssignment assignmentForStudent = RestAssured.given()
                    .body(jsonData.toString())
                    .post("/assignments")
                    .then().statusCode(200)
                    .extract().as(StudentDashboardAssignment.class);

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
                        jsonData.put("target_type", "student");
                        jsonData.put("student_id", student);
                        jsonData.put("content_id", content);

                        StudentDashboardAssignment assignmentForStudentRepeat = RestAssured.given()
                                .body(jsonData.toString())
                                .post("/assignments")
                                .then().statusCode(200)
                                .extract().as(StudentDashboardAssignment.class);

                        Assert.assertNull(assignmentForStudent.mark);
                    } else if (markForStudent.mark == null) {

                        int oldMark = assignmentForStudent.mark;

                        content = allAssignmentContent[0].id;
                        student = getAllStudents[i].id;

                        JSONObject joAssignmPostRepeat = new JSONObject();
                        jsonData.put("target_type", "student");
                        jsonData.put("student_id", student);
                        jsonData.put("content_id", content);

                        StudentDashboardAssignment assignmentForStudentRepeat = RestAssured.given()
                                .body(jsonData.toString())
                                .post("/assignments")
                                .then().statusCode(200)
                                .extract().as(StudentDashboardAssignment.class);

                        Assert.assertEquals(assignmentForStudent.mark, oldMark);
                    }
                }
            }
        }
    }

    private Student createStudent(String firstName, String lastName) {
        StudentData studentData =  Student.createStudent(firstName, lastName);

        Response studentCreateResponse = RestAssured.given()
                .body(studentData)
                .post("/students");
        studentCreateResponse.then().statusCode(200);

        return studentCreateResponse.as(Student.class);
    }


}
