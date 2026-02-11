package com.springschedule.comment.controller;

import com.springschedule.comment.dto.CommentResponse;
import com.springschedule.comment.dto.CreateCommentRequest;
import com.springschedule.comment.dto.UpdateCommentRequest;
import com.springschedule.comment.service.CommentService;
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

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @PathVariable Long scheduleId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.save(scheduleId, request));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long scheduleId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.findAllBySchedule(scheduleId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable Long scheduleId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.update(scheduleId, commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long scheduleId,
            @PathVariable Long commentId
    ) {
        commentService.delete(scheduleId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
