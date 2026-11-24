package com.zjgs.equip.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjgs.equip.auth.dto.LoginRequest;
import com.zjgs.equip.auth.dto.LoginResponse;
import com.zjgs.equip.auth.dto.RegisterRequest;
import com.zjgs.equip.auth.dto.UserInfo;
import com.zjgs.equip.auth.entity.LoginLog;
import com.zjgs.equip.auth.mapper.LoginLogMapper;
import com.zjgs.equip.clazz.entity.ClassGroup;
import com.zjgs.equip.clazz.entity.StudentClass;
import com.zjgs.equip.clazz.mapper.ClassGroupMapper;
import com.zjgs.equip.clazz.mapper.StudentClassMapper;
import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import com.zjgs.equip.common.Role;
import com.zjgs.equip.user.entity.Guest;
import com.zjgs.equip.user.entity.Student;
import com.zjgs.equip.user.entity.Teacher;
import com.zjgs.equip.user.mapper.GuestMapper;
import com.zjgs.equip.user.mapper.StudentMapper;
import com.zjgs.equip.user.mapper.TeacherMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    private final GuestMapper guestMapper;
    private final StudentClassMapper studentClassMapper;
    private final ClassGroupMapper classGroupMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenStore tokenStore;
    private final LoginLogMapper loginLogMapper;

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String username = request.getUsername().trim();
        String password = request.getPassword();

        Teacher teacher = teacherMapper.selectOne(new LambdaQueryWrapper<Teacher>()
                .eq(Teacher::getUsername, username).last("limit 1"));
        if (teacher != null) {
            if (teacher.getStatus() != null && teacher.getStatus() == 0) {
                throw new BusinessException("教师账号已被禁用");
            }
            if (!passwordEncoder.matches(password, teacher.getPassword())) {
                log(username, Role.TEACHER, teacher.getId(), false, "密码错误", httpRequest);
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
            }
            UserPrincipal principal = new UserPrincipal(teacher.getId(), teacher.getUsername(), teacher.getName(),
                    Role.TEACHER, null);
            log(username, Role.TEACHER, teacher.getId(), true, "登录成功", httpRequest);
            return issue(principal, UserInfo.builder()
                    .id(teacher.getId())
                    .username(teacher.getUsername())
                    .name(teacher.getName())
                    .phone(teacher.getPhone())
                    .role(Role.TEACHER.name())
                    .build());
        }

        Student student = null;
        if (username.matches("\\d{10}")) {
            student = studentMapper.selectById(Long.valueOf(username));
        }
        if (student != null) {
            if (student.getStatus() != null && student.getStatus() == 0) {
                throw new BusinessException("学生账号已被禁用");
            }
            if (!passwordEncoder.matches(password, student.getPassword())) {
                log(username, Role.STUDENT, student.getStuId(), false, "密码错误", httpRequest);
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "学号或密码错误");
            }
            StudentClass sc = latestStudentClass(student.getStuId());
            ClassGroup group = sc != null && sc.getGroupId() != null
                    ? classGroupMapper.selectById(sc.getGroupId()) : null;
            UserPrincipal principal = new UserPrincipal(student.getStuId(), String.valueOf(student.getStuId()),
                    student.getStuName(), Role.STUDENT, student.getStuId());
            log(username, Role.STUDENT, student.getStuId(), true, "登录成功", httpRequest);
            return issue(principal, UserInfo.builder()
                    .id(student.getStuId())
                    .stuId(student.getStuId())
                    .username(String.valueOf(student.getStuId()))
                    .name(student.getStuName())
                    .phone(student.getPhone())
                    .role(Role.STUDENT.name())
                    .classId(sc == null ? null : sc.getClassId())
                    .groupId(sc == null ? null : sc.getGroupId())
                    .groupName(group == null ? null : group.getGroupName())
                    .build());
        }

        // 访客：手机号登录，首次登录自动创建访客账号
        if (username.matches("\\d{11}")) {
            Guest guest = guestMapper.selectOne(new LambdaQueryWrapper<Guest>()
                    .eq(Guest::getPhone, username).last("limit 1"));
            if (guest == null) {
                guest = new Guest();
                guest.setPhone(username);
                guest.setName("访客" + username.substring(7));
                guest.setCreatedAt(LocalDateTime.now());
                guestMapper.insert(guest);
            }
            UserPrincipal principal = new UserPrincipal(guest.getId(), username, guest.getName(),
                    Role.GUEST, null);
            log(username, Role.GUEST, guest.getId(), true, "访客登录", httpRequest);
            return issue(principal, UserInfo.builder()
                    .id(guest.getId())
                    .username(username)
                    .name(guest.getName())
                    .phone(guest.getPhone())
                    .role(Role.GUEST.name())
                    .build());
        }

        log(username, null, null, false, "账号不存在", httpRequest);
        throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名/学号/手机号不存在");
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        String phone = request.getPhone().trim();
        if (!phone.matches("\\d{11}")) {
            throw new BusinessException("手机号格式不正确");
        }
        Guest guest = guestMapper.selectOne(new LambdaQueryWrapper<Guest>()
                .eq(Guest::getPhone, phone).last("limit 1"));
        if (guest == null) {
            guest = new Guest();
            guest.setPhone(phone);
            guest.setName(request.getName() == null || request.getName().isBlank()
                    ? "访客" + phone.substring(7) : request.getName().trim());
            guest.setCreatedAt(LocalDateTime.now());
            guestMapper.insert(guest);
        }
        UserPrincipal principal = new UserPrincipal(guest.getId(), phone, guest.getName(), Role.GUEST, null);
        return issue(principal, UserInfo.builder()
                .id(guest.getId())
                .username(phone)
                .name(guest.getName())
                .phone(guest.getPhone())
                .role(Role.GUEST.name())
                .build());
    }

    public UserInfo me(UserPrincipal principal) {
        if (principal.role() == Role.TEACHER) {
            Teacher teacher = teacherMapper.selectById(principal.userId());
            if (teacher == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "教师不存在");
            }
            return UserInfo.builder()
                    .id(teacher.getId()).username(teacher.getUsername()).name(teacher.getName())
                    .phone(teacher.getPhone()).role(Role.TEACHER.name()).build();
        }
        if (principal.role() == Role.STUDENT) {
            Student student = studentMapper.selectById(principal.stuId());
            StudentClass sc = latestStudentClass(principal.stuId());
            ClassGroup group = sc != null && sc.getGroupId() != null
                    ? classGroupMapper.selectById(sc.getGroupId()) : null;
            return UserInfo.builder()
                    .id(student.getStuId()).stuId(student.getStuId())
                    .username(String.valueOf(student.getStuId())).name(student.getStuName())
                    .phone(student.getPhone()).role(Role.STUDENT.name())
                    .classId(sc == null ? null : sc.getClassId())
                    .groupId(sc == null ? null : sc.getGroupId())
                    .groupName(group == null ? null : group.getGroupName())
                    .build();
        }
        Guest guest = guestMapper.selectById(principal.userId());
        return UserInfo.builder()
                .id(guest.getId()).username(guest.getPhone()).name(guest.getName())
                .phone(guest.getPhone()).role(Role.GUEST.name()).build();
    }

    public void logout(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        try {
            Claims claims = jwtService.parse(token.replace("Bearer ", ""));
            tokenStore.revoke(claims.getId());
        } catch (Exception ignored) {
        }
    }

    private LoginResponse issue(UserPrincipal principal, UserInfo userInfo) {
        String token = jwtService.createToken(principal);
        Claims claims = jwtService.parse(token);
        tokenStore.store(claims.getId(), principal.userId(), jwtService.getExpirationSeconds());
        return new LoginResponse(token, userInfo);
    }

    private StudentClass latestStudentClass(Long stuId) {
        return studentClassMapper.selectOne(new LambdaQueryWrapper<StudentClass>()
                .eq(StudentClass::getStuId, stuId)
                .orderByDesc(StudentClass::getJoinedAt)
                .last("limit 1"));
    }

    private void log(String username, Role role, Long userId, boolean success, String message,
                     HttpServletRequest request) {
        LoginLog log = new LoginLog();
        log.setUserType(role == null ? "UNKNOWN" : role.name());
        log.setUserId(userId);
        log.setIp(request == null ? null : request.getRemoteAddr());
        log.setSuccess(success ? 1 : 0);
        log.setMessage(message + ":" + username);
        log.setCreatedAt(LocalDateTime.now());
        loginLogMapper.insert(log);
    }
}
