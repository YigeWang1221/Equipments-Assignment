package com.zjgs.equip.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private Long id;
    private String username;
    private String name;
    private String role;
    private String phone;
    private Long stuId;
    private Long classId;
    private String className;
    private Long groupId;
    private String groupName;
}
