package com.myblog.service;

import com.myblog.payload.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(long postId, CommentDto commentDto);

   List<CommentDto> getCommentsByPostId(long postId);
   CommentDto getCommentById(long postId, long commentId);


    CommentDto updateComment(Long postId, Long id, CommentDto commentDto);

    void deleteComment(Long postId, Long id);
}
