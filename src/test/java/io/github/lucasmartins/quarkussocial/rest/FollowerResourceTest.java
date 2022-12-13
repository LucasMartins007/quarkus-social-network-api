package io.github.lucasmartins.quarkussocial.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.lucasmartins.quarkussocial.domain.model.Follower;
import io.github.lucasmartins.quarkussocial.domain.model.User;
import io.github.lucasmartins.quarkussocial.domain.repository.FollowerRepository;
import io.github.lucasmartins.quarkussocial.domain.repository.UserRepository;
import io.github.lucasmartins.quarkussocial.rest.dto.request.FollowerRequest;
import io.github.lucasmartins.quarkussocial.rest.dto.response.FollowerResponse;
import io.github.lucasmartins.quarkussocial.rest.resource.FollowerResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private FollowerRepository followerRepository;

    private User user;

    private User follower;

    private Follower followerEntity;

    @BeforeEach
    @Transactional
    void setUp() {
        user = createUser("Some user");
        follower = createUser("Another user");

        followerEntity = createFollower(user, follower);
    }

    private Follower createFollower(User user, User follower) {
        final Follower followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);

        followerRepository.persist(followerEntity);
        return followerEntity;
    }

    private User createUser(String name) {
        final User user = new User();
        user.setAge(25);
        user.setName(name);

        userRepository.persist(user);
        return user;
    }

    @Test
    @DisplayName("Shoul return 409 when followerId is equal to UserId")
    void saveUserAsFollowerTest() {
        final FollowerRequest followerRequest = new FollowerRequest();
        followerRequest.setFollowerId(user.getId());

        given()
                .contentType(ContentType.JSON)
                .body(followerRequest)
                .pathParam("userId", user.getId())
                .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself."));
    }

    @Test
    @DisplayName("Should return 404 on follow an user when userId doesn't exist")
    void userNotFoundWhenTryingToFollowTest() {
        final FollowerRequest followerRequest = new FollowerRequest();
        followerRequest.setFollowerId(user.getId());

        final Long inexistentUserId = 99L;

        given()
                .contentType(ContentType.JSON)
                .body(followerRequest)
                .pathParam("userId", inexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should follow an user")
    void followUserTest() {
        final FollowerRequest followerRequest = new FollowerRequest();
        followerRequest.setFollowerId(follower.getId());

        given()
                .contentType(ContentType.JSON)
                .body(followerRequest)
                .pathParam("userId", user.getId())
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 on list user followers and userId doesn't exist")
    void userNotFoundWhenTryingToListFollowersTest() {
        final Long inexistentUserId = 99L;
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should list an user's followers")
    void ListFollowersTest() {
        var response = given()
                .contentType(ContentType.JSON)
                .pathParam("userId", followerEntity.getUser().getId())
                .when()
                .get()
                .then()
                .extract()
                .response();
        final Integer followersCount = response.jsonPath().get("followersCount");
        final List<FollowerResponse> content = response.jsonPath().get("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(1, followersCount);
        assertEquals(1, content.size());
    }

    @Test
    @DisplayName("Should return 404 on unfollow user and userId doesn't exist")
    void userNotFoundWhenUnfollowAnUserTest() {
        final Long inexistentUserId = 99L;
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", follower.getId())
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("Should unfollow an user ")
    void unfollowUserTest() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", user.getId())
                .queryParam("followerId", follower.getId())
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}
