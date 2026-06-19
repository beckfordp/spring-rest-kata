package com.pab.kata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@WebMvcTest
class PostControllerTest {
    List<Post> posts;
    RestTestClient client;

    @Autowired
    private MockMvc mockNvc;

    @Autowired
    private ObjectMapper mapper;

    private @MockitoBean PostService mockService;

    @BeforeEach
    void setup() {
        posts = List.of(new Post(1L, 1L, "Beginning", "This is the beginning"),
                new Post(2L, 2L, "ending", "This is the ending"));
        client = RestTestClient.bindTo(mockNvc).build();
    }


//TODO Test Get All (Retrieve) - missing HATEOS links
    @Test
    void  canGetAllPosts() {
        when(mockService.getAll()).thenReturn(posts);
        client.get().uri("/api/posts")
                .exchange()
                .expectStatus().isOk();
    }


//TODO Test Get one - missing HATEOS
@Test
void  canGetOnePost() {
    when(mockService.getById(1L)).thenReturn(new Post(1L, 1L, "first", "first"));
    client.get().uri("/api/posts/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.title").isEqualTo("first");

}

@Test
void getOneNotFound() {
    when(mockService.getById(1L)).thenThrow(PostNotFoundException.class);
    client.get().uri("/api/posts/1")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody().jsonPath("$.title").doesNotExist();
}


//TODO Test Post (create) - more complicated should return header with location url same as GET, status 201
@Test
void canPost() {
        PostRequestDTO newPost = new PostRequestDTO(50L,"first", "first");
        Post saved = new Post(50L, 1L, "first", "first");

        System.out.println("******************{}" +  newPost);
        System.out.println("******************{}" +  saved);
    when(mockService.create(newPost)).thenReturn(saved);
    Post created = client.post().uri("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .body(mapper.writeValueAsString(newPost))
            //.body(newPost // This works too after overloading toString on record, don't know why? Magic :)
            .exchange()
            .expectStatus().isCreated()
            .expectBody(new ParameterizedTypeReference<Post>(){})
            .returnResult()
            .getResponseBody();

    assertThat(created.getId()).isEqualTo(1L);
}

//TODO Test Put (Update)
//TODO Test Delete



}
