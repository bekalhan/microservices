package com.kalhan.post_service.service;

import com.kalhan.post_service.dto.CreateUpdatePostRequest;
import com.kalhan.post_service.dto.PostDto;
import com.kalhan.post_service.dto.UserDto;
import com.kalhan.post_service.entity.Post;
import com.kalhan.post_service.handler.exception.ResourceNoAccessException;
import com.kalhan.post_service.handler.exception.ResourceNotFoundException;
import com.kalhan.post_service.mapper.PostMapper;
import com.kalhan.post_service.repository.PostRepository;
import com.kalhan.post_service.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;
    private final UserApiClient userApiClient;

    public List<PostDto> getAllPosts() {
        return postRepository.findAll()
                .stream().map(postMapper::toPostDto).toList();
    }

    public PostDto getPostById(Integer id) {
        return postMapper.toPostDto(postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with this id " + id)));
    }

    public List<PostDto> getUserPosts(String userId) {
        List<Post> userPosts = postRepository.findByUserId(userId);
        return userPosts.stream().map(postMapper::toPostDto).toList();
    }

    public List<PostDto> getUserSaved(String userId) {
        List<Post> allPosts = postRepository.findAll();
        List<Post> savedPosts = allPosts.stream()
                .filter(post -> post.getSaved().contains(userId))
                .toList();
        return savedPosts.stream()
                .map(postMapper::toPostDto)
                .collect(Collectors.toList());
    }

    public Integer getSavedCount(Integer postId){
        return Math.toIntExact(postRepository.findById(postId).stream().count());
    }

    public void createPost(MultipartFile thumbnailFile, String title,String content, String userId) {
        String thumbnailPath = fileStorageService.saveFile(thumbnailFile);
        if (thumbnailPath == null) {
            throw new RuntimeException("Failed to save thumbnail");
        }

        PostDto postDto = PostDto.builder()
                .title(title)
                .content(content)
                .userId(userId)
                .thumbnail(thumbnailPath)
                .likes(new HashSet<>())
                .saved(new HashSet<>())
                .comments(new ArrayList<>())
                .build();

        Post post = postMapper.toPost(postDto);
        postRepository.save(post);
    }

    public void updatePost(
            Integer postId,
            CreateUpdatePostRequest request,
            String userId
    ) {
        Post post = approveIdentityAndReturnPost(postId, userId);

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        postRepository.save(post);
    }

    public void uploadThumbnail(Integer postId,MultipartFile thumbnailFile,String userId){
        Post post = approveIdentityAndReturnPost(postId, userId);

        String thumbnailPath = fileStorageService.saveFile(thumbnailFile);
        if (thumbnailPath == null) {
            throw new RuntimeException("Failed to save thumbnail");
        }

        post.setThumbnail(thumbnailPath);
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

    public void saveAndUnsavedPost(Integer postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with this id " + postId));

        Set<String> saveList = post.getSaved();

        if (saveList.contains(userId)) {
            saveList.remove(userId);
        } else {
            saveList.add(userId);
        }

        post.setSaved(saveList);
        postRepository.save(post);
    }

    public void deletePost(
            Integer id,
            String userId
    ) {
        approveIdentityAndReturnPost(id, userId);
        postRepository.deleteById(id);
    }

    private Post approveIdentityAndReturnPost(Integer postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with this id " + postId));

        UserDto apiUser = userApiClient.findUserById(userId);

        if (!Objects.equals(post.getUserId(), userId) && !apiUser.getRoles().contains("ROLE_ADMIN")) {
            throw new ResourceNoAccessException("You have no access to this resource");
        }

        return post;
    }
}
