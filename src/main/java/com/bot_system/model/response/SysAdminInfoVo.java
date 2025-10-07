package com.bot_system.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @className: SysAdminInfoVo
 * @author: Java之父
 * @date: 2025/10/7 17:12
 * @version: 1.0.0
 * @description: 管理员信息
 */
@Schema(description = "管理员信息")
@Data
public class SysAdminInfoVo {
    @Schema(description = "名字")
    private String realName;
    @Schema(description = "角色")
    private List<String> roles;
}
