package com.zjgs.equip.equipment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.equipment.dto.EquipmentRequest;
import com.zjgs.equip.equipment.dto.EquipmentVO;
import com.zjgs.equip.equipment.entity.Equipment;
import com.zjgs.equip.equipment.entity.EquipmentBorrow;
import com.zjgs.equip.equipment.mapper.EquipmentBorrowMapper;
import com.zjgs.equip.equipment.mapper.EquipmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 器材基础资料与库存服务。
 */
@Slf4j
@Service
public class EquipmentService {
    private final EquipmentMapper equipmentMapper;
    private final EquipmentBorrowMapper borrowMapper;

    public EquipmentService(EquipmentMapper equipmentMapper, EquipmentBorrowMapper borrowMapper) {
        this.equipmentMapper = equipmentMapper;
        this.borrowMapper = borrowMapper;
    }

    public List<EquipmentVO> list(String keyword) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Equipment::getName, keyword.trim());
        }
        wrapper.orderByAsc(Equipment::getId);
        return equipmentMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    public EquipmentVO get(Long id) {
        return toVO(require(id));
    }

    public EquipmentVO create(EquipmentRequest request) {
        Equipment equipment = new Equipment();
        equipment.setName(request.getName().trim());
        equipment.setPhotoUrl(blankToNull(request.getPhotoUrl()));
        equipment.setInfoUrl(blankToNull(request.getInfoUrl()));
        equipment.setTotalNumber(request.getTotalNumber());
        equipment.setUsedNumber(0);
        equipment.setStatus(1);
        equipment.setVersion(0);
        equipmentMapper.insert(equipment);
        return toVO(require(equipment.getId()));
    }

    public EquipmentVO update(Long id, EquipmentRequest request) {
        Equipment existing = require(id);
        int used = existing.getUsedNumber() == null ? 0 : existing.getUsedNumber();
        if (request.getTotalNumber() < used) {
            throw new BusinessException(ErrorCode.CONFLICT, "器材总数不能小于当前已借出数量 " + used);
        }
        // 条件更新 + version 自增，避免并发覆盖库存。
        int rows = equipmentMapper.update(null, new UpdateWrapper<Equipment>()
                .eq("id", id)
                .eq("version", existing.getVersion() == null ? 0 : existing.getVersion())
                .set("name", request.getName().trim())
                .set("photo_url", blankToNull(request.getPhotoUrl()))
                .set("info_url", blankToNull(request.getInfoUrl()))
                .set("total_number", request.getTotalNumber())
                .setSql("version = version + 1"));
        if (rows == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "器材信息已被修改，请刷新后重试");
        }
        return toVO(require(id));
    }

    public void delete(Long id) {
        require(id);
        Long activeCount = borrowMapper.selectCount(new LambdaQueryWrapper<EquipmentBorrow>()
                .eq(EquipmentBorrow::getEquipmentId, id)
                .in(EquipmentBorrow::getStatus, 0, 1));
        if (activeCount != null && activeCount > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "该器材存在申请中或已借出的记录，无法删除");
        }
        try {
            equipmentMapper.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // 历史借用记录通过外键引用器材，保留审计数据，不能物理删除。
            log.warn("Delete equipment {} blocked by borrow history", id);
            throw new BusinessException(ErrorCode.CONFLICT, "该器材存在历史借用记录，无法删除，可将状态置为停用");
        }
    }

    public Equipment require(Long id) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "器材不存在");
        }
        return equipment;
    }

    public EquipmentVO toVO(Equipment equipment) {
        int total = equipment.getTotalNumber() == null ? 0 : equipment.getTotalNumber();
        int used = equipment.getUsedNumber() == null ? 0 : equipment.getUsedNumber();
        return EquipmentVO.builder()
                .id(equipment.getId())
                .name(equipment.getName())
                .photoUrl(equipment.getPhotoUrl())
                .infoUrl(equipment.getInfoUrl())
                .totalNumber(total)
                .usedNumber(used)
                .availableNumber(Math.max(total - used, 0))
                .status(equipment.getStatus())
                .createdAt(equipment.getCreatedAt())
                .updatedAt(equipment.getUpdatedAt())
                .build();
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
