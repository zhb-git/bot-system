package com.bot_system.model.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: R
 * @author: Java之父
 * @date: 2025/8/2 15:35
 * @version: 1.0.0
 * @description: 统一响应
 */
@Data
public class R<T> implements Serializable {
    /**
     * 错误码
     */
    @Schema(name = "code", description = "错误码,当code为200时返回正常", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    private Integer code;

    /**
     * 错误提示信息
     */
    @Schema(name = "message", description = "错误提示信息,当code为非200时返回提示信息", requiredMode = Schema.RequiredMode.REQUIRED, example = "操作成功")
    private String message;

    /**
     * 附加返回数据
     */
    @Schema(name = "data", description = "附加返回数据,当code为200时返回数据")
    private T data;

    /**
     * 给ObjectMapper用的，代码中不要调用
     */
    public R() {

    }

    public static <T> R<T> success() {
        R<T> r = new R<>();
        r.code = 200;
        r.message = "操作成功";
        return r;
    }

    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.code = 200;
        r.message = "操作成功";
        r.data = data;
        return r;
    }

    /**
     * 将 MyBatis-Plus 分页结果 IPage<E> 转换为 PageVO<V>
     *
     * @param page   原始分页对象（Entity）
     * @param voClass VO 类型
     * @param <E>    原始实体类型
     * @param <V>    目标 VO 类型
     * @return R<RespPage<V>> 封装后的分页结果
     */
    public static <E, V> R<RespPage<V>> success(IPage<E> page, Class<V> voClass) {
        List<V> voList = page.getRecords().stream().map(entity -> {
            try {
                V vo = voClass.getDeclaredConstructor().newInstance(); // JDK 8+
                BeanUtils.copyProperties(entity, vo);
                return vo;
            } catch (Exception e) {
                throw new RuntimeException("VO实例创建失败: " + voClass.getName(), e);
            }
        }).collect(Collectors.toList());

        RespPage<V> voRespPage = new RespPage<>();
        voRespPage.setRecords(voList);
        // 排除records
        BeanUtils.copyProperties(page, voRespPage, "records");
        return success(voRespPage);
    }

    public static <E> R<RespPage<E>> success(IPage<E> page) {
        RespPage<E> voRespPage = new RespPage<>();
        voRespPage.setRecords(page.getRecords());
        // 排除records
        BeanUtils.copyProperties(page, voRespPage, "records");
        return success(voRespPage);
    }

    public static R<String> fail() {
        R<String> r = new R<>();
        r.code = 999;
        r.message = "操作失败";
        return r;
    }

    public  static <T> R<T> fail(String message) {
        R<T> r = new R<>();
        r.code = 999;
        r.message = message;
        return r;
    }

    public static <T> R<T> error() {
        R<T> r = new R<>();
        r.code = 500;
        r.message = "系统异常";
        return r;
    }

    public static <T> R<T> error(String message) {
        R<T> r = new R<>();
        r.code = 500;
        r.message = message;
        return r;
    }

    public static <T> R<T> notLogin() {
        R<T> r = new R<>();
        r.code = 401;
        r.message = "未登录";
        return r;
    }
}
