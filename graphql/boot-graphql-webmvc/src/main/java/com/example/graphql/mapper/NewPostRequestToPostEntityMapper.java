package com.example.graphql.mapper;

import com.example.graphql.entities.PostEntity;
import com.example.graphql.entities.PostTagEntity;
import com.example.graphql.entities.TagEntity;
import com.example.graphql.model.request.NewPostRequest;
import com.example.graphql.model.request.TagsRequest;
import com.example.graphql.repositories.TagRepository;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.CollectionUtils;

// After Mapping will not be set if we use builder pattern, hence disabled it
@Mapper(uses = {TagRepository.class})
public interface NewPostRequestToPostEntityMapper {

    @Mapping(target = "tags", ignore = true)
    @Mapping(
            target = "publishedAt",
            expression = "java(newPostRequest.published() ? java.time.LocalDateTime.now() : null)")
    PostEntity convert(NewPostRequest newPostRequest, @Context TagRepository tagRepository);

    @Mapping(target = "tags", ignore = true)
    void updatePostEntity(
            NewPostRequest newPostRequest,
            @MappingTarget PostEntity postEntity,
            @Context TagRepository tagRepository);

    @AfterMapping
    default void afterMapping(
            NewPostRequest newPostRequest,
            @MappingTarget PostEntity postEntity,
            @Context TagRepository tagRepository) {
        if (!CollectionUtils.isEmpty(newPostRequest.tags())) {

            List<TagEntity> tagEntitiesFromDb =
                    postEntity.getTags().stream().map(PostTagEntity::getTagEntity).toList();

            List<TagEntity> tagEntitiesToRemove =
                    tagEntitiesFromDb.stream()
                            .filter(
                                    tagEntity ->
                                            !newPostRequest.tags().stream()
                                                    .map(TagsRequest::tagName)
                                                    .toList()
                                                    .contains(tagEntity.getTagName()))
                            .toList();
            tagEntitiesToRemove.forEach(postEntity::removeTag);

            List<TagEntity> tagEntitiesToUpdate =
                    tagEntitiesFromDb.stream()
                            .filter(
                                    tagEntity ->
                                            newPostRequest.tags().stream()
                                                    .map(TagsRequest::tagName)
                                                    .toList()
                                                    .contains(tagEntity.getTagName()))
                            .toList();

            List<TagsRequest> tagEntitiesToInsert =
                    newPostRequest.tags().stream()
                            .filter(
                                    tagsRequest ->
                                            !tagEntitiesToUpdate.stream()
                                                    .map(TagEntity::getTagName)
                                                    .toList()
                                                    .contains(tagsRequest.tagName()))
                            .toList();

            tagEntitiesToInsert.forEach(
                    tagsRequest -> postEntity.addTag(getTagEntity(tagRepository, tagsRequest)));
        }
    }

    default TagEntity getTagEntity(TagRepository tagRepository, TagsRequest tagsRequest) {
        return tagRepository
                .findByTagNameIgnoreCase(tagsRequest.tagName())
                .orElseGet(
                        () ->
                                tagRepository.save(
                                        new TagEntity()
                                                .setTagName(tagsRequest.tagName())
                                                .setTagDescription(tagsRequest.tagDescription())));
    }
}
