package com.pab.kata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class LoadDatabase implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);


    private final PostRepository repository;

    public LoadDatabase(PostRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        log.info("CommandLineRunner running...");
        log.info(" Preloading {}", repository.save(new Post(50L, null, "A title", "A Post")));
        log.info(" Preloading {}", repository.save(new Post(50L, null, "Another title", "Another Post")));
    }
}
