package com.myblog.service.impl;

import com.myblog.Entity.Comment;
import com.myblog.Entity.Post;
import com.myblog.Repository.CommentRepository;
import com.myblog.Repository.PostRepository;
import com.myblog.exception.BlogApiException;
import com.myblog.exception.ResourceNotFoundException;
import com.myblog.payload.CommentDto;
import com.myblog.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;
    private PostRepository postRepository;

    private ModelMapper mapper;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper mapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
    }

    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {
       Comment comment = mapToEntity(commentDto);

        Post post =postRepository.findById(postId).orElseThrow(
                ()-> new ResourceNotFoundException("Post", "id", postId)
        );

        comment.setPost(post);

        Comment newCommment = commentRepository.save(comment);


        return mapToDto(newCommment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
         return comments.stream().map(comment -> mapToDto(comment)).collect(Collectors.toList());

    }

    @Override
    public CommentDto getCommentById(long postId, long commentId) {
        Post post =postRepository.findById(postId).orElseThrow(
                ()-> new ResourceNotFoundException("Post", "id", postId));

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                ()-> new ResourceNotFoundException("Comment","id", commentId));

        if (!Objects.equals(comment.getPost().getId(), post.getId())) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "comment does not exist");
        }
        return mapToDto(comment);


    }

    @Override
    public CommentDto updateComment(Long postId, Long id, CommentDto commentDto) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", id));

        if (!Objects.equals(comment.getPost().getId(), post.getId())) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "comment does not exist");
        }
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());

        Comment updatedComment = commentRepository.save(comment);
        return mapToDto(comment);
    }

    @Override
    public void deleteComment(Long postId, Long id) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", id));

        if (!Objects.equals(comment.getPost().getId(), post.getId())) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST, "comment does not exist");
        }

        commentRepository.deleteById(comment.getId());
    }


    Comment mapToEntity(CommentDto commentDto){
        Comment comment = mapper.map(commentDto, Comment.class);
        return comment;

    }
    CommentDto mapToDto(Comment newCommment){
        CommentDto dto = mapper.map(newCommment, CommentDto.class);

        return dto;

    }
}
