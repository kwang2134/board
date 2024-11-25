package com.kwang.board.comment.usecase;

public interface CommentCrudUseCase {
    /*Comment createComt(CommentDTO.UserRequest comtDTO);

    Comment createComt(CommentDTO.NonUserRequest comtDTO);

    Comment updateComt(Long comtId, CommentDTO.UserRequest comtDTO);

    Comment updateComt(Long comtId, CommentDTO.NonUserRequest comtDTO);*/

    void deleteComt(Long comtId);

//    List<CommentDTO.Response> viewComts(Long postId);

}
