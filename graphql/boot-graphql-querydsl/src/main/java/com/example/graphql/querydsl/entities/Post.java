package com.example.graphql.querydsl.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();

    @JoinColumn(name = "details_Id")
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "post", orphanRemoval = true, fetch = FetchType.LAZY)
    private PostDetails details;

    public Post setId(Long id) {
        this.id = id;
        return this;
    }

    public Post setTitle(String title) {
        this.title = title;
        return this;
    }

    public Post setContent(String content) {
        this.content = content;
        return this;
    }

    public Post setComments(List<PostComment> comments) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        this.comments = comments;
        return this;
    }

    public Post setDetails(PostDetails details) {
        this.details = details;
        return this;
    }

    public void addComment(PostComment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(PostComment comment) {
        this.comments.remove(comment);
        comment.setPost(null);
    }

    public void addDetails(PostDetails details) {
        this.details = details;
        details.setPost(this);
    }

    public void removeDetails() {
        this.details.setPost(null);
        this.details = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Post post = (Post) o;
        return id != null && Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
