package io.github.lucasmartins.quarkussocial.rest.resource;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.lucasmartins.quarkussocial.domain.model.Follower;
import io.github.lucasmartins.quarkussocial.domain.model.User;
import io.github.lucasmartins.quarkussocial.domain.repository.FollowerRepository;
import io.github.lucasmartins.quarkussocial.domain.repository.UserRepository;
import io.github.lucasmartins.quarkussocial.rest.dto.request.FollowerRequest;
import io.github.lucasmartins.quarkussocial.rest.dto.response.FollowerResponse;
import io.github.lucasmartins.quarkussocial.rest.dto.response.FollowersPerUserResponse;
import lombok.RequiredArgsConstructor;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class FollowerResource {

    private final FollowerRepository followerRepository;

    private final UserRepository userRepository;

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest followerRequest) {
        if (userId.equals(followerRequest.getFollowerId())) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("You can't follow yourself.")
                    .build();
        }
        final User user = userRepository.findById(userId);
        if (user == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
        final User userFollower = userRepository.findById(followerRequest.getFollowerId());

        final boolean follows = followerRepository.follows(userFollower, user);
        if (!follows) {
            final Follower follower = new Follower();
            follower.setUser(user);
            follower.setFollower(userFollower);

            followerRepository.persist(follower);
        }

        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {
        final User user = userRepository.findById(userId);
        if (user == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        final List<Follower> followers = followerRepository.findByUser(userId);
        final FollowersPerUserResponse followersPerUserResponse = new FollowersPerUserResponse();
        followersPerUserResponse.setFollowersCount(followers.size());

        List<FollowerResponse> followerResponses = followers.stream()
                .map(FollowerResponse::new)
                .collect(Collectors.toList());

        followersPerUserResponse.setContent(followerResponses);
        return Response.ok(followersPerUserResponse).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        final User user = userRepository.findById(userId);
        if (user == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        followerRepository.deleteByFollowerAndUser(followerId, user);

        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }

}
