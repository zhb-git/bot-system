# 🤖 Bot-System 快速开发框架

> 一个基于 **Spring Boot 3 + TelegramBots 9.1.0** 打造的轻量化机器人快速开发框架，支持 **Webhook** 与 **长轮询** 双模式启动，内置统一异常体系、并发锁、日志管理、模块化结构与高度封装的 Bot 工具类，专为企业级机器人与自动化系统开发而设计。

---

## 🚀 一、项目简介

**Bot-System** 致力于让 Telegram 机器人开发像写 Spring Controller 一样自然。  
通过对 `TelegramBots` SDK 的深度封装，实现了：

- ✅ Webhook 与 LongPolling 双模式自动切换；
- ✅ 异步消息消费与异常反馈；
- ✅ 统一业务异常体系（内置 `BotErrorProcessor`）；
- ✅ 模块化的分层结构与可扩展封装；
- ✅ 高度抽象的 `Bot` 操作类，内置百余个便捷方法；
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

```
bot-system/
├── logs/                         # 运行日志目录
├── sql/                          # 数据库脚本文件
├── src/
│   └── main/
│       ├── java/com/bot/system/
│       │   ├── annotation/       # 自定义注解模块
│       │   ├── bot/              # 机器人主逻辑模块
│       │   │   ├── dispatch/     # 消息分发调度器
│       │   │   ├── handler/      # 各类型消息处理器 (Message/Callback/Inline)
│       │   │   ├── mode/         # 启动模式 (Webhook / LongPolling)
│       │   │   └── BotApplication.java  # 机器人启动入口
│       │   ├── common/           # 公共工具层
│       │   │   ├── constant/     # 常量定义
│       │   │   ├── core/         # 核心类封装 (Bot、ErrorProcessor等)
│       │   │   ├── lock/         # 本地锁与并发控制
│       │   │   └── utils/        # 通用工具集 (时间、请求、JSON、日志)
│       │   ├── config/           # Spring 配置类 (SystemConfig, TelegramBotConfig)
│       │   ├── controller/       # 控制层 (REST接口)
│       │   │   ├── admin/        # 管理后台接口
│       │   │   └── global/       # 全局通用接口
│       │   ├── exception/        # 异常定义与统一捕获
│       │   ├── job/              # 定时任务模块
│       │   ├── mapper/           # MyBatis 映射接口
│       │   ├── model/            # 数据模型
│       │   │   ├── entity/       # 实体类 (数据库表映射)
│       │   │   ├── pojo/         # 业务POJO对象
│       │   │   ├── request/      # 请求参数模型
│       │   │   └── response/     # 响应结果模型
│       │   └── service/          # 业务逻辑层
│       │       └── BotSystemApplication.java  # 主启动类
│       ├── resources/
│       │   ├── static/           # 静态资源目录
│       │   ├── test/             # 测试配置
│       │   └── application.yml   # 主配置文件
├── pom.xml                       # Maven 配置文件
└── README.md                     # 项目说明文档
```

---

## ⚙️ 四、核心模块说明

### 🧠 BotApplication
机器人启动入口，自动根据配置启动 `Webhook` 或 `LongPolling` 模式。
- Webhook 模式自动注册 `/webhook/{uuid}`；
- 启动失败自动降级为长轮询；
- 启动时自动清空旧消息队列。

### 🧭 BotUpdateDispatch
统一分发 Telegram 更新事件：
- `Message` → `BotMessageHandler`
- `CallbackQuery` → `BotCallbackHandler`
- `InlineQuery` → `BotInlineHandler`

### 💬 BotMessageHandler
文本消息处理模块。  
示例中当消息为 `1/2/3` 时会触发不同类型异常，用于演示异常处理链路。

### 🛰️ BotWebhook
Webhook 模式入口点。  
接收 Telegram 服务器推送的更新，分发至对应处理器，并自动返回异常反馈。

### 🕹️ BotLongPoll
长轮询模式消费者。  
基于 `LongPollingSingleThreadUpdateConsumer` 实现异步消费，并封装异常反馈逻辑。

### ⚡ BotErrorProcessor
统一异常反馈处理器。
- 根据 `Update` 类型自动生成反馈结构（`SendMessage` / `AnswerCallbackQuery` / `AnswerInlineQuery`）。
- 支持注解 `@BotFeedback` 标识可反馈异常。
- 未标注的异常自动替换为通用提示。
- 自动识别群组上下文，支持回复原消息。

### ✅ RedisAction
redis操作

---

## 🧩 五、Bot 通用封装说明

`Bot` 是整个框架的核心操作类，封装了 Telegram SDK 的全部常用方法，  
通过面向对象的方式提供简洁、统一的调用体验。

### ✨ 功能分类

| 分类 | 功能 | 示例方法 |
|------|------|----------|
| **消息发送** | 文本、图片、视频、语音、GIF、文档、贴纸、表情 | `sendText()`, `sendPhoto()`, `sendVideo()` |
| **消息编辑** | 编辑消息文字、媒体、标记等 | `editText()`, `editMarkup()` |
| **消息管理** | 删除、置顶、撤回、转发 | `deleteMessage()`, `pinMessage()`, `forwardMessage()` |
| **群组操作** | 禁言、解除禁言、踢人、设置管理员 | `banUser()`, `unbanUser()`, `promoteAdmin()` |
| **菜单与命令** | 动态注册指令、清除菜单 | `setCommand()`, `clearCommands()` |
| **文件与资源** | 下载文件、发送媒体流 | `sendDocument()`, `sendAnimation()` |
| **用户反馈** | 私聊提示、群组引用回复、弹窗 | `replyText()`, `toast()` |
| **高级控制** | 异步执行、延迟消息、批量群发 | `asyncSend()`, `delaySend()` |

### 💡 使用示例

````
// 发送消息
bot.sendText(chatId, "Hello Telegram!");

// 回复用户并引用原消息
bot.replyText(chatId, "命令执行成功 ✅", replyMessageId);

// 发送带按钮的消息
bot.sendMarkup(chatId, "请选择操作", keyboardMarkup);

// 删除消息
bot.deleteMessage(chatId, messageId);

// 禁言用户
bot.banUser(groupId, userId);
````

### 🧰 设计理念
- **高内聚低耦合**：每个方法只封装一个 Telegram 行为。
- **链式友好调用**：部分方法支持链式使用，逻辑清晰。
- **异常安全封装**：内部全部捕获 `TelegramApiException` 并抛出业务异常。
- **支持异步发送**：结合线程池异步执行，适合高并发场景。
- **可快速扩展**：新增功能只需在 Bot 类中添加方法即可被所有模块复用。

---

## 🧨 六、异常与响应体系

| 异常类 | 说明 |
|--------|------|
| `BizException` | 业务逻辑异常（可反馈） |
| `BotActionException` | 机器人执行异常（可反馈） |
| `BotException` | 启动或系统级异常 |
| `GlobalExceptionHandler` | 全局异常捕获器 |

所有异常均通过 `BotErrorProcessor` 生成标准反馈，自动返回给用户。

---

## 🪵 七、日志体系

- 使用 **SLF4J + Logback**；
- 日志输出目录：`./logs/`；
- 控制台与文件双输出；
- 自动分割与滚动策略；
- 推荐日志级别：
  - **INFO**：常规日志；
  - **WARN**：潜在风险；
  - **ERROR**：业务/系统异常。

---

## 🧰 八、运行方式

### 本地运行

```bash
mvn clean package
mvn spring-boot:run
```

或使用 IDE 运行：
```
com.bot.system.service.BotSystemApplication
```

### Webhook 模式配置
在 `application.yml` 中：

```yaml
telegram:
  token: YOUR_BOT_TOKEN
  model: WEBHOOK
system:
  domain: https://your.domain.com
```

### 长轮询模式配置
```yaml
telegram:
  token: YOUR_BOT_TOKEN
  model: LONG_POLL
```

---

## 🧩 九、开发规范

- Controller 仅作请求转发；
- Service 层负责业务逻辑；
- 异常统一通过 `Response` 封装；
- 禁止使用 `System.out.println`；
- 公共常量与枚举放入 `common.constant`；
- 所有 Bot 相关逻辑位于 `bot.handler` 下；
- 封装方法命名需语义化（如 sendText / editMessage / banUser）。

---

## 🔄 十、可扩展方向

- [ ] Redis 支持（缓存 + 分布式锁）
- [ ] 多机器人集群管理
- [ ] 消息持久化与重放机制
- [ ] WebSocket 实时推送
- [ ] 前端管理面板（Vue + Vben Admin）
- [ ] 云函数部署支持 (Serverless)

---

## 📜 License

本项目仅供学习与内部使用，禁止未经授权的商用或传播。

---

🧡 **Developed by Java之父**  
_“让机器人开发像写 Controller 一样优雅。”_
