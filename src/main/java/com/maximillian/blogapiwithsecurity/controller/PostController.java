package com.maximillian.blogapiwithsecurity.controller;

import com.maximillian.blogapiwithsecurity.Dto.PostResponse;
import com.maximillian.blogapiwithsecurity.Dto.PostsDto;
import com.maximillian.blogapiwithsecurity.Exceptions.CustomException;
import com.maximillian.blogapiwithsecurity.Service.PostService;
import com.maximillian.blogapiwithsecurity.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
   private PostService postService;
   private UserService userService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createPost")
    public ResponseEntity<String> CreatePost(
            @Valid @RequestBody PostsDto postsDto,
            Authentication authentication
            ) throws CustomException {
       UserDetails userDetails1 = (UserDetails) authentication.getPrincipal();
        PostsDto posts = postService.createPost(postsDto, userDetails1.getUsername());
        return new ResponseEntity<>(posts.getTitle(), HttpStatus.CREATED);
    }
    @GetMapping("/getAllPost")
    public ResponseEntity<List<PostsDto>> getAllPost(){
        List<PostsDto> returnedPosts = postService.displayAllPost();
        return new ResponseEntity<>(returnedPosts, HttpStatus.OK);
}
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/deletePost/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long id
       ) throws CustomException {
        PostsDto postsDto = new PostsDto();
        postsDto.setId(id);
        postService.deletePost(postsDto);
        return new ResponseEntity<>("Delete Successful", HttpStatus.ACCEPTED);

    }
    @PreAuthorize("hasROle('ADMIN')")
    @PutMapping("/edit-post/{id}")
    public ResponseEntity<String> editPost(
            @PathVariable Long id,
            @RequestBody PostsDto postsDto) throws CustomException {
        postsDto.setId(id);
        postService.editPost(postsDto);
        return new ResponseEntity<>("update successful", HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostsDto>> searchForPosts(@RequestBody PostsDto postsDto){
        List<PostsDto> theListOfSearches = postService.searchPosts(postsDto.getTitle());
        return new ResponseEntity<>(theListOfSearches, HttpStatus.FOUND);
    }

    @GetMapping("/get-post/{id}")
    public ResponseEntity<PostsDto> viewPost(@PathVariable Long id, PostsDto postsDto) throws CustomException {
        postsDto.setId(id);
        PostsDto postDto1 = postService.viewPost(postsDto);
        return new ResponseEntity<>(postDto1, HttpStatus.FOUND);
    }
    @GetMapping("/viewPost")
    public ResponseEntity<PostResponse> getAllPosts(@RequestParam(defaultValue = "1") int pageNo,
                                           @RequestParam(defaultValue = "5") int pageSize) throws CustomException {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        PostResponse postsPage = postService.getAllPosts(pageable);
        return ResponseEntity.ok(postsPage);
    }
}
