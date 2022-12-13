package io.github.lucasmartins.quarkussocial.rest.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FieldError {

    private String field;

    private String message;

    
}
