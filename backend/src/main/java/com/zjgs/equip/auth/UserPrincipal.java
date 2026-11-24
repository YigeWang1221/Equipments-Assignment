package com.zjgs.equip.auth;

import com.zjgs.equip.common.Role;

public record UserPrincipal(
        Long userId,
        String username,
        String name,
        Role role,
        Long stuId
) {
}
