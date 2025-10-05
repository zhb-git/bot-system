package com.bot_system.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @className: SysAdminLoginQuery
 * @author: Java之父
 * @date: 2025/10/3 18:51
 * @version: 1.0.0
 * @description: 登录参数
 */
@Data
@Schema(description = "登录参数")
public class SysAdminLoginQuery {
    @Schema(description = "账号")
    @NotBlank(message = "请填写账号")
    private String account;
    @Schema(description = "密码")
    @NotBlank(message = "请填写密码")
    private String password;
}
