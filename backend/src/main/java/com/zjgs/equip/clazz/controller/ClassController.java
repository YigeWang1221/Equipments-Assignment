package com.zjgs.equip.clazz.controller;

import com.zjgs.equip.auth.CurrentUser;
import com.zjgs.equip.clazz.dto.ClassRequest;
import com.zjgs.equip.clazz.dto.ClassVO;
import com.zjgs.equip.clazz.service.ClassService;
import com.zjgs.equip.common.ApiResponse;
import com.zjgs.equip.homework.dto.WorkVO;
import com.zjgs.equip.homework.service.WorkService;
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
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class ClassController {
    private final ClassService classService;
    private final WorkService workService;
    private final CurrentUser currentUser;

    @GetMapping
    public ApiResponse<List<ClassVO>> list(@RequestParam(value = "type", required = false) String type,
                                           @RequestParam(value = "year", required = false) Integer year) {
        return ApiResponse.ok(classService.list(type, year));
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassVO> get(@PathVariable("id") Long id) {
        return ApiResponse.ok(classService.get(id));
    }

    /** 兼容前端按班级获取作业列表的调用。 */
    @GetMapping("/{id}/works")
    public ApiResponse<List<WorkVO>> works(@PathVariable("id") Long id) {
        return ApiResponse.ok(workService.list(id, null));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<ClassVO> create(@Valid @RequestBody ClassRequest request) {
        return ApiResponse.ok(classService.create(request, currentUser.teacherId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<ClassVO> update(@PathVariable("id") Long id,
                                       @Valid @RequestBody ClassRequest request) {
        return ApiResponse.ok(classService.update(id, request, currentUser.teacherId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        classService.delete(id, currentUser.teacherId());
        return ApiResponse.ok();
    }
}
