package za.co.turbo.code_shield.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import za.co.turbo.code_shield.builder.TaskBuilder;
import za.co.turbo.code_shield.model.Task;
import za.co.turbo.code_shield.model.TaskStatus;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("integration")
public class TaskApiIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createTask_ValidTask_ReturnsCreatedTask() {
        // Given
        Task newTask = TaskBuilder.aTask()
                .withTitle("Integration Test Task")
                .withDescription("Testing task creation via API")
                .withStatus(TaskStatus.TODO)
                .build();

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(newTask)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(200)
            .body("title", equalTo("Integration Test Task"))
            .body("description", equalTo("Testing task creation via API"))
            .body("status", equalTo("TODO"))
            .body("id", notNullValue());
    }

    @Test
    void createTask_EmptyTitle_ReturnsBadRequest() {
        // Given
        Task invalidTask = TaskBuilder.aTask()
                .withTitle("")  // Empty title should fail validation
                .build();

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(invalidTask)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("errors.message", equalTo("Task title is required"));
    }

    @Test
    void createTask_NullTitle_ReturnsBadRequest() {
        // Given
        Task invalidTask = TaskBuilder.aTask()
                .withTitle(null)  // Null title should fail validation
                .build();

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(invalidTask)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("errors.message", equalTo("Task title is required"));
    }

    @Test
    void createTask_PastDueDate_ReturnsBadRequest() {
        // Given
        Task invalidTask = TaskBuilder.aTask()
                .withDueDate(LocalDateTime.now().minusDays(1))  // Past due date
                .build();

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(invalidTask)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("errors.message", equalTo("Due date cannot be in the past"));
    }

    @Test
    void createTask_NullStatus_ReturnsBadRequest() {
        // Given
        Task invalidTask = TaskBuilder.aTask()
                .withStatus(null)  // Null status should fail validation
                .build();

        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(invalidTask)
        .when()
            .post("/api/tasks")
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("errors.message", equalTo("Task status is required"));
    }

    @Test
    void getTask_ExistingTask_ReturnsTask() {
        // First create a task
        Task newTask = TaskBuilder.aTask()
                .withTitle("Task to Retrieve")
                .build();

        Integer taskId = given()
            .contentType(ContentType.JSON)
            .body(newTask)
        .when()
            .post("/api/tasks")
        .then()
            .extract()
            .path("id");

        // Then retrieve it
        given()
        .when()
            .get("/api/tasks/" + taskId)
        .then()
            .statusCode(200)
            .body("id", equalTo(taskId))
            .body("title", equalTo("Task to Retrieve"));
    }

    @Test
    void getTask_NonExistentTask_ReturnsNotFound() {
        given()
        .when()
            .get("/api/tasks/999999")
        .then()
            .statusCode(404)
            .body("status", equalTo(404))
            .body("message", equalTo("Task with id 999999 not found"));
    }

    @Test
    void updateTask_ValidData_ReturnsUpdatedTask() {
        // First create a task
        Task newTask = TaskBuilder.aTask()
                .withTitle("Original Title")
                .build();

        Integer taskId = given()
            .contentType(ContentType.JSON)
            .body(newTask)
        .when()
            .post("/api/tasks")
        .then()
            .extract()
            .path("id");

        // Then update it
        Task updatedTask = TaskBuilder.aTask()
                .withTitle("Updated Title")
                .withStatus(TaskStatus.IN_PROGRESS)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(updatedTask)
        .when()
            .put("/api/tasks/" + taskId)
        .then()
            .statusCode(200)
            .body("id", equalTo(taskId))
            .body("title", equalTo("Updated Title"))
            .body("status", equalTo("IN_PROGRESS"));
    }

    @Test
    void updateTask_InvalidData_ReturnsBadRequest() {
        // First create a task
        Task newTask = TaskBuilder.aTask()
                .withTitle("Original Title")
                .build();

        Integer taskId = given()
            .contentType(ContentType.JSON)
            .body(newTask)
        .when()
            .post("/api/tasks")
        .then()
            .extract()
            .path("id");

        // Then try to update it with invalid data
        Task invalidTask = TaskBuilder.aTask()
                .withTitle("")  // Empty title
                .withStatus(TaskStatus.IN_PROGRESS)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(invalidTask)
        .when()
            .put("/api/tasks/" + taskId)
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("errors.message", equalTo("Task title is required"));
    }

    @Test
    void deleteTask_ExistingTask_ReturnsNoContent() {
        // First create a task
        Task newTask = TaskBuilder.aTask()
                .withTitle("Task to Delete")
                .build();

        Integer taskId = given()
            .contentType(ContentType.JSON)
            .body(newTask)
        .when()
            .post("/api/tasks")
        .then()
            .extract()
            .path("id");

        // Then delete it
        given()
        .when()
            .delete("/api/tasks/" + taskId)
        .then()
            .statusCode(204);

        // Verify it's gone
        given()
        .when()
            .get("/api/tasks/" + taskId)
        .then()
            .statusCode(404);
    }
} 