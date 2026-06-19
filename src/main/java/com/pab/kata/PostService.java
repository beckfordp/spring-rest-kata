package com.pab.kata;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }
    public List<Post> getAll() throws PostNotFoundException {
       return  repository.findAll();
    }

    public Post getById(long id) {
        return repository.findById(id).orElseThrow(() -> new PostNotFoundException("Post not found"));
    }

    public Post create(PostRequestDTO newPost) {
       return repository.save(new Post(
               newPost.userId(),
               null,
               newPost.title(),
               newPost.post()
       ));
    }
}
