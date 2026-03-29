package com.example.streetleague.ServiceInterface;

import com.example.streetleague.Entity.Post;
import com.example.streetleague.dto.postDTO;

import java.util.List;

public interface PostService {
    Post addPost(postDTO dto);
    List<Post> getPosts();
    Post getPost(Long id);
    Post updatePost(Long id, postDTO dto);
    void deletePost(Long id);
    Post likePost(Long id);

}
