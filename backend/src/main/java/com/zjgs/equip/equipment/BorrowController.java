package com.zjgs.equip.equipment;

import com.zjgs.equip.auth.CurrentUser;
import com.zjgs.equip.common.ApiResponse;
import com.zjgs.equip.equipment.dto.BorrowRequest;
import com.zjgs.equip.equipment.dto.BorrowVO;
import com.zjgs.equip.equipment.dto.RejectRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BorrowController {
    private final BorrowService borrowService;
    private final CurrentUser currentUser;

    public BorrowController(BorrowService borrowService, CurrentUser currentUser) {
        this.borrowService = borrowService;
        this.currentUser = currentUser;
    }

    @PostMapping("/equipments/{id}/borrows")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<BorrowVO> apply(@PathVariable Long id, @Valid @RequestBody BorrowRequest request) {
        return ApiResponse.ok(borrowService.apply(id, request.getNum()));
    }

    @GetMapping("/borrows")
    public ApiResponse<List<BorrowVO>> list(@RequestParam(required = false) Integer status,
                                            @RequestParam(required = false) Long stuId,
                                            @RequestParam(required = false) Long equipmentId) {
        return ApiResponse.ok(borrowService.list(status, stuId, equipmentId, currentUser.require()));
    }

    @GetMapping("/borrows/mine")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<List<BorrowVO>> mine(@RequestParam(required = false) Integer status) {
        return ApiResponse.ok(borrowService.mine(status));
    }

    @PostMapping("/borrows/{id}/approve")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<BorrowVO> approve(@PathVariable Long id) {
        return ApiResponse.ok(borrowService.approve(id));
    }

    @PostMapping("/borrows/{id}/reject")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<BorrowVO> reject(@PathVariable Long id,
                                        @Valid @RequestBody(required = false) RejectRequest request) {
        return ApiResponse.ok(borrowService.reject(id, request == null ? null : request.getRemark()));
    }

    @PostMapping("/borrows/{id}/return")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<BorrowVO> markReturned(@PathVariable Long id) {
        return ApiResponse.ok(borrowService.markReturned(id));
    }

    @PostMapping("/borrows/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<BorrowVO> cancel(@PathVariable Long id) {
        return ApiResponse.ok(borrowService.cancel(id));
    }
}
