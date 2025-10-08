package com.bot_system.bot;

import com.bot_system.bot.mode.BotLongPoll;
import com.bot_system.bot.mode.BotWebhook;
import com.bot_system.common.constant.BotModel;
import com.bot_system.common.core.Bot;
import com.bot_system.config.SystemConfig;
import com.bot_system.config.TelegramBotConfig;
import com.bot_system.exception.BotException;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @className: BotApplication
 * @author: Java之父
 * @date: 2025/10/5 14:38
 * @version: 1.0.0
 * @description: 机器人应用
 */
@Slf4j
@Component
public class BotApplication {
    @Resource
    private RequestMappingHandlerMapping handlerMapping;

    @Resource
    private SystemConfig systemConfig;

    @Resource
    private TelegramBotConfig telegramBotConfig;

    @Resource
    private BotWebhook botWebhook;

    @Resource
    private BotLongPoll botLongPoll;

    @Resource
    private Bot bot;

    private final TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();

    private final AtomicBoolean running = new AtomicBoolean();

    private int mode;

    public void start() {
        if (running.get()) {
            throw new BotException("机器人运行中");
        }
        // 设置指令
        // setCommand();
        if (telegramBotConfig.getModel() == BotModel.WEBHOOK) {
            // webhook 模式
            try {
                webhook();
            } catch (Exception e) {
                log.error("webhook模式启动失败：{}，改用长轮询模式", e.getMessage());
                longPoll();
            }
        } else if (telegramBotConfig.getModel() == BotModel.LONG_POLL) {
            // 长轮询模式
            longPoll();
        } else {
            throw new BotException("模式不存在");
        }
    }

    private void setCommand() {
        List<BotCommand> commandList = new ArrayList<>();
        commandList.add(BotCommand.builder().command("/start").description("启动机器人").build());
        bot.setCommand(commandList);
    }

    private void webhook() {
        String uuid = UUID.randomUUID().toString();
        String path = "/webhook/" + uuid;
        Method method;
        try {
            method = BotWebhook.class.getMethod("webhook", Update.class);
        } catch (NoSuchMethodException e) {
            throw new BotException("未找到webhook方法", e);
        }
        RequestMappingInfo mapping = RequestMappingInfo
                .paths(path)
                .methods(RequestMethod.POST)
                .build();
        handlerMapping.registerMapping(mapping, botWebhook, method);
        // 机器人设置webhook路径
        String webhookUrl = systemConfig.getDomain() + path;
        bot.setWebhook(webhookUrl);
        // 设置当前模式
        this.mode = BotModel.WEBHOOK;
        log.info("机器人webhook模式启动成功");
    }

    private void longPoll() {
        try {
            // 清空已存在的消息
            bot.clearUpdates();
            // 注册长轮询
            botsApplication.registerBot(telegramBotConfig.getToken(), botLongPoll);
            // 设置当前模式
            this.mode = BotModel.LONG_POLL;
            log.info("机器人长轮询模式启动成功");
        } catch (TelegramApiException e) {
            throw new BotException("长轮询注册失败", e);
        }
    }

    @PreDestroy
    public void destroy() {
        // 删除webhook
        if (this.mode == BotModel.WEBHOOK) {
            bot.delWebhook();
        }
    }
}
