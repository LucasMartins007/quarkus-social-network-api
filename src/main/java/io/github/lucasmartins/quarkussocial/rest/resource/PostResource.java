package io.github.lucasmartins.quarkussocial.rest.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.lucasmartins.quarkussocial.domain.model.Post;
import io.github.lucasmartins.quarkussocial.domain.model.User;
import io.github.lucasmartins.quarkussocial.domain.repository.PostRepository;
import io.github.lucasmartins.quarkussocial.domain.repository.UserRepository;
import io.github.lucasmartins.quarkussocial.rest.dto.request.CreatePostRequest;
import io.github.lucasmartins.quarkussocial.rest.dto.response.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import lombok.RequiredArgsConstructor;

@Path("/users/{userId}/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class PostResource {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest createPostRequest) {
        final User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final Post post = new Post();
        post.setText(createPostRequest.getText());
        post.setUser(user);
        postRepository.persist(post);

        return Response
                .status(Response.Status.CREATED)
                .entity(PostResponse.fromEntity(post))
                .build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId) {
        final User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        PanacheQuery<Post> query = postRepository.find("user", Sort.by("dateTime", Direction.Descending), user);

        List<Post> posts = query.list();

        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response
                .status(Response.Status.OK)
                .entity(postResponses)
                .build();
    }

}
