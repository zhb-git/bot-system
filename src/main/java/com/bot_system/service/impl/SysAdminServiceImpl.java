package com.bot_system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bot_system.exception.BizException;
import com.bot_system.mapper.SysAdminMapper;
import com.bot_system.model.entity.SysAdmin;
import com.bot_system.model.request.CreateSysAdminQuery;
import com.bot_system.model.request.SysAdminLoginQuery;
import com.bot_system.model.request.SysAdminPageQuery;
import com.bot_system.model.request.UpdateSysAdminQuery;
import com.bot_system.service.ISysAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * @className: SysAdminServiceImpl
 * @author: Java之父
 * @date: 2025/10/5 19:04
 * @version: 1.0.0
 * @description: 管理员业务实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class SysAdminServiceImpl extends ServiceImpl<SysAdminMapper, SysAdmin> implements ISysAdminService {
    @Override
    public void create(CreateSysAdminQuery query) {
        SysAdmin sysAdmin = new SysAdmin();
        BeanUtils.copyProperties(query, sysAdmin);
        if (baseMapper.insert(sysAdmin) != 1) {
            throw new BizException("添加管理员失败");
        }
    }

    @Override
    public void deleteById(Long id) {
        if (baseMapper.deleteById(id) != 1) {
            throw new BizException("删除管理员失败");
        }
    }

    @Override
    public void update(UpdateSysAdminQuery query) {
        LambdaUpdateWrapper<SysAdmin> wrapper = new LambdaUpdateWrapper<>();
        if (StringUtils.hasText(query.getRole())) {
            wrapper.set(SysAdmin::getRole, query.getRole());
        }
        if (StringUtils.hasText(query.getRemark())) {
            wrapper.set(SysAdmin::getRemark, query.getRemark());
        }
        wrapper.eq(SysAdmin::getId, query.getId());
        if (wrapper.getSqlSet() != null) {
            // 设置更新时间
            wrapper.set(SysAdmin::getUpdateTime, LocalDateTime.now());
            if (baseMapper.update(wrapper) != 1) {
                throw new BizException("更改管理员信息失败");
            }
        }
    }

    @Override
    public IPage<SysAdmin> getPage(SysAdminPageQuery query) {
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        if (query.getId() != null) {
            wrapper.eq(SysAdmin::getId, query.getId());
        }
        if (StringUtils.hasText(query.getAccount())) {
            wrapper.like(SysAdmin::getRole, query.getAccount());
        }
        if (StringUtils.hasText(query.getRemark())) {
            wrapper.like(SysAdmin::getRemark, query.getRemark());
        }
        return baseMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
    }

    @Override
    public void login(SysAdminLoginQuery query) {
        LambdaQueryWrapper<SysAdmin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysAdmin::getAccount, query.getAccount())
                .eq(SysAdmin::getPassword, query.getPassword());
        SysAdmin sysAdmin = baseMapper.selectOne(wrapper);
        if (sysAdmin == null) {
            throw new BizException("账号或密码错误");
        }
        // sa-token 登录（同一线程）
        StpUtil.login(sysAdmin.getId());
    }
}
