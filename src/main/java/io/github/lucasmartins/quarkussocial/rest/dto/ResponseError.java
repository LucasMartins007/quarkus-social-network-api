package io.github.lucasmartins.quarkussocial.rest.dto;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;

public class ResponseError {

    private String message;

    private Collection<FieldError> errors;

    public static Integer UNPROCESSABLE_ENTITY_STATUS = 422;

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

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

    public void setErrors(Collection<FieldError> errors) {
        this.errors = errors;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Collection<FieldError> getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }
}
