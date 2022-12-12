package io.github.lucasmartins.quarkussocial.rest.dto;

import java.time.LocalDateTime;

import io.github.lucasmartins.quarkussocial.domain.model.Post;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponse {

    private String text;

    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post) {
        final PostResponse postResponse = new PostResponse();
        postResponse.setText(post.getText());
        postResponse.setDateTime(post.getDateTime());

        return postResponse;
    }
    
}
