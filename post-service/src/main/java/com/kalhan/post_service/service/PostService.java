package com.kalhan.post_service.service;

import com.kalhan.post_service.dto.CreateUpdatePostRequest;
import com.kalhan.post_service.dto.PostDto;
import com.kalhan.post_service.dto.UserDto;
import com.kalhan.post_service.entity.Post;
import com.kalhan.post_service.handler.exception.ResourceNoAccessException;
import com.kalhan.post_service.handler.exception.ResourceNotFoundException;
import com.kalhan.post_service.mapper.PostMapper;
import com.kalhan.post_service.repository.PostRepository;
import com.kalhan.user_service.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserApiClient userApiClient;

    public List<PostDto> getAllPosts(){
        return postRepository.findAll()
                .stream().map(postMapper::toPostDto).toList();
    }

    public PostDto getPostById(Integer id){
        return postMapper.toPostDto(postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post not found with this id "+id)));
    }

    public List<PostDto> getUserPosts(String userId){
        List<Post> userPosts = postRepository.findByUserId(userId);
        return userPosts.stream().map(postMapper::toPostDto).toList();
    }

    public void createPost(
            CreateUpdatePostRequest request,
            String username
    ){
        User user = userApiClient.findUserByEmail(username);

        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }

        PostDto postDto = PostDto.builder()
                .title(request.title())
                .thumbnail(request.thumbnail())
                .content(request.content())
                .userId(user.getId())
                .build();

        Post post = postMapper.toPost(postDto);
        postRepository.save(post);
    }

    public void updatePost(
            Integer id,
            CreateUpdatePostRequest request,
            String username
    ){
        Post post = approveIdentityAndReturnPost(id,username);

        post.setTitle(request.title());
        post.setThumbnail(request.thumbnail());
        post.setContent(request.content());
        postRepository.save(post);


    }

    public void likeAndUnlikePost(Integer postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with this id " + postId));

        Set<String> likesList = post.getLikes();

        if (likesList.contains(userId)) {
            likesList.remove(userId);
        } else {
            likesList.add(userId);
        }

        post.setLikes(likesList);
        postRepository.save(post);
    }

    public List<UserDto> getPostLikes(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with this id " + postId));

        Set<String> likesList = post.getLikes();

        return likesList.stream()
                .map(userApiClient::findUserById)
                .map(user -> {
                    return UserDto
                            .builder()
                            .firstname(user.getFirstname())
                            .lastname(user.getLastname())
                            .profilePhoto(user.getProfilePhoto()).build();
                })
                .toList();
    }

    public void deletePost(
            Integer id,
            String username
    ){
        approveIdentityAndReturnPost(id,username);
        postRepository.deleteById(id);
    }

    private Post approveIdentityAndReturnPost(Integer id,String username){
        User user = userApiClient.findUserByEmail(username);

        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with this id "+id));

        if(!Objects.equals(post.getUserId(), user.getId())){
            throw  new ResourceNoAccessException("You have no access to this resource");
        }

        return post;
    }

}
