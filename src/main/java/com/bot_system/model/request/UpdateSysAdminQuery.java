package com.bot_system.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @className: UpdateSysAdminQuery
 * @author: Java之父
 * @date: 2025/10/5 19:15
 * @version: 1.0.0
 * @description: 更改管理员参数
 */
@Schema(description = "更改管理员参数")
@Data
public class UpdateSysAdminQuery {
    @Schema(description = "编号")
    @NotNull(message = "请填写编号")
    private Long id;
    @Schema(description = "角色")
    private List<String> roles;
    @Schema(description = "备注")
    private String remark;
}
