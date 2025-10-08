package com.bot_system.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @className: SysAdmin
 * @author: Java之父
 * @date: 2025/10/5 18:53
 * @version: 1.0.0
 * @description: 管理员
 */
@Data
// 若有字段需要json映射得开启autoResultMap = true，且标明@TableField(typeHandler = JacksonTypeHandler.class)注解
@TableName(value = "sys_admin", autoResultMap = true)
@EqualsAndHashCode(callSuper = true)
public class SysAdmin extends BaseEntity {
    @TableField("admin_account")
    private String account;
    @TableField("admin_password")
    private String password;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> roles;
    private String remark;
}
