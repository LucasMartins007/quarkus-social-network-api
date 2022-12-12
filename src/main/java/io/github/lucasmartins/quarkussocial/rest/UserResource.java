package io.github.lucasmartins.quarkussocial.rest;

import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import io.github.lucasmartins.quarkussocial.domain.model.User;
import io.github.lucasmartins.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@Path("/users")
public class UserResource {

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {
        final User user = new User();

        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        user.persist();

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers() {
        PanacheQuery<User> query = User.findAll();

        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        final User user = User.findById(id);
        
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
            .build();
        }
        
        user.delete();
        return Response.ok().build();
    }
    
    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest createUserRequest) {
        final User user = User.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }

        user.setName(createUserRequest.getName());
        user.setAge(createUserRequest.getAge());
        return Response.ok().build();
    }

}