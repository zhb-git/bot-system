package com.bot_system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @className: AdminController
 * @author: Java之父
 * @date: 2025/10/5 19:10
 * @version: 1.0.0
 * @description: 管理员接口
 */
@Tag(name = "管理员接口")
@RestController
@SaCheckLogin
@RequestMapping("/admin")
public class AdminController {
}
