package io.github.lucasmartins.quarkussocial.domain.repository;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import io.github.lucasmartins.quarkussocial.domain.model.Follower;
import io.github.lucasmartins.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public List<Follower> findByUser(Long userId) {
        PanacheQuery<Follower> query = find("user.id", userId);

        return query.list();
    }

    public boolean follows(User follower, User user) {
        final Map<String, Object> params = Parameters.with("follower", follower)
                .and("user", user)
                .map();

        final PanacheQuery<Follower> query = find("follower = :follower and user = :user", params);

        return query
                .firstResultOptional()
                .isPresent();
    }

    public void deleteByFollowerAndUser(Long followerId, User user) {
        final Map<String, Object> params = Parameters.with("user", user)
                .and("followerId", followerId)
                .map();

        delete("follower.id = :followerId and user = :user", params);
    }

}
