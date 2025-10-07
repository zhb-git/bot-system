package com.bot_system;

import com.bot_system.bot.BotApplication;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@Slf4j
@MapperScan("com.bot_system.mapper")
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class BotSystemApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(BotSystemApplication.class, args);
    }

    @Resource
    private BotApplication botApplication;

    @Resource
    private Environment environment;

    @Value("${spring.profiles.active}")
    private String active;

    @Value("${server.port}")
    private int port;

    @Override
    public void run(ApplicationArguments args) {
        // 初始化文件
        initFile();
        // 启动机器人
        botApplication.start();
        // 启动完成
        if (active.equals("dev")) {
            String property = environment.getProperty("local.server.port");
            int port = property == null ? this.port : Integer.parseInt(property);
            System.out.println("---------------------------------------------------------");
            System.out.println("- 当前环境为本地, 接口文档地址: http://localhost:" + port + "/doc.html");
            System.out.println("---------------------------------------------------------");
        }
    }

    private void initFile() {
        // 根目录是否有static
        File dir = new File("./static");
        if (!dir.exists()) {
            if (dir.mkdir()) {
                log.info("static文件创建成功");
            } else {
                log.error("static文件创建失败");
            }
        }
    }
}
