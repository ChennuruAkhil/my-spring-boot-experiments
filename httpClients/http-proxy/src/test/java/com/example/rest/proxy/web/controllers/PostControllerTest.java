package com.example.rest.proxy.web.controllers;

import static com.example.rest.proxy.utils.AppConstants.PROFILE_TEST;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.rest.proxy.entities.Post;
import com.example.rest.proxy.model.response.PagedResult;
import com.example.rest.proxy.services.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PostController.class)
@ActiveProfiles(PROFILE_TEST)
class PostControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PostService postService;

    @Autowired private ObjectMapper objectMapper;

    private List<Post> postList;

    @BeforeEach
    void setUp() {
        this.postList = new ArrayList<>();
        this.postList.add(new Post(1L, "text 1"));
        this.postList.add(new Post(2L, "text 2"));
        this.postList.add(new Post(3L, "text 3"));
    }

    @Test
    void shouldFetchAllPosts() throws Exception {
        Page<Post> page = new PageImpl<>(postList);
        PagedResult<Post> postPagedResult = new PagedResult<>(page);
        given(postService.findAllPosts(0, 10, "id", "asc")).willReturn(postPagedResult);

        this.mockMvc
                .perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()", is(postList.size())))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.isFirst", is(true)))
                .andExpect(jsonPath("$.isLast", is(true)))
                .andExpect(jsonPath("$.hasNext", is(false)))
                .andExpect(jsonPath("$.hasPrevious", is(false)));
    }

    @Test
    void shouldFindPostById() throws Exception {
        Long postId = 1L;
        Post post = new Post(postId, "text 1");
        given(postService.findPostById(postId)).willReturn(Optional.of(post));

        this.mockMvc
                .perform(get("/api/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(post.getText())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingPost() throws Exception {
        Long postId = 1L;
        given(postService.findPostById(postId)).willReturn(Optional.empty());

        this.mockMvc.perform(get("/api/posts/{id}", postId)).andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewPost() throws Exception {
        given(postService.savePost(any(Post.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        Post post = new Post(1L, "some text");
        this.mockMvc
                .perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.text", is(post.getText())));
    }

    @Test
    void shouldReturn400WhenCreateNewPostWithoutText() throws Exception {
        Post post = new Post(null, null);

        this.mockMvc
                .perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andExpect(jsonPath("$.instance", is("/api/posts")))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("text")))
                .andExpect(jsonPath("$.violations[0].message", is("Text cannot be empty")))
                .andReturn();
    }

    @Test
    void shouldUpdatePost() throws Exception {
        Long postId = 1L;
        Post post = new Post(postId, "Updated text");
        given(postService.findPostById(postId)).willReturn(Optional.of(post));
        given(postService.savePost(any(Post.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc
                .perform(
                        put("/api/posts/{id}", post.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(post.getText())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingPost() throws Exception {
        Long postId = 1L;
        given(postService.findPostById(postId)).willReturn(Optional.empty());
        Post post = new Post(postId, "Updated text");

        this.mockMvc
                .perform(
                        put("/api/posts/{id}", postId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePost() throws Exception {
        Long postId = 1L;
        Post post = new Post(postId, "Some text");
        given(postService.findPostById(postId)).willReturn(Optional.of(post));
        doNothing().when(postService).deletePostById(post.getId());

        this.mockMvc
                .perform(delete("/api/posts/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(post.getText())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingPost() throws Exception {
        Long postId = 1L;
        given(postService.findPostById(postId)).willReturn(Optional.empty());

        this.mockMvc.perform(delete("/api/posts/{id}", postId)).andExpect(status().isNotFound());
    }
}