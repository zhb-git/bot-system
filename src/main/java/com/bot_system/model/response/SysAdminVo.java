package com.bot_system.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @className: SysAdminVo
 * @author: Java之父
 * @date: 2025/10/3 19:01
 * @version: 1.0.0
 * @description: 管理员信息
 */
@Schema(description = "管理员信息")
@Data
public class SysAdminVo {
    @Schema(description = "编号")
    private Long id;
    @Schema(description = "账号")
    private String account;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "角色")
    private List<String> roles;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
