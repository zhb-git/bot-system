package com.bot_system.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @className: SysAdmin
 * @author: Java之父
 * @date: 2025/10/5 18:53
 * @version: 1.0.0
 * @description: 管理员
 */
@Data
@TableName("sys_admin")
@EqualsAndHashCode(callSuper = true)
public class SysAdmin extends BaseEntity {
    @TableField("admin_account")
    private String account;
    @TableField("admin_password")
    private String password;
    private String role;
    private String remark;
}
