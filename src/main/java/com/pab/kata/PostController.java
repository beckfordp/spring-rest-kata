package com.pab.kata;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;

    PostController(PostService service) {
        this.service = service;
    }

    @GetMapping("")
    List<Post> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    Post getOne(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("")
    ResponseEntity<Post> post(@RequestBody PostRequestDTO newPost) {

        Post created =  service.create(newPost);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/posts/" + created.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(created);
    }
}
