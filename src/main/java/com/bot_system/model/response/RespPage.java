package com.bot_system.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @className: RespPage
 * @author: Java之父
 * @date: 2025/8/8 19:54
 * @version: 1.0.0
 * @description: 分页结果
 */
@Data
public class RespPage<T> {
    @Schema(description = "数据")
    private List<T> records;
    @Schema(description = "总记录数")
    private long total;
    @Schema(description = "每页条数")
    private long size;
    @Schema(description = "当前页码")
    private long pageNum;
    @Schema(description = "总页数")
    private long pageSize;
}
