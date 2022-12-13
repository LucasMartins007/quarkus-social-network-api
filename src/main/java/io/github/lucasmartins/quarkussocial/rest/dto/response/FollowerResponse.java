package io.github.lucasmartins.quarkussocial.rest.dto.response;

import io.github.lucasmartins.quarkussocial.domain.model.Follower;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowerResponse {

    private Long id;

    private String name;

    public FollowerResponse() {
    }

    public FollowerResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public FollowerResponse(Follower follower) {
        this(follower.getId(), follower.getFollower().getName());
    }

}
