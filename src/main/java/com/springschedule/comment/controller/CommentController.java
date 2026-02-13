package com.springschedule.comment.controller;

import com.springschedule.comment.dto.CommentResponse;
import com.springschedule.comment.dto.CreateCommentRequest;
import com.springschedule.comment.dto.UpdateCommentRequest;
import com.springschedule.comment.service.CommentService;
import com.springschedule.common.exception.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules/{scheduleId}/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성: 로그인 사용자만 작성 가능
    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long scheduleId,
            @Valid @RequestBody CreateCommentRequest request,
            @SessionAttribute(name = "loginUserId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new UnauthorizedException("님 로그인 해 주세요");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.save(scheduleId, request, loginUserId));
    }

    // 댓글 목록 조회
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long scheduleId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.findAllBySchedule(scheduleId));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long scheduleId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            @SessionAttribute(name = "loginUserId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new UnauthorizedException("님 로그인 해 주세요");
        }
        return ResponseEntity.status(HttpStatus.OK).body(commentService.update(scheduleId, commentId, request, loginUserId));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long scheduleId,
            @PathVariable Long commentId,
            @SessionAttribute(name = "loginUserId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new UnauthorizedException("님 로그인 해 주세요");
        }
        commentService.delete(scheduleId, commentId, loginUserId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
