package com.bot_system.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @className: CreateSysAdminQuery
 * @author: Java之父
 * @date: 2025/10/5 19:14
 * @version: 1.0.0
 * @description: 创建管理员参数
 */
@Data
@Schema(description = "创建管理员参数")
public class CreateSysAdminQuery {
    @Schema(description = "账号")
    @NotBlank(message = "请填写账号")
    private String account;
    @Schema(description = "密码")
    @NotBlank(message = "请填写密码")
    private String password;
    @Schema(description = "角色")
    @NotBlank(message = "请填写角色")
    private String role;
    @Schema(description = "备注")
    private String remark;
}
