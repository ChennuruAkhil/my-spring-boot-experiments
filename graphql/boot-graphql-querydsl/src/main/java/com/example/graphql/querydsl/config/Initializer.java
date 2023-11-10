package com.example.graphql.querydsl.config;

import com.example.graphql.querydsl.entities.Post;
import com.example.graphql.querydsl.entities.PostDetails;
import com.example.graphql.querydsl.entities.Tag;
import com.example.graphql.querydsl.repositories.PostRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Initializer implements CommandLineRunner {

    private final PostRepository postRepository;

    @Override
    public void run(String... args) {
        log.info("Running Initializer.....");
        Post post = new Post().setTitle("title").setContent("content");
        post.addDetails(new PostDetails()
                .setCreatedOn(LocalDateTime.of(2023, 12, 31, 10, 35, 45, 99))
                .setCreatedBy("appUser"));
        post.addTag(new Tag().setName("java"));
        post.addTag(new Tag().setName("spring"));
        postRepository.save(post);
    }
}
