package com.kalhan.post_service.service;

import com.kalhan.post_service.dto.CreateUpdatePostRequest;
import com.kalhan.post_service.dto.PostDto;
import com.kalhan.post_service.dto.UserDto;
import com.kalhan.post_service.entity.Post;
import com.kalhan.post_service.file.FileStorageService;
import com.kalhan.post_service.handler.exception.ResourceNoAccessException;
import com.kalhan.post_service.handler.exception.ResourceNotFoundException;
import com.kalhan.post_service.mapper.PostMapper;
import com.kalhan.post_service.repository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("User service test")
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private  PostMapper postMapper;
    @Mock
    private  PostRepository postRepository;
    @Mock
    private  FileStorageService fileStorageService;
    @Mock
    private  UserApiClient userApiClient;
    @InjectMocks
    private PostService postService;

    @DisplayName("get all post successfully")
    @Test
    void test_getAllPosts(){
        //given
        Post post1 = new Post();
        post1.setId(1);
        post1.setContent("post1");

        Post post2 = new Post();
        post2.setId(2);
        post2.setContent("post2");

        List<Post> postList = new ArrayList<>(){{
            add(post1);
            add(post2);
        }};

        PostDto postDto1 = new PostDto();
        postDto1.setContent("post1");

        PostDto postDto2 = new PostDto();
        postDto2.setContent("post2");

        List<PostDto> postDtoList =  new ArrayList<>(){{
            add(postDto1);
            add(postDto2);
        }};

        //when
        when(postRepository.findAll()).thenReturn(postList);
        when(postMapper.toPostDto(post1)).thenReturn(postDto1);
        when(postMapper.toPostDto(post2)).thenReturn(postDto2);

        //then
        List<PostDto> resultList = postService.getAllPosts();

        assertEquals(resultList,postDtoList);

        //verify
        verify(postRepository).findAll();
        verifyNoMoreInteractions(postRepository);
    }

    @DisplayName("get post by id successfully")
    @Test
    void test_getPostById(){
        //given
        Integer id = 1;

        Post post = new Post();
        post.setContent("post1");

        PostDto postDto = new PostDto();
        postDto.setContent("postdto1");

        //when
        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(postMapper.toPostDto(post)).thenReturn(postDto);

        //then
        PostDto result = postService.getPostById(id);

        assertEquals(result,postDto);

        //verfiy
        verify(postRepository).findById(id);
        verifyNoMoreInteractions(postRepository);
    }

    @DisplayName("should throw exception when post not found with given id")
    @Test
    void getPostById_shouldThrowException_whenPostNotFoundWithGivenId(){
        //given
        Integer id = 2;

        //when
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        //that
        Assertions.assertThatThrownBy(
                () -> postService.getPostById(id)
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found with this id ");


    }

    @DisplayName("get user post successfully")
    @Test
    void test_getUserPosts(){
        //given
        String userId = "i1";

        Post post1 = new Post();
        post1.setContent("post1");
        post1.setUserId("i1");

        Post post2 = new Post();
        post2.setContent("post2");
        post2.setUserId("i1");

        List<Post> postList = new ArrayList<>(){{
            add(post1);
            add(post2);
        }};

        PostDto postDto1 = new PostDto();
        postDto1.setContent("postDto1");
        postDto1.setUserId("i1");

        PostDto postDto2 = new PostDto();
        postDto2.setContent("postDto2");
        postDto2.setUserId("i1");

        List<PostDto> postDtoList = new ArrayList<>(){{
            add(postDto1);
            add(postDto2);
        }};

        //when
        when(postRepository.findByUserId(userId)).thenReturn(postList);
        when(postMapper.toPostDto(post1)).thenReturn(postDto1);
        when(postMapper.toPostDto(post2)).thenReturn(postDto2);

        //then
        List<PostDto> result = postService.getUserPosts(userId);

        assertEquals(result,postDtoList);

        //verify
        verify(postRepository).findByUserId(userId);
        verify(postMapper).toPostDto(post1);
        verify(postMapper).toPostDto(post2);
        verifyNoMoreInteractions(postRepository,postMapper);

    }


    @DisplayName("get user saved successfully")
    @Test
    void test_getUserSaved(){
        //given
        String userId = "i1";

        Post post1 = new Post();
        post1.setContent("post1");
        post1.setUserId("i1");
        post1.setSaved(Set.of("i1"));

        Post post2 = new Post();
        post2.setContent("post2");
        post2.setUserId("i2");
        post2.setSaved(Set.of("i2"));


        List<Post> postList = new ArrayList<>(){{
            add(post1);
            add(post2);
        }};

        PostDto postDto = new PostDto();
        postDto.setContent("postDto1");
        postDto.setUserId("i1");

        PostDto postDto2 = new PostDto();
        postDto2.setContent("postDto2");
        postDto2.setUserId("i2");

        List<PostDto> postDtoList = new ArrayList<>(){{
            add(postDto);
        }};

        //when
        when(postRepository.findAll()).thenReturn(postList);
        when(postMapper.toPostDto(post1)).thenReturn(postDto);

        //then
        List<PostDto> result = postService.getUserSaved(userId);

        assertEquals(result.size(),1);

        verify(postRepository).findAll();
        verify(postMapper).toPostDto(post1);
    }


    @DisplayName("get saved count")
    @Test
    void test_getSavedCount(){
        //given
        Integer id = 1;

        Post post = new Post();
        post.setContent("post1");

        //when
        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        //then
        Integer count = postService.getSavedCount(id);

        assertEquals(count,1);
    }

    @Disabled
    @Test
    void test_createPost() {
        // Given
        MultipartFile thumbnailFile = mock(MultipartFile.class);
        String title = "Sample Title";
        String content = "Sample Content";
        String userId = "user123";
        String thumbnailPath = "path/to/thumbnail";

        // Mocking the behavior of fileStorageService
        when(fileStorageService.saveFile(thumbnailFile)).thenReturn(thumbnailPath);

        // Mocking the behavior of postMapper
        PostDto postDto = PostDto.builder()
                .title(title)
                .content(content)
                .userId(userId)
                .thumbnail(thumbnailPath)
                .likes(new HashSet<>())
                .saved(new HashSet<>())
                .comments(new ArrayList<>())
                .build();
        Post post = new Post();
        when(postMapper.toPost(postDto)).thenReturn(post);

        // When
        postService.createPost(thumbnailFile, title, content, userId);

        // Then
        verify(fileStorageService).saveFile(thumbnailFile);
        verify(postMapper).toPost(postDto);
        verify(postRepository).save(post);
    }

    @Test
    void test_createPost_shouldThrowException_whenFailedToSaveThumbnail() {
        // Given
        MultipartFile thumbnailFile = mock(MultipartFile.class);
        String title = "Sample Title";
        String content = "Sample Content";
        String userId = "user123";

        // Mocking the behavior of fileStorageService
        when(fileStorageService.saveFile(thumbnailFile)).thenReturn(null);

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            postService.createPost(thumbnailFile, title, content, userId);
        });
        assertEquals("Failed to save thumbnail", thrown.getMessage());

        // Verify
        verify(fileStorageService).saveFile(thumbnailFile);
        verifyNoMoreInteractions(postMapper, postRepository);
    }

    @Test
    void test_updatePost(){
        //given
        Integer id = 1;
        CreateUpdatePostRequest request = new CreateUpdatePostRequest();
        request.setTitle("update");
        request.setContent("content");
        String userId = "i1";

        Post post = new Post();
        post.setTitle("title");
        post.setContent("post");
        post.setUserId("i1");

        UserDto apiUser = new UserDto();
        apiUser.setId("i1");
        apiUser.setRoles(List.of("ROLE_ADMIN"));

        //when
        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(userApiClient.findUserById(userId)).thenReturn(apiUser);

        //then
        postService.updatePost(id,request,userId);

        assertEquals(request.getTitle(),post.getTitle());
        assertEquals(request.getContent(),post.getContent());

        verify(postRepository).findById(id);
        verify(userApiClient).findUserById(userId);
        verify(postRepository).save(post);

    }

    @Test
    void updatePost_shouldThrowException_whenPostNotFoundWithGivenId(){
        //given
        Integer id = 1;
        CreateUpdatePostRequest request = new CreateUpdatePostRequest();
        request.setTitle("update");
        request.setContent("content");
        String userId = "i1";

        //when
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        //then
        Assertions.assertThatThrownBy(
                () -> postService.updatePost(id,request,userId)
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found with this id ");

        //verify
        verify(postRepository).findById(id);
    }

    @Test
    void updatePost_shouldThrowException_whenUserNoAccess(){
        //given
        Integer postId = 1;
        String userId = "user1";
        CreateUpdatePostRequest request = new CreateUpdatePostRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        Post post = new Post();
        post.setId(postId);
        post.setUserId("anotherUser");
        post.setTitle("Original Title");
        post.setContent("Original Content");

        UserDto apiUser = new UserDto();
        apiUser.setId(userId);
        apiUser.setRoles(List.of("ROLE_USER"));

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userApiClient.findUserById(userId)).thenReturn(apiUser);

        //then
        Assertions.assertThatThrownBy(
                () -> postService.updatePost(postId,request,userId)
        ).isInstanceOf(ResourceNoAccessException.class)
                .hasMessageContaining("You have no access to this resource");

        //verfiy
        verify(postRepository).findById(postId);
        verify(userApiClient).findUserById(userId);

    }

    @Test
    void test_uploadThumbnail(){
        //given
        Integer postId = 1;
        String userId = "i1";
        MultipartFile thumbnailFile = mock(MultipartFile.class);
        String thumbnailPath = "path/to/thumbnail";

        Post post = new Post();
        post.setId(postId);
        post.setUserId(userId);

        UserDto apiUser = new UserDto();
        apiUser.setId(userId);
        apiUser.setRoles(List.of("ROLE_ADMIN"));

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userApiClient.findUserById(userId)).thenReturn(apiUser);
        when(fileStorageService.saveFile(thumbnailFile)).thenReturn(thumbnailPath);

        postService.uploadThumbnail(postId, thumbnailFile, userId);

        // Then
        assertEquals(thumbnailPath, post.getThumbnail());
        verify(postRepository).findById(postId);
        verify(userApiClient).findUserById(userId);
        verify(fileStorageService).saveFile(thumbnailFile);
        verify(postRepository).save(post);
    }

    @Test
    void updateThumbnail_shouldThrowException_whenPostNotFoundWithGivenId(){
        //given
        Integer postId = 1;
        String userId = "i1";
        MultipartFile thumbnailFile = mock(MultipartFile.class);
        String thumbnailPath = "path/to/thumbnail";

        Post post = new Post();
        post.setId(postId);
        post.setUserId(userId);

        UserDto apiUser = new UserDto();
        apiUser.setId(userId);
        apiUser.setRoles(List.of("ROLE_ADMIN"));

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThatThrownBy(
                () -> postService.uploadThumbnail(postId,thumbnailFile,userId)
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found with this id ");

    }

    @Test
    void updateThumbnail_shouldReturnException_whenUserNoAccess(){
        // Given
        Integer postId = 1;
        String userId = "user123";
        MultipartFile thumbnailFile = mock(MultipartFile.class);

        Post post = new Post();
        post.setId(postId);
        post.setUserId("differentUser");

        UserDto apiUser = new UserDto();
        apiUser.setId(userId);
        apiUser.setRoles(List.of("ROLE_USER"));

        // When
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userApiClient.findUserById(userId)).thenReturn(apiUser);

        // Then
        ResourceNoAccessException thrown = assertThrows(ResourceNoAccessException.class, () -> {
            postService.uploadThumbnail(postId, thumbnailFile, userId);
        });

        assertEquals("You have no access to this resource", thrown.getMessage());

        verify(postRepository).findById(postId);
        verify(userApiClient).findUserById(userId);
        verify(fileStorageService, never()).saveFile(thumbnailFile);
        verify(postRepository, never()).save(post);
    }

    @Test
    void uploadThumbnail_shouldThrowException_whenThumbnailIsNull(){
        // Given
        Integer postId = 1;
        String userId = "user123";
        MultipartFile thumbnailFile = mock(MultipartFile.class);

        Post post = new Post();
        post.setId(postId);
        post.setUserId("differentUser");

        UserDto apiUser = new UserDto();
        apiUser.setId(userId);
        apiUser.setRoles(List.of("ROLE_ADMIN"));

        // When
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userApiClient.findUserById(userId)).thenReturn(apiUser);
        when(fileStorageService.saveFile(thumbnailFile)).thenReturn(null);

        // Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            postService.uploadThumbnail(postId, thumbnailFile, userId);
        });

        assertEquals("Failed to save thumbnail", thrown.getMessage());

        verify(postRepository).findById(postId);
        verify(userApiClient).findUserById(userId);
    }

    @Test
    void test_likeAndUnlikePost(){
        //given
        Integer postId = 1;
        String userId = "i1";

        String likes1 = "i1";
        String likes2 = "i2";

        Set<String> likelist = new HashSet<>(){{
            add(likes1);
            add(likes2);
        }};

        Post post = new Post();
        post.setTitle("post");
        post.setLikes(likelist);

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        //then
        postService.likeAndUnlikePost(postId,userId);

        assertEquals(post.getLikes().size(),1);

    }

    @Test
    void likeAndUnlikePost_shouldThrowException_whenPostNotFoundWithGivenId(){
        //given
        Integer postId = 1;
        String userId = "i1";

        String likes1 = "i1";
        String likes2 = "i2";

        Set<String> likelist = new HashSet<>(){{
            add(likes1);
            add(likes2);
        }};

        Post post = new Post();
        post.setTitle("post");
        post.setLikes(likelist);

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThatThrownBy(
                () -> postService.likeAndUnlikePost(postId,userId)
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found with this id ");
    }

    @Test
    void test_saveAndUnsavedPost(){
        //given
        Integer postId = 1;
        String userId = "i1";

        String saved1 = "i1";
        String saved2 = "i2";

        Set<String> saveList = new HashSet<>(){{
            add(saved1);
            add(saved2);
        }};

        Post post = new Post();
        post.setTitle("post");
        post.setSaved(saveList);

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        //then
        postService.saveAndUnsavedPost(postId,userId);

        assertEquals(post.getSaved().size(),1);
    }

    @Test
    void saveAndUnsavedPost_shouldThrowException_whenPostNotFoundWithGivenId(){
        //given
        Integer postId = 1;
        String userId = "i1";

        String saved1 = "i1";
        String saved2 = "i2";

        Set<String> saveList = new HashSet<>(){{
            add(saved1);
            add(saved2);
        }};

        Post post = new Post();
        post.setTitle("post");
        post.setLikes(saveList);

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThatThrownBy(
                        () -> postService.saveAndUnsavedPost(postId,userId)
                ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found with this id ");
    }

    @Test
    void test_deletePost(){
        //given
        Integer postId = 1;
        String userId = "i1";

        Post post = new Post();
        post.setTitle("post");
        post.setUserId(userId);

        UserDto apiUser = new UserDto();
        apiUser.setId(userId);
        apiUser.setRoles(List.of("ROLE_ADMIN"));

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userApiClient.findUserById(userId)).thenReturn(apiUser);

        //then
        postService.deletePost(postId,userId);

        //verify
        verify(postRepository).findById(postId);
        verify(userApiClient).findUserById(userId);
    }

    @DisplayName("deletePost should throw ResourceNoAccessException when user has no access")
    @Test
    void test_deletePost_shouldThrowResourceNoAccessException_whenUserHasNoAccess() {
        // Given
        Integer postId = 1;
        String userId = "user123";
        Post post = new Post();
        post.setId(postId);
        post.setUserId("differentUser");

        UserDto apiUser = new UserDto();
        apiUser.setId(userId);
        apiUser.setRoles(List.of("ROLE_USER"));

        // When
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userApiClient.findUserById(userId)).thenReturn(apiUser);

        // Then
        ResourceNoAccessException thrown = assertThrows(ResourceNoAccessException.class, () -> {
            postService.deletePost(postId, userId);
        });

        assertEquals("You have no access to this resource", thrown.getMessage());

        verify(postRepository).findById(postId);
        verify(userApiClient).findUserById(userId);
        verify(postRepository, never()).deleteById(postId);
    }

    @DisplayName("deletePost should throw ResourceNotFoundException when post not found")
    @Test
    void test_deletePost_shouldThrowResourceNotFoundException_whenPostNotFound() {
        // Given
        Integer postId = 1;
        String userId = "user123";

        UserDto apiUser = new UserDto();
        apiUser.setId(userId);
        apiUser.setRoles(List.of("ROLE_USER"));

        // When
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Then
        Assertions.assertThatThrownBy(
                () -> postService.deletePost(postId,userId)
        ).isInstanceOf(ResourceNotFoundException.class)
                        .hasMessageContaining("Post not found with this id ");

        verify(postRepository).findById(postId);
        verify(postRepository, never()).deleteById(postId);
    }
}
