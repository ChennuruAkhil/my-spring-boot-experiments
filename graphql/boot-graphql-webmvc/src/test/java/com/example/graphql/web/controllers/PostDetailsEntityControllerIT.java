package com.example.graphql.web.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.graphql.common.AbstractIntegrationTest;
import com.example.graphql.entities.PostDetailsEntity;
import com.example.graphql.entities.PostEntity;
import com.example.graphql.model.request.PostDetailsRequest;
import com.example.graphql.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class PostDetailsEntityControllerIT extends AbstractIntegrationTest {

    @Autowired private PostRepository postRepository;

    private PostEntity post;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();

        post = PostEntity.builder().content("First Post").title("First Title").build();

        PostDetailsEntity postDetailsEntity =
                PostDetailsEntity.builder().detailsKey("Junit1").build();
        post.setDetails(postDetailsEntity);

        post = postRepository.save(post);
    }

    @Test
    void shouldFetchAllPostDetails() throws Exception {
        this.mockMvc
                .perform(get("/api/postdetails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)));
    }

    @Test
    void shouldFindPostDetailsById() throws Exception {
        PostDetailsEntity postDetails = post.getDetails();

        this.mockMvc
                .perform(get("/api/postdetails/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.createdBy", is(postDetails.getCreatedBy())));
    }

    @Test
    void shouldUpdatePostDetails() throws Exception {

        PostDetailsRequest postDetailsRequest = new PostDetailsRequest("Updated PostDetails");

        Long postDetailsId = post.getId();
        this.mockMvc
                .perform(
                        put("/api/postdetails/{id}", postDetailsId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(postDetailsRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detailsKey", is("Updated PostDetails")));
    }
}
