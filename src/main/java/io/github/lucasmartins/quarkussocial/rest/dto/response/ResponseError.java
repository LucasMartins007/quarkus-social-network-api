package io.github.lucasmartins.quarkussocial.rest.dto.response;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;

import io.github.lucasmartins.quarkussocial.rest.dto.FieldError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseError {

    private String message;

    private Collection<FieldError> errors;

    public static Integer UNPROCESSABLE_ENTITY_STATUS = 422;

    /**
     * @param <T>
     * @param violations
     * @return
     */
    public static <T> ResponseError createFromValidation(Set<ConstraintViolation<T>> violations) {
        final List<FieldError> errors = violations.stream()
                .map(violation -> new FieldError(violation.getPropertyPath().toString(), violation.getMessage()))
                .collect(Collectors.toList());

        final String message = "Validation Error";

        var responseError = new ResponseError(message, errors);
        return responseError;
    }

    public Response withStatusCode(Integer statusCode) {
        return Response.status(statusCode).entity(this).build();
    }

}
