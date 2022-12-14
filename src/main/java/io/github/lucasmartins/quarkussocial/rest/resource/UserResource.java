package io.github.lucasmartins.quarkussocial.rest.resource;

import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.github.lucasmartins.quarkussocial.domain.model.User;
import io.github.lucasmartins.quarkussocial.domain.repository.UserRepository;
import io.github.lucasmartins.quarkussocial.rest.dto.error.ResponseError;
import io.github.lucasmartins.quarkussocial.rest.dto.request.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import lombok.RequiredArgsConstructor;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserResource {

    private final UserRepository userRepository;

    private final Validator validator;

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if (!violations.isEmpty()) {
            return ResponseError
                    .createFromValidation(violations)
                    .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        final User user = new User();

        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        userRepository.persist(user);

        return Response.status(Status.CREATED)
                .entity(user)
                .build();
    }

    @GET
    public Response listAllUsers() {
        PanacheQuery<User> query = userRepository.findAll();

        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        final User user = userRepository.findById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }

        userRepository.delete(user);
        return Response.noContent().build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest createUserRequest) {
        final User user = userRepository.findById(id);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }

        user.setName(createUserRequest.getName());
        user.setAge(createUserRequest.getAge());
        return Response.noContent().build();
    }

}