package com.pab.kata;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PostServiceJpaTest {

    @Autowired
    PostRepository repository;

    @Test
    void canWriteAndReadPost() {
        PostService service = new PostService(repository);
        PostRequestDTO newPost = new PostRequestDTO(50L,"the title", "the post");
        Post created = service.create(newPost);
        assertThat(created.getId()).isEqualTo(1L);
    }
}
