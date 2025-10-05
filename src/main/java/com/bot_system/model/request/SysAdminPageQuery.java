package com.bot_system.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @className: SysAdminPageQuery
 * @author: Java之父
 * @date: 2025/10/5 19:16
 * @version: 1.0.0
 * @description: 管理员分页参数
 */
@Schema(description = "管理员分页参数")
@Data
public class SysAdminPageQuery {
    @Schema(description = "页码", example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码必须>=1")
    private Integer pageNum;
    @Schema(description = "数量", example = "10")
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须>=1")
    private Integer pageSize;
    @Schema(description = "编号")
    private Long id;
    @Schema(description = "账号（模糊）")
    private String account;
    @Schema(description = "备注（模糊）")
    private String remark;
}
