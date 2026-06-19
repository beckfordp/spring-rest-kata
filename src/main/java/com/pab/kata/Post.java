package com.pab.kata;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;import java.util.Objects;

@Entity
public class Post {

    private Long userId;

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private String post;

    public Post() {
    }

    public Post(Long userId, Long id, String title, String post) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.post = post;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(userId, post.userId) && Objects.equals(id, post.id) && Objects.equals(title, post.title) && Objects.equals(post, post.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, id, title, post);
    }

    @Override public String toString() {
    return "Post{" +
            "userId=" + userId +
            ", id=" + id +
            ", title='" + title + '\'' +
            ", post='" + post + '\'' +
            '}';
}}


