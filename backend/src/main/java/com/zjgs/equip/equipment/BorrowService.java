package com.zjgs.equip.equipment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.zjgs.equip.auth.CurrentUser;
import com.zjgs.equip.auth.UserPrincipal;
import com.zjgs.equip.clazz.entity.StudentClass;
import com.zjgs.equip.clazz.mapper.StudentClassMapper;
import com.zjgs.equip.common.BorrowStatus;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.common.Role;
import com.zjgs.equip.equipment.dto.BorrowVO;
import com.zjgs.equip.equipment.entity.Equipment;
import com.zjgs.equip.equipment.entity.EquipmentBorrow;
import com.zjgs.equip.equipment.entity.LabUsageLog;
import com.zjgs.equip.equipment.mapper.EquipmentBorrowMapper;
import com.zjgs.equip.equipment.mapper.EquipmentMapper;
import com.zjgs.equip.equipment.mapper.LabUsageLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 借用申请状态机：0申请中 -> 1已借出 -> 2已归还；0 -> 3已拒绝；0 -> 4已取消。
 * 所有状态流转都使用带当前状态条件的更新，保证审计记录不被并发覆盖。
 */
@Slf4j
@Service
public class BorrowService {
    private final EquipmentMapper equipmentMapper;
    private final EquipmentBorrowMapper borrowMapper;
    private final LabUsageLogMapper labUsageLogMapper;
    private final StudentClassMapper studentClassMapper;
    private final EquipmentService equipmentService;
    private final CurrentUser currentUser;

    public BorrowService(EquipmentMapper equipmentMapper,
                         EquipmentBorrowMapper borrowMapper,
                         LabUsageLogMapper labUsageLogMapper,
                         StudentClassMapper studentClassMapper,
                         EquipmentService equipmentService,
                         CurrentUser currentUser) {
        this.equipmentMapper = equipmentMapper;
        this.borrowMapper = borrowMapper;
        this.labUsageLogMapper = labUsageLogMapper;
        this.studentClassMapper = studentClassMapper;
        this.equipmentService = equipmentService;
        this.currentUser = currentUser;
    }

    @Transactional
    public BorrowVO apply(Long equipmentId, int num) {
        if (num <= 0) {
            throw new BusinessException("借用数量必须大于0");
        }
        UserPrincipal principal = currentUser.require();
        if (principal.role() != Role.STUDENT || principal.stuId() == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅学生可以申请借用器材");
        }
        StudentClass studentClass = studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getStuId, principal.stuId())
                .orderByDesc(StudentClass::getJoinedAt)
                .last("limit 1"));
        if (studentClass == null) {
            throw new BusinessException(ErrorCode.CONFLICT, "你尚未加入班级");
        }
        Equipment equipment = equipmentService.require(equipmentId);
        if (equipment.getStatus() == null || equipment.getStatus() != 1) {
            throw new BusinessException(ErrorCode.CONFLICT, "该器材当前不可借用");
        }

        EquipmentBorrow borrow = new EquipmentBorrow();
        borrow.setEquipmentId(equipmentId);
        borrow.setStuId(principal.stuId());
        borrow.setStuName(principal.name());
        borrow.setNum(num);
        borrow.setStatus(BorrowStatus.APPLYING.getCode());
        borrow.setApplyTime(LocalDateTime.now());
        borrowMapper.insert(borrow);
        return toVO(borrowMapper.selectById(borrow.getId()), equipment);
    }

    /**
     * 教师查询；学生只能看到自己的借用记录。
     */
    public List<BorrowVO> list(Integer status, Long stuId, Long equipmentId, UserPrincipal principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        if (principal.role() == Role.STUDENT) {
            stuId = principal.stuId();
        } else if (principal.role() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "没有访问权限");
        }
        return query(status, stuId, equipmentId);
    }

    public List<BorrowVO> mine(Integer status) {
        Long stuId = currentUser.studentId();
        return query(status, stuId, null);
    }

    public List<BorrowVO> listByEquipment(Long equipmentId) {
        equipmentService.require(equipmentId);
        return query(null, null, equipmentId);
    }

    @Transactional
    public BorrowVO approve(Long id) {
        UserPrincipal teacher = requireTeacher();
        EquipmentBorrow borrow = require(id);
        requireStatus(borrow, BorrowStatus.APPLYING, "只有申请中的记录可以审批");

        int rows = borrowMapper.update(null, new UpdateWrapper<EquipmentBorrow>()
                .eq("id", id)
                .eq("status", BorrowStatus.APPLYING.getCode())
                .set("status", BorrowStatus.BORROWED.getCode())
                .set("approve_time", LocalDateTime.now())
                .set("operator_id", teacher.userId()));
        if (rows == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "借用状态已被其他操作修改，请刷新后重试");
        }

        // 条件扣减库存：used_number + num <= total_number，失败则整个事务回滚。
        int stockRows = equipmentMapper.update(null, new UpdateWrapper<Equipment>()
                .eq("id", borrow.getEquipmentId())
                .eq("status", 1)
                .setSql("used_number = used_number + " + borrow.getNum())
                .setSql("version = version + 1")
                .apply("used_number + {0} <= total_number", borrow.getNum()));
        if (stockRows == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "可用库存不足，无法审批");
        }

        writeUsage(borrow, "BORROW");
        return toVO(borrowMapper.selectById(id));
    }

    @Transactional
    public BorrowVO reject(Long id, String remark) {
        UserPrincipal teacher = requireTeacher();
        EquipmentBorrow borrow = require(id);
        requireStatus(borrow, BorrowStatus.APPLYING, "只有申请中的记录可以拒绝");

        int rows = borrowMapper.update(null, new UpdateWrapper<EquipmentBorrow>()
                .eq("id", id)
                .eq("status", BorrowStatus.APPLYING.getCode())
                .set("status", BorrowStatus.REJECTED.getCode())
                .set("operator_id", teacher.userId())
                .set(remark != null && !remark.isBlank(), "remark", remark == null ? null : remark.trim()));
        if (rows == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "借用状态已被其他操作修改，请刷新后重试");
        }
        return toVO(borrowMapper.selectById(id));
    }

    @Transactional
    public BorrowVO markReturned(Long id) {
        UserPrincipal teacher = requireTeacher();
        EquipmentBorrow borrow = require(id);
        requireStatus(borrow, BorrowStatus.BORROWED, "只有已借出的记录可以归还");

        int rows = borrowMapper.update(null, new UpdateWrapper<EquipmentBorrow>()
                .eq("id", id)
                .eq("status", BorrowStatus.BORROWED.getCode())
                .set("status", BorrowStatus.RETURNED.getCode())
                .set("return_time", LocalDateTime.now())
                .set("operator_id", teacher.userId()));
        if (rows == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "借用状态已被其他操作修改，请刷新后重试");
        }

        // 归还时避免出现负库存。
        equipmentMapper.update(null, new UpdateWrapper<Equipment>()
                .eq("id", borrow.getEquipmentId())
                .setSql("used_number = CASE WHEN used_number >= " + borrow.getNum()
                        + " THEN used_number - " + borrow.getNum() + " ELSE 0 END")
                .setSql("version = version + 1"));

        writeUsage(borrow, "RETURN");
        return toVO(borrowMapper.selectById(id));
    }

    @Transactional
    public BorrowVO cancel(Long id) {
        Long stuId = currentUser.studentId();
        EquipmentBorrow borrow = require(id);
        if (!Objects.equals(borrow.getStuId(), stuId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能取消自己的借用申请");
        }
        requireStatus(borrow, BorrowStatus.APPLYING, "只有申请中的记录可以取消");

        int rows = borrowMapper.update(null, new UpdateWrapper<EquipmentBorrow>()
                .eq("id", id)
                .eq("status", BorrowStatus.APPLYING.getCode())
                .set("status", BorrowStatus.CANCELLED.getCode()));
        if (rows == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "借用状态已被其他操作修改，请刷新后重试");
        }
        return toVO(borrowMapper.selectById(id));
    }

    private List<BorrowVO> query(Integer status, Long stuId, Long equipmentId) {
        LambdaQueryWrapper<EquipmentBorrow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, EquipmentBorrow::getStatus, status)
                .eq(stuId != null, EquipmentBorrow::getStuId, stuId)
                .eq(equipmentId != null, EquipmentBorrow::getEquipmentId, equipmentId)
                .orderByDesc(EquipmentBorrow::getApplyTime)
                .orderByDesc(EquipmentBorrow::getId);
        List<EquipmentBorrow> borrows = borrowMapper.selectList(wrapper);
        if (borrows.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> equipmentIds = borrows.stream().map(EquipmentBorrow::getEquipmentId).distinct().toList();
        Map<Long, Equipment> equipmentMap = equipmentMapper.selectBatchIds(equipmentIds).stream()
                .collect(Collectors.toMap(Equipment::getId, Function.identity()));
        return borrows.stream()
                .map(borrow -> toVO(borrow, equipmentMap.get(borrow.getEquipmentId())))
                .toList();
    }

    private UserPrincipal requireTeacher() {
        UserPrincipal principal = currentUser.require();
        if (principal.role() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅教师可执行该操作");
        }
        return principal;
    }

    private EquipmentBorrow require(Long id) {
        EquipmentBorrow borrow = borrowMapper.selectById(id);
        if (borrow == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "借用记录不存在");
        }
        return borrow;
    }

    private void requireStatus(EquipmentBorrow borrow, BorrowStatus expected, String message) {
        if (borrow.getStatus() == null || borrow.getStatus() != expected.getCode()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, message);
        }
    }

    private void writeUsage(EquipmentBorrow borrow, String eventType) {
        LabUsageLog usageLog = new LabUsageLog();
        usageLog.setStuId(borrow.getStuId());
        usageLog.setClassId(latestClassId(borrow.getStuId()));
        usageLog.setBorrowId(borrow.getId());
        usageLog.setEventType(eventType);
        usageLog.setOccurredAt(LocalDateTime.now());
        labUsageLogMapper.insert(usageLog);
    }

    private Long latestClassId(Long stuId) {
        StudentClass studentClass = studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getStuId, stuId)
                .orderByDesc(StudentClass::getJoinedAt)
                .last("limit 1"));
        return studentClass == null ? null : studentClass.getClassId();
    }

    private BorrowVO toVO(EquipmentBorrow borrow, Equipment equipment) {
        int available = 0;
        if (equipment != null) {
            int total = equipment.getTotalNumber() == null ? 0 : equipment.getTotalNumber();
            int used = equipment.getUsedNumber() == null ? 0 : equipment.getUsedNumber();
            available = Math.max(total - used, 0);
        }
        BorrowStatus status = safeStatus(borrow.getStatus());
        return BorrowVO.builder()
                .id(borrow.getId())
                .equipmentId(borrow.getEquipmentId())
                .equipmentName(equipment == null ? null : equipment.getName())
                .stuId(borrow.getStuId())
                .stuName(borrow.getStuName())
                .num(borrow.getNum())
                .status(borrow.getStatus())
                .statusName(status == null ? null : status.getLabel())
                .applyTime(borrow.getApplyTime())
                .approveTime(borrow.getApproveTime())
                .returnTime(borrow.getReturnTime())
                .availableNumber(available)
                .operatorId(borrow.getOperatorId())
                .remark(borrow.getRemark())
                .build();
    }

    private BorrowVO toVO(EquipmentBorrow borrow) {
        Equipment equipment = borrow == null ? null : equipmentMapper.selectById(borrow.getEquipmentId());
        return toVO(borrow, equipment);
    }

    private BorrowStatus safeStatus(Integer code) {
        if (code == null) {
            return null;
        }
        try {
            return BorrowStatus.fromCode(code);
        } catch (BusinessException e) {
            return null;
        }
    }
}
