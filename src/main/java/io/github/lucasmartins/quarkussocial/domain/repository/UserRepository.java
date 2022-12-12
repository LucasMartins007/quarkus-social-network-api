package io.github.lucasmartins.quarkussocial.domain.repository;

import javax.enterprise.context.ApplicationScoped;

import io.github.lucasmartins.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
}
