package com.zjgs.equip.user.controller;

import com.zjgs.equip.auth.CurrentUser;
import com.zjgs.equip.clazz.dto.ClassVO;
import com.zjgs.equip.clazz.dto.GroupVO;
import com.zjgs.equip.common.ApiResponse;
import com.zjgs.equip.user.dto.StudentRequest;
import com.zjgs.equip.user.dto.StudentVO;
import com.zjgs.equip.user.service.StudentService;
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
public class StudentController {
    private final StudentService studentService;
    private final CurrentUser currentUser;

    @GetMapping("/classes/{id}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<StudentVO>> listByClass(@PathVariable("id") Long id) {
        return ApiResponse.ok(studentService.listByClass(id));
    }

    @GetMapping("/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<StudentVO>> list(@RequestParam(value = "classId", required = false) Long classId) {
        return ApiResponse.ok(studentService.list(classId));
    }

    @PostMapping("/students")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<StudentVO> create(@Valid @RequestBody StudentRequest request) {
        return ApiResponse.ok(studentService.create(request, currentUser.teacherId()));
    }

    @PutMapping("/students/{stuId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<StudentVO> update(@PathVariable("stuId") Long stuId,
                                         @Valid @RequestBody StudentRequest request) {
        return ApiResponse.ok(studentService.update(stuId, request, currentUser.teacherId()));
    }

    @DeleteMapping("/students/{stuId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<Void> delete(@PathVariable("stuId") Long stuId,
                                    @RequestParam(value = "classId", required = false) Long classId) {
        studentService.delete(stuId, classId, currentUser.teacherId());
        return ApiResponse.ok();
    }

    @GetMapping("/students/{stuId}/class")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    public ApiResponse<ClassVO> studentClass(@PathVariable("stuId") Long stuId) {
        return ApiResponse.ok(studentService.getClassOfStudent(stuId, currentUser.require()));
    }

    @GetMapping("/students/{stuId}/group")
    @PreAuthorize("hasAnyRole('TEACHER','STUDENT')")
    public ApiResponse<GroupVO> studentGroup(@PathVariable("stuId") Long stuId) {
        return ApiResponse.ok(studentService.getGroupOfStudent(stuId, currentUser.require()));
    }

    @GetMapping("/students/me/class")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<ClassVO> myClass() {
        return ApiResponse.ok(studentService.getClassOfStudent(currentUser.studentId(), currentUser.require()));
    }

    @GetMapping("/students/me/group")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<GroupVO> myGroup() {
        return ApiResponse.ok(studentService.getGroupOfStudent(currentUser.studentId(), currentUser.require()));
    }
}
