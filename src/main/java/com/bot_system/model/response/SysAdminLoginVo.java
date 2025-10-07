package com.bot_system.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @className: SysAdminLoginVo
 * @author: Java之父
 * @date: 2025/10/7 17:10
 * @version: 1.0.0
 * @description: 管理员登录结果
 */
@Schema(description = "登录结果")
@Data
public class SysAdminLoginVo {
    @Schema(description = "token凭证")
    private String accessToken;
}
