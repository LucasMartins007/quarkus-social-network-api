package io.github.lucasmartins.quarkussocial.rest.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowersPerUserResponse {
    
    private Integer followersCount;

    private List<FollowerResponse> content;
    
}
