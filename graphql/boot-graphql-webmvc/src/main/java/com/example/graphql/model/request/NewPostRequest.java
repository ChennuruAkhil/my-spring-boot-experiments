package com.example.graphql.model.request;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

public record NewPostRequest(
        @NotBlank(message = "PostTitle must not be blank") String title,
        @NotBlank(message = "PostContent must not be blank") String content,
        @NotBlank(message = "Email must not be blank") String email,
        boolean published,
        PostDetailsRequest details,
        List<TagsRequest> tags)
        implements Serializable {}
