package com.bot_system.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器
     *
     * @param registry 注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截后台管理接口，走 Sa-Token 登录校验
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/admin/**");
    }

    /**
     * 资源映射
     *
     * @param registry 注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置系统静态资源路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/static/");
    }

    /**
     * 跨域配置
     *
     * @param registry 注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(168000);
    }

    /**
     * tomcat配置
     * 自动根据系统硬件配置tomcat并发数
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            // 检测 CPU 核心数
            int cpuCores = Runtime.getRuntime().availableProcessors();

            int maxThreads;
            int acceptCount;
            int maxConnections;
            int connectionTimeout = 60000; // 客户端无响应最大等待时间(ms)

            // 平台线程模式：根据 CPU 核心数动态分配
            maxThreads = cpuCores * 50; // 最大工作线程数（每请求对应一个线程）
            acceptCount = cpuCores * 20; // 排队请求数
            maxConnections = cpuCores * 100; // 并发连接数
            log.info(
                    "[Tomcat配置] 平台线程模式: cpuCores={}, maxThreads={}, acceptCount={}, maxConnections={}",
                    cpuCores, maxThreads, acceptCount, maxConnections
            );

            // 获取 Tomcat 协议处理器
            ProtocolHandler handler = connector.getProtocolHandler();
            if (handler instanceof AbstractHttp11Protocol<?> protocol) {
                // 应用配置
                protocol.setMaxThreads(maxThreads);
                protocol.setAcceptCount(acceptCount);
                protocol.setMaxConnections(maxConnections);
                protocol.setConnectionTimeout(connectionTimeout);
            }
        });
    }
}
