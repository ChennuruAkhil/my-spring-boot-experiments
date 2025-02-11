package com.example.graphql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "PostTag")
@Table(name = "post_tag")
@Setter
@Getter
public class PostTagEntity implements Serializable {

    @EmbeddedId
    private PostTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private TagEntity tagEntity;

    @Column(name = "created_on")
    private LocalDateTime createdOn = LocalDateTime.now();

    public PostTagEntity() {
        this.createdOn = LocalDateTime.now();
    }

    public PostTagEntity(PostEntity postEntity, TagEntity tagEntity) {
        this.postEntity = postEntity;
        this.tagEntity = tagEntity;
        this.id = new PostTagId(postEntity.getId(), tagEntity.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostTagEntity that = (PostTagEntity) o;
        return Objects.equals(this.postEntity, that.postEntity) && Objects.equals(this.tagEntity, that.tagEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.postEntity, this.tagEntity);
    }
}
