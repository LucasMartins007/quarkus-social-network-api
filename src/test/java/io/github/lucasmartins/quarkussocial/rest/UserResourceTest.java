package io.github.lucasmartins.quarkussocial.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.github.lucasmartins.quarkussocial.rest.dto.error.ResponseError;
import io.github.lucasmartins.quarkussocial.rest.dto.request.CreateUserRequest;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiUrl;

    @Test
    @Order(1)
    @DisplayName("Should create an user successfully")
    void createUserTest() {
        final CreateUserRequest user = new CreateUserRequest();
        user.setName("Test user");
        user.setAge(25);

        var response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiUrl)
                .then()
                .extract()
                .response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath());
    }

    @Test
    @DisplayName("Should return error when JSON is not valid")
    void createUserValidationErrorTest() {
        final CreateUserRequest user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        var response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiUrl)
                .then()
                .extract()
                .response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        final List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
    }

    @Test
    @DisplayName("Should return error message when age is not valid.")
    void createUserValidationErrorWhenAgeIsNotValidTest() {
        final CreateUserRequest user = new CreateUserRequest();
        user.setAge(null);
        user.setName("test name");

        var response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiUrl)
                .then()
                .extract()
                .response();

        final List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertEquals("Age is required", errors.get(0).get("message"));
        assertEquals(1, errors.size());
    }

    @Test
    @DisplayName("Should return error message when name is not valid.")
    void createUserValidationErrorWhenNameIsNotValidTest() {
        final CreateUserRequest user = new CreateUserRequest();
        user.setAge(25);
        user.setName(null);

        var response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiUrl)
                .then()
                .extract()
                .response();

        final List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertEquals("Name is required", errors.get(0).get("message"));
        assertEquals(1, errors.size());
    }

    @Test
    @DisplayName("Should list all users")
    void listAllUsersTest() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(apiUrl)
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }

}
