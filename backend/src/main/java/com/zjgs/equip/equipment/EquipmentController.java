package com.zjgs.equip.equipment;

import com.zjgs.equip.common.ApiResponse;
import com.zjgs.equip.equipment.dto.BorrowVO;
import com.zjgs.equip.equipment.dto.EquipmentRequest;
import com.zjgs.equip.equipment.dto.EquipmentVO;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/equipments")
public class EquipmentController {
    private final EquipmentService equipmentService;
    private final BorrowService borrowService;

    public EquipmentController(EquipmentService equipmentService, BorrowService borrowService) {
        this.equipmentService = equipmentService;
        this.borrowService = borrowService;
    }

    @GetMapping
    public ApiResponse<List<EquipmentVO>> list(@RequestParam(required = false) String keyword) {
        return ApiResponse.ok(equipmentService.list(keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<EquipmentVO> get(@PathVariable Long id) {
        return ApiResponse.ok(equipmentService.get(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<EquipmentVO> create(@Valid @RequestBody EquipmentRequest request) {
        return ApiResponse.ok(equipmentService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<EquipmentVO> update(@PathVariable Long id,
                                           @Valid @RequestBody EquipmentRequest request) {
        return ApiResponse.ok(equipmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}/borrows")
    @PreAuthorize("hasRole('TEACHER')")
    public ApiResponse<List<BorrowVO>> borrows(@PathVariable Long id) {
        return ApiResponse.ok(borrowService.listByEquipment(id));
    }
}
