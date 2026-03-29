package com.example.streetleague;

import com.example.streetleague.Controller.PostController;
import com.example.streetleague.Entity.Post;
import com.example.streetleague.ServiceInterface.PostService;
import com.example.streetleague.dto.postDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private Post createPost() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setDescription("Test Description");
        return post;
    }

    private postDTO createDTO() {
        postDTO dto = new postDTO();
        dto.setTitle("Test Post");
        dto.setDescription("Test Description");
        return dto;
    }

    @Test
    void shouldAddPostWithImage() throws Exception {
        MultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        Post post = createPost();

        when(postService.addPost(any(postDTO.class))).thenReturn(post);

        ResponseEntity<Post> response = postController.addPost(
                "Test Post",
                "Test Description",
                image
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Test Post", response.getBody().getTitle());

        verify(postService).addPost(any(postDTO.class));
    }

    @Test
    void shouldGetAllPosts() {
        Post post = createPost();

        when(postService.getPosts()).thenReturn(List.of(post));

        ResponseEntity<List<Post>> response = postController.getPosts();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());

        verify(postService).getPosts();
    }

    @Test
    void shouldGetPostById() {
        Post post = createPost();

        when(postService.getPost(1L)).thenReturn(post);

        ResponseEntity<Post> response = postController.getPost(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(post, response.getBody());

        verify(postService).getPost(1L);
    }

    @Test
    void shouldUpdatePost() {
        postDTO dto = createDTO();
        Post post = createPost();

        when(postService.updatePost(1L, dto)).thenReturn(post);

        ResponseEntity<Post> response = postController.updatePost(1L, dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(post, response.getBody());

        verify(postService).updatePost(1L, dto);
    }

    @Test
    void shouldDeletePost() {
        doNothing().when(postService).deletePost(1L);

        ResponseEntity<Void> response = postController.deletePost(1L);

        assertEquals(204, response.getStatusCode().value());

        verify(postService).deletePost(1L);
    }

    @Test
    void shouldLikePost() {
        Post post = createPost();

        when(postService.likePost(1L)).thenReturn(post);

        ResponseEntity<Post> response = postController.likePost(1L);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(post, response.getBody());

        verify(postService).likePost(1L);
    }
}