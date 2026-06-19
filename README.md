# Testing Spring REST APIs with RestTestClient

Learn how to test your Spring REST APIs using the new `RestTestClient` introduced in Spring Framework 7. 
This guide walks you through five different testing approaches, from simple unit tests to full end-to-end tests.

## What is RestTestClient?

`RestTestClient` is a new unified testing tool in Spring Framework 7 that makes it easy to test REST APIs. 
Think of it as combining the best features of `MockMvc` and `WebTestClient` into one simple API.

**Why use RestTestClient?**
- One consistent API for all types of tests
- Fluent, readable syntax
- Works for unit tests, integration tests, and end-to-end tests
- Easy to learn and use

## Which Testing Approach Should I Use?

RestTestClient offers 5 different ways to test your API. Here's a simple guide:

| Method | Speed | What It Tests | Best For |
|--------|-------|---------------|----------|
| `bindToController` | ⚡ Fastest | Just your controller | Quick unit tests |
| `bindToMockMvc` | 🚀 Fast | Controller + Spring MVC | Testing validation, security |
| `bindToApplicationContext` | 🐢 Slower | Full app (no HTTP) | Real database tests |
| `bindToServer` | 🐌 Slowest | Everything with HTTP | Complete end-to-end tests |
| `bindToRouterFunction` | ⚡ Fastest | Functional endpoints | WebFlux functional routes |

**Not sure? Start with `bindToController` for simple tests and `bindToApplicationContext` for integration tests.**

## 1. Unit Tests with `bindToController`

**What it does:** Tests just your controller logic, nothing else. No Spring, no database, no validation.

**When to use:** When you want super-fast tests for your controller logic.

```java
public class TodoControllerUnitTest {
    RestTestClient client;
    TodoService todoService;

    @BeforeEach
    void setup() {
        // Mock your dependencies
        todoService = Mockito.mock(TodoService.class);
        when(todoService.getAllTodos()).thenReturn(
            List.of(new Todo(1L, 1L, "Test Todo", false))
        );

        // Bind to your controller
        client = RestTestClient.bindToController(new TodoController(todoService)).build();
    }

    @Test
    void shouldGetAllTodos() {
        client.get().uri("/api/todos")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].title").isEqualTo("Test Todo");
    }
}
```

**✅ Pros:** Super fast, simple setup
**❌ Cons:** No validation, security, or other Spring features

---

## 2. MVC Tests with `bindToMockMvc`

**What it does:** Tests your controller with Spring MVC features like validation, security, and exception handling.

**When to use:** When you need to test validation rules, security, or `@ControllerAdvice` error handling.

```java
@WebMvcTest(TodoController.class)
public class TodoControllerMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    void shouldValidateInput() {
        RestTestClient client = RestTestClient.bindTo(mockMvc).build();

        // Test validation - empty title should fail
        client.post().uri("/api/todos/")
                .body(new Todo(null, null, "", false))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser
    void shouldAllowAuthenticatedUser() {
        when(todoService.findAll()).thenReturn(
            List.of(new Todo(1L, 1L, "Secured Todo", false))
        );

        RestTestClient client = RestTestClient.bindTo(mockMvc);

        client.get().uri("/api/todos")
            .exchange()
            .expectStatus().isOk();
    }
}
```

**✅ Pros:** Tests validation, security, and error handling
**❌ Cons:** Services are still mocked, no real database

---

## 3. Integration Tests with `bindToApplicationContext`

**What it does:** Tests your full application with real services and database - but without actual HTTP.

**When to use:** When you want to test the complete flow including database operations.

```java
@SpringBootTest
@Transactional  // Rolls back after each test
public class TodoControllerIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void shouldCreateAndRetrieveTodo() {
        client = RestTestClient.bindToApplicationContext(context).build();

        // Create a todo - this hits the real database
        client.post().uri("/api/todos/")
                .body(new Todo(null, 1L, "Integration Test", false))
                .exchange()
                .expectStatus().isCreated();

        // Verify it's in the database
        assertEquals(1, todoService.findAll().size());

        // Retrieve it
        client.get().uri("/api/todos/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].title").isEqualTo("Integration Test");
    }
}
```

**✅ Pros:** Real database, real services, tests actual behavior
**❌ Cons:** Slower, can't test HTTP-specific features (like CORS)

---

## 4. End-to-End Tests with `bindToServer`

**What it does:** Tests everything including real HTTP requests - the most complete testing possible.

**When to use:** When you need to test HTTP-specific features like CORS, headers, or compression.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TodoControllerServerTest {

    @LocalServerPort
    private int port;

    private RestTestClient client;

    @BeforeEach
    public void setup() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void findAllTodos() {
        List<Todo> todos = client.get()
                .uri("/api/todos/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<Todo>>() {})
                .returnResult()
                .getResponseBody();

        assertEquals(200, todos.size());
        assertEquals("delectus aut autem", todos.get(0).title());
        assertFalse(todos.get(0).completed());
    }


}
```

**✅ Pros:** Most realistic, tests everything including HTTP
**❌ Cons:** Slowest, can be flaky due to network issues

---

## 5. Functional Tests with `bindToRouterFunction`

**What it does:** Tests functional/reactive endpoints (WebFlux style).

**When to use:** Only if you're using `RouterFunction` instead of `@RestController`.

```java
public class TodoRouterTest {

    @Test
    void shouldTestFunctionalEndpoint() {
        // Define your routes
        RouterFunction<ServerResponse> routes = RouterFunctions.route()
            .GET("/api/todos", todoHandler::getAllTodos)
            .build();

        RestTestClient client = RestTestClient.bindToRouterFunction(routes);

        client.get().uri("/api/todos")
            .exchange()
            .expectStatus().isOk();
    }
}
```

**✅ Pros:** Fast, good for reactive apps
**❌ Cons:** Only works with RouterFunction (not @RestController)

---

## Getting Started

### Required Dependencies

Add these to your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

For Spring Boot 4, you'll also need:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-restclient</artifactId>
</dependency>
```

### Running the Tests

```bash
# Run all tests
./mvnw test

# Run a specific test
./mvnw test -Dtest=TodoControllerMockTest
```

---

## Quick Tips

### Test Pyramid - How Many of Each Test?
- **Many** unit tests (`bindToController`) - fast feedback
- **Some** integration tests (`bindToApplicationContext`) - test real behavior
- **Few** end-to-end tests (`bindToServer`) - catch edge cases

### Common Mistakes

**1. Using @MockBean without Spring**
```java
// ❌ Won't work
public class Test {
    @MockBean TodoService service;
}

// ✅ Works
@WebMvcTest
public class Test {
    @MockBean TodoService service;
}
```

**2. Testing HTTP features without a server**
```java
// ❌ CORS won't work here
bindToMockMvc()

// ✅ Use this for CORS/headers
bindToServer()
```

---

## Migrating from Old Testing Tools

### From MockMvc
```java
// Old way
mockMvc.perform(get("/api/todos"))
    .andExpect(status().isOk());

// New way
RestTestClient.bindToMockMvc(mockMvc)
    .get().uri("/api/todos")
    .exchange()
    .expectStatus().isOk();
```

### From TestRestTemplate
```java
// Old way
testRestTemplate.getForEntity("/api/todos", Todo[].class);

// New way
RestTestClient.bindToServer()
    .get().uri("/api/todos")
    .exchange()
    .expectStatus().isOk();
```

---

## Additional Resources

- [Spring Framework RestTestClient Docs](https://docs.spring.io/spring-framework/reference/7.0/testing/resttestclient.html)
- [Spring Boot Testing Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)

---

**Created by Dan Vega**