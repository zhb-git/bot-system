package com.bot_system.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bot_system.model.entity.SysAdmin;
import com.bot_system.model.request.CreateSysAdminQuery;
import com.bot_system.model.request.SysAdminLoginQuery;
import com.bot_system.model.request.SysAdminPageQuery;
import com.bot_system.model.request.UpdateSysAdminQuery;
import com.bot_system.model.response.*;
import com.bot_system.service.ISysAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

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
    @Resource
    private ISysAdminService sysAdminService;

    @Operation(
            summary = "登录",
            description = "成功后返回登录结果"
    )
    @SaIgnore
    @PostMapping("/login")
    R<SysAdminLoginVo> login(@RequestBody @Valid SysAdminLoginQuery query) {
        sysAdminService.login(query);
        SysAdminLoginVo vo = new SysAdminLoginVo();
        vo.setAccessToken(StpUtil.getTokenValue());
        return R.success(vo);
    }

    @Operation(
            summary = "退出登录",
            description = "成功后返回200"
    )
    @PostMapping("/logout")
    R<SysAdmin> logout() {
        StpUtil.logout();
        return R.success();
    }

    @Operation(
            summary = "获取管理员信息",
            description = "成功后返回信息"
    )
    @GetMapping("/info")
    R<SysAdminInfoVo> info() {
        Object loginId = StpUtil.getLoginId();
        Long id = Convert.toLong(loginId);
        SysAdmin sysAdmin = sysAdminService.getById(id);
        SysAdminInfoVo vo = new SysAdminInfoVo();
        vo.setRealName(sysAdmin.getAccount());
        vo.setRoles(sysAdmin.getRoles());
        return R.success(vo);
    }

    @Operation(
            summary = "创建管理员",
            description = "成功后返回200"
    )
    @PostMapping("/create")
    R<String> create(@RequestBody @Valid CreateSysAdminQuery query) {
        sysAdminService.create(query);
        return R.success();
    }

    @Operation(
            summary = "删除管理员",
            description = "成功后返回200"
    )
    @PostMapping("/delete")
    R<String> delete(@Parameter(description = "编号", required = true) @RequestParam("id") Long id) {
        sysAdminService.deleteById(id);
        return R.success();
    }

    @Operation(
            summary = "修改管理员",
            description = "成功后返回200"
    )
    @PostMapping("/update")
    R<String> update(@RequestBody @Valid UpdateSysAdminQuery query) {
        sysAdminService.update(query);
        return R.success();
    }

    @Operation(
            summary = "分页查询",
            description = "成功后返回分页结果"
    )
    @GetMapping("/getPage")
    R<RespPage<SysAdminVo>> getPage(@ParameterObject @Valid SysAdminPageQuery query) {
        IPage<SysAdmin> page = sysAdminService.getPage(query);
        return R.success(page, SysAdminVo.class);
    }
}
