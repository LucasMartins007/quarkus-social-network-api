package io.github.lucasmartins.quarkussocial.rest;

import static io.restassured.RestAssured.given;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.lucasmartins.quarkussocial.domain.model.Follower;
import io.github.lucasmartins.quarkussocial.domain.model.Post;
import io.github.lucasmartins.quarkussocial.domain.model.User;
import io.github.lucasmartins.quarkussocial.domain.repository.FollowerRepository;
import io.github.lucasmartins.quarkussocial.domain.repository.PostRepository;
import io.github.lucasmartins.quarkussocial.domain.repository.UserRepository;
import io.github.lucasmartins.quarkussocial.rest.dto.request.CreatePostRequest;
import io.github.lucasmartins.quarkussocial.rest.resource.PostResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
public class PostResourceTest {

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private FollowerRepository followerRepository;

    @Inject
    private PostRepository postRepository;

    private User user;

    private User userNotFollower;

    private User userFollower;

    @BeforeEach
    @Transactional
    void setUp() {
        this.user = createUser("Some user");
        this.userNotFollower = createUser("User that doesn't follow anyone");
        this.userFollower = createUser("User that follows someone");

        createFollower();
        createPost();
    }

    void createPost() {
        final Post post = new Post();
        post.setText("Some text");
        post.setUser(user);

        postRepository.persist(post);
    }

    void createFollower() {
        final Follower follower = new Follower();
        follower.setFollower(userFollower);
        follower.setUser(user);

        followerRepository.persist(follower);
    }

    private User createUser(String name) {
        final User user = new User();
        user.setAge(25);
        user.setName(name);

        userRepository.persist(user);
        return user;
    }

    @Test
    @DisplayName("Should create a post for a user")
    void createPostTest() {
        final CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setText("Some text");
        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", user.getId())
                .when()
                .post()
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Should return an error 404 when trying to make a post for an inexistent user")
    void postForAnInexistentUserTest() {
        final CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        final Long inexistentUserId = 99L;
        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", inexistentUserId)
                .when()
                .post()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 404 when user doesn't exist")
    void listPostsUserNotFoundTest() {
        final Long inexistentUserId = 99L;
        given()
                .pathParam("userId", inexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when followerId header is not present")
    void listPostsFollowerIdHeaderNotSentTest() {
        given()
                .pathParam("userId", user.getId())
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId."));
    }

    @Test
    @DisplayName("Should return 400 when follower doesn't exist")
    void listPostsFollowerNotFound() {
        final Long inexistentFollowerId = 99L;

        given()
                .pathParam("userId", user.getId())
                .header("followerId", inexistentFollowerId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("FollowerId doesn't exist."));
    }

    @Test
    @DisplayName("Should return 400 when follower isn't a follower")
    void listPostsNotAFollower() {
        given()
                .pathParam("userId", user.getId())
                .header("followerId", userNotFollower.getId())
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You doesn't have permission to access this resource."));
    }

    @Test
    @DisplayName("Should return posts from user")
    void listPosts() {
        given()
        .pathParam("userId", user.getId())
        .header("followerId", userFollower.getId())
        .when()
        .get()
        .then()
        .statusCode(200)
        .body("size()", Matchers.is(1));
    }

}
