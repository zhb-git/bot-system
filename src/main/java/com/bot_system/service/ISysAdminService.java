package com.bot_system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bot_system.model.entity.SysAdmin;
import com.bot_system.model.request.CreateSysAdminQuery;
import com.bot_system.model.request.SysAdminLoginQuery;
import com.bot_system.model.request.SysAdminPageQuery;
import com.bot_system.model.request.UpdateSysAdminQuery;

/**
 * @className: ISysAdminService
 * @author: Java之父
 * @date: 2025/10/5 19:04
 * @version: 1.0.0
 * @description: 管理员业务类
 */
public interface ISysAdminService extends IService<SysAdmin> {
    void create(CreateSysAdminQuery query);

    void deleteById(Long id);

    void update(UpdateSysAdminQuery query);

    IPage<SysAdmin> getPage(SysAdminPageQuery query);

    void login(SysAdminLoginQuery query);
}
