package io.github.lucasmartins.quarkussocial.domain.repository;

import javax.enterprise.context.ApplicationScoped;

import io.github.lucasmartins.quarkussocial.domain.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post>{ 
    
}
