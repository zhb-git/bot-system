# 🤖 Bot-System 快速开发框架

> 一个基于 **Spring Boot 3 + TelegramBots 9.1.0** 打造的轻量化机器人快速开发框架，支持 **Webhook** 与 **长轮询** 双模式启动，内置统一异常体系、并发锁、日志管理、模块化结构，专为企业级机器人与自动化系统开发而设计。

---

## 🚀 一、项目简介

**Bot-System** 致力于让 Telegram 机器人开发像写 Spring Controller 一样自然。  
通过对 `TelegramBots` SDK 的封装，它实现了自动化的消息分发、异常回调、WebHook 注册与长轮询注册机制。

该框架已实现：
- ✅ Webhook 与 LongPolling 双模式自动切换；
- ✅ 异步消息消费与异常反馈；
- ✅ 统一业务异常体系；
- ✅ 模块化的分层结构；
- ✅ 一键启动，无需手动注册路径。

---

## 🧱 二、技术栈

| 技术组件 | 版本 | 说明 |
|-----------|------|------|
| **Java** | 17 | 项目语言 |
| **Spring Boot** | 3.2.5 | 主框架 |
| **TelegramBots** | 9.1.0 | Telegram 机器人 SDK |
| **MyBatis-Plus** | 3.5.12 | ORM 框架 |
| **Hutool** | 5.8.35 | 工具类库 |
| **Guava** | 32.1.2-jre | 并发与缓存工具 |
| **Sa-Token** | 1.41.0 | 权限认证体系 |
| **Fastjson2** | 2.0.57 | 高性能 JSON 序列化 |
| **Knife4j** | 4.5.0 | Swagger 文档增强 |
| **Lombok** | — | 简化代码开发 |
| **MySQL** | — | 数据存储层 |

---

## 📁 三、项目结构

bot-system/
├── logs/ # 运行日志目录
├── sql/ # 数据库脚本文件
├── src/
│ └── main/
│ ├── java/com/bot/system/
│ │ ├── annotation/ # 自定义注解模块
│ │ ├── bot/ # 机器人主逻辑模块
│ │ │ ├── dispatch/ # 消息分发调度器
│ │ │ ├── handler/ # 各类型消息处理器（Message/Callback/Inline）
│ │ │ ├── mode/ # 启动模式（Webhook / LongPolling）
│ │ │ └── BotApplication.java # 机器人启动入口
│ │ ├── common/ # 公共工具层
│ │ │ ├── constant/ # 常量定义
│ │ │ ├── core/ # 核心类封装（Bot、ErrorProcessor等）
│ │ │ ├── lock/ # 本地锁与并发控制
│ │ │ └── utils/ # 通用工具集（时间、请求、JSON、日志）
│ │ ├── config/ # Spring 配置类（SystemConfig、TelegramBotConfig）
│ │ ├── controller/ # 控制层（REST接口）
│ │ │ ├── admin/ # 管理后台接口
│ │ │ └── global/ # 全局通用接口
│ │ ├── exception/ # 异常定义与统一捕获
│ │ ├── job/ # 定时任务模块
│ │ ├── mapper/ # MyBatis 映射接口
│ │ ├── model/ # 数据模型
│ │ │ ├── entity/ # 实体类（数据库表映射）
│ │ │ ├── pojo/ # 业务POJO对象
│ │ │ ├── request/ # 请求参数模型
│ │ │ └── response/ # 响应结果模型
│ │ └── service/ # 业务逻辑层
│ │ └── BotSystemApplication.java # 主启动类
│ ├── resources/
│ │ ├── static/ # 静态资源目录
│ │ ├── test/ # 测试配置
│ │ └── application.yml # 主配置文件
├── pom.xml # Maven 配置文件
└── README.md # 项目说明文档
