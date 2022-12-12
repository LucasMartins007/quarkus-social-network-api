package io.github.lucasmartins.quarkussocial.rest.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class CreateUserRequest {

    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Age is required")
    private Integer age;

}
