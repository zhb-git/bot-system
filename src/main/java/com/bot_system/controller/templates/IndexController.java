package com.bot_system.controller.templates;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @className: IndexController
 * @author: Java之父
 * @date: 2025/10/9 21:10
 * @version: 1.0.0
 * @description: 模板控制器
 */
@Controller
public class IndexController {
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "欢迎来到 Thymeleaf 示例");
        model.addAttribute("message", "Hello, Spring Boot + Thymeleaf!");
        // 返回 templates/index.html
        return "index";
    }
}
