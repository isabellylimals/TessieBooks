package com.isabelly.tessiebooks.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.isabelly.tessiebooks.entity.Post;
import com.isabelly.tessiebooks.entity.User;
import com.isabelly.tessiebooks.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<Post> getFeed() {
        return postRepository.findByOrderByCreatedAtDesc();
    }

    public List<Post> getUserPosts(Long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Post createPost(User user, String content) {
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        return postRepository.save(post);
    }

    public Post deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post não encontrado"));
        postRepository.delete(post);
        return post;
    }
}
