package com.zjgs.equip.auth;

import com.zjgs.equip.common.BusinessException;
import com.zjgs.equip.common.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public UserPrincipal require() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        return principal;
    }

    public UserPrincipal get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal;
        }
        return null;
    }

    public Long teacherId() {
        UserPrincipal principal = require();
        if (principal.role() != com.zjgs.equip.common.Role.TEACHER) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "仅教师可执行该操作");
        }
        return principal.userId();
    }

    public Long studentId() {
        UserPrincipal principal = require();
        if (principal.stuId() == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "当前账号不是学生");
        }
        return principal.stuId();
    }
}
