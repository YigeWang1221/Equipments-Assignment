package com.zjgs.equip.clazz.controller;

import com.zjgs.equip.auth.CurrentUser;
import com.zjgs.equip.clazz.dto.GroupRequest;
import com.zjgs.equip.clazz.dto.GroupVO;
import com.zjgs.equip.clazz.service.GroupService;
import com.zjgs.equip.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final CurrentUser currentUser;

    @GetMapping("/classes/{classId}/groups")
    public ApiResponse<List<GroupVO>> listByClass(@PathVariable("classId") Long classId) {
        return ApiResponse.ok(groupService.listByClass(classId));
    }

    @GetMapping("/groups")
    public ApiResponse<List<GroupVO>> list(@RequestParam(value = "classId", required = false) Long classId) {
        return ApiResponse.ok(groupService.list(classId));
    }

    @PostMapping("/groups")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<GroupVO> create(@Valid @RequestBody GroupRequest request) {
        return ApiResponse.ok(groupService.create(request, currentUser.teacherId()));
    }

    @PutMapping("/groups/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<GroupVO> update(@PathVariable("id") Long id,
                                       @Valid @RequestBody GroupRequest request) {
        return ApiResponse.ok(groupService.update(id, request, currentUser.teacherId()));
    }

    @DeleteMapping("/groups/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        groupService.delete(id, currentUser.teacherId());
        return ApiResponse.ok();
    }

    @DeleteMapping("/classes/{classId}/groups")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<Void> deleteByClass(@PathVariable("classId") Long classId) {
        groupService.deleteByClass(classId, currentUser.teacherId());
        return ApiResponse.ok();
    }

    @PostMapping("/groups/{id}/students/{stuId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<GroupVO> assignStudent(@PathVariable("id") Long id,
                                              @PathVariable("stuId") Long stuId) {
        return ApiResponse.ok(groupService.assignStudent(id, stuId, currentUser.teacherId()));
    }

    @DeleteMapping("/groups/{id}/students/{stuId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<GroupVO> unassignStudent(@PathVariable("id") Long id,
                                                @PathVariable("stuId") Long stuId) {
        return ApiResponse.ok(groupService.unassignStudent(id, stuId, currentUser.teacherId()));
    }
}
