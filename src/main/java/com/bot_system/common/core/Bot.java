package com.bot_system.common.core;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bot_system.common.utils.HttpUtil;
import com.bot_system.exception.BotException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinAllChatMessages;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.media.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * @className: Bot
 * @author: Java之父
 * @date: 2025/10/4 21:53
 * @version: 1.0.0
 * @description: 机器人
 */
@Slf4j
@Getter
public class Bot {
    private final String token;
    private final TelegramClient client;
    private final String name;

    /**
     * 创建机器人实例
     *
     * @param token token凭证
     */
    public Bot(String token) {
        this.token = token;
        this.client = new OkHttpTelegramClient(token);
        this.name = getMe().getUserName();
        log.info("Telegram机器人【@{}】创建客户端成功", this.name);
    }

    /**
     * 获取机器人信息
     *
     * @return 信息
     */
    public User getMe() {
        try {
            // 调用 getMe 获取 bot 的用户名
            return this.client.execute(new GetMe());
        } catch (Exception e) {
            throw new BotException("获取机器人" + this.token + "信息失败", e);
        }
    }

    /**
     * 设置webhook回调
     *
     * @param url 路径
     */
    public void setWebhook(String url) {
        try {
            SetWebhook webhook = SetWebhook.builder()
                    // webhook路径
                    .url(url)
                    // 接收指定类型消息
                    // .allowedUpdates(List.of("message", "callback_query", "inline_query"))
                    // 清空旧消息
                    .dropPendingUpdates(true)
                    .build();
            this.client.execute(webhook);
            log.info("机器人@{}设置webhook成功", this.name);
        } catch (Exception e) {
            throw new BotException("机器人@" + this.name + "设置webhook失败", e);
        }
    }

    /**
     * 删除webhook
     */
    public void delWebhook() {
        try {
            DeleteWebhook webhook = DeleteWebhook.builder().build();
            this.client.execute(webhook);
            log.info("机器人@{}删除webhook成功", this.name);
        } catch (Exception e) {
            throw new BotException("机器人@" + this.name + "删除webhook失败", e);
        }
    }

    /**
     * 设置机器人指令
     *
     * @param commandList 指令
     */
    public void setCommand(List<BotCommand> commandList) {
        SetMyCommands setMyCommands = SetMyCommands.builder()
                .commands(commandList)
                .build();
        try {
            this.client.execute(setMyCommands);
        } catch (TelegramApiException e) {
            throw new BotException("机器人@" + this.name + "指令设置失败", e);
        }
    }

    /**
     * 清空机器人消息
     */
    public void clearUpdates() {
        try {
            String url = "https://api.telegram.org/bot" + this.token + "/getUpdates?timeout=0";
            String response = HttpUtil.get(url);

            JSONObject json = JSON.parseObject(response);
            JSONArray result = json.getJSONArray("result");

            if (result != null && !result.isEmpty()) {
                JSONObject last = result.getJSONObject(result.size() - 1);
                long lastUpdateId = last.getLongValue("update_id");

                String cleanUrl = "https://api.telegram.org/bot" + this.token
                        + "/getUpdates?offset=" + (lastUpdateId + 1);
                HttpUtil.get(cleanUrl);

                log.info("已跳过旧消息，最新 offset = {}", lastUpdateId + 1);
            } else {
                log.info("暂无旧消息，无需跳过旧消息");
            }
        } catch (Exception e) {
            throw new BotException("跳过旧消息失败", e);
        }
    }

    /**
     * 发送消息
     *
     * @param chatId 目标
     * @param text   文本
     * @return 消息
     */
    public SendMessage sendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build();
    }

    /**
     * 发送消息 + 文本 + 按钮
     *
     * @param chatId 目标
     * @param text   文本
     * @param button 按钮（消息按钮、键盘按钮）
     * @return 消息
     */
    public SendMessage sendMessage(Long chatId, String text, ReplyKeyboard button) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(button)
                .build();
    }

    /**
     * 回复消息 + 文本
     *
     * @param chatId    目标
     * @param messageId 与目标的消息id
     * @param text      文本
     * @return 消息
     */
    public SendMessage replyMessage(Long chatId, Integer messageId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build();
    }

    /**
     * 回复消息 + 文本 + 按钮
     *
     * @param chatId    目标
     * @param messageId 与目标的消息id
     * @param text      文本
     * @param button    按钮（消息按钮、键盘按钮）
     * @return 消息
     */
    public SendMessage replyMessage(Long chatId, Integer messageId, String text, ReplyKeyboard button) {
        return SendMessage.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(button)
                .build();
    }

    /**
     * 发送图片
     * @param chatId 目标
     * @param photo  图片（本地路径、URL、Telegram文件ID）
     * @return       图片消息
     */
    public SendPhoto sendPhoto(Long chatId, String photo) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(resolveInputFile(photo))
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 发送图片 + 文本说明
     * @param chatId  目标
     * @param photo   图片（本地路径、URL、Telegram文件ID）
     * @param caption 图片文字说明
     * @return        图片消息
     */
    public SendPhoto sendPhoto(Long chatId, String photo, String caption) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(resolveInputFile(photo))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 发送图片 + 按钮
     * @param chatId  目标
     * @param photo   图片（本地路径、URL、Telegram文件ID）
     * @param button  按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return        图片消息
     */
    public SendPhoto sendPhoto(Long chatId, String photo, ReplyKeyboard button) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(resolveInputFile(photo))
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 发送图片 + 文本说明 + 按钮
     * @param chatId  目标
     * @param photo   图片（本地路径、URL、Telegram文件ID）
     * @param caption 文本说明
     * @param button  按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return        图片消息
     */
    public SendPhoto sendPhoto(Long chatId, String photo, String caption, ReplyKeyboard button) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(resolveInputFile(photo))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 回复图片
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param photo     图片（本地路径、URL、Telegram文件ID）
     * @return          图片消息
     */
    public SendPhoto replyPhoto(Long chatId, Integer messageId, String photo) {
        return SendPhoto.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .photo(resolveInputFile(photo))
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 回复图片 + 文本说明
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param photo     图片（本地路径、URL、Telegram文件ID）
     * @param caption   图片说明文字
     * @return          图片消息
     */
    public SendPhoto replyPhoto(Long chatId, Integer messageId, String photo, String caption) {
        return SendPhoto.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .photo(resolveInputFile(photo))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 回复图片 + 按钮
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param photo     图片（本地路径、URL、Telegram文件ID）
     * @param button    按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return          图片消息
     */
    public SendPhoto replyPhoto(Long chatId, Integer messageId, String photo, ReplyKeyboard button) {
        return SendPhoto.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .photo(resolveInputFile(photo))
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 回复图片 + 文本说明 + 按钮
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param photo     图片（本地路径、URL、Telegram文件ID）
     * @param caption   图片说明文字
     * @param button    按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return          图片消息
     */
    public SendPhoto replyPhoto(Long chatId, Integer messageId, String photo, String caption, ReplyKeyboard button) {
        return SendPhoto.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .photo(resolveInputFile(photo))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 发送视频
     * @param chatId 目标
     * @param video  视频（本地路径、URL、Telegram文件ID）
     * @return       视频消息
     */
    public SendVideo sendVideo(Long chatId, String video) {
        return SendVideo.builder()
                .chatId(chatId)
                .video(resolveInputFile(video))
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 发送视频 + 文本说明
     * @param chatId  目标
     * @param video   视频（本地路径、URL、Telegram文件ID）
     * @param caption 视频文字说明
     * @return        视频消息
     */
    public SendVideo sendVideo(Long chatId, String video, String caption) {
        return SendVideo.builder()
                .chatId(chatId)
                .video(resolveInputFile(video))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 发送视频 + 按钮
     * @param chatId 目标
     * @param video  视频（本地路径、URL、Telegram文件ID）
     * @param button 按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return       视频消息
     */
    public SendVideo sendVideo(Long chatId, String video, ReplyKeyboard button) {
        return SendVideo.builder()
                .chatId(chatId)
                .video(resolveInputFile(video))
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 发送视频 + 文本说明 + 按钮
     * @param chatId  目标
     * @param video   视频（本地路径、URL、Telegram文件ID）
     * @param caption 文本说明
     * @param button  按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return        视频消息
     */
    public SendVideo sendVideo(Long chatId, String video, String caption, ReplyKeyboard button) {
        return SendVideo.builder()
                .chatId(chatId)
                .video(resolveInputFile(video))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 回复视频
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param video     视频（本地路径、URL、Telegram文件ID）
     * @return          视频消息
     */
    public SendVideo replyVideo(Long chatId, Integer messageId, String video) {
        return SendVideo.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .video(resolveInputFile(video))
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 回复视频 + 文本说明
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param video     视频（本地路径、URL、Telegram文件ID）
     * @param caption   视频说明文字
     * @return          视频消息
     */
    public SendVideo replyVideo(Long chatId, Integer messageId, String video, String caption) {
        return SendVideo.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .video(resolveInputFile(video))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 回复视频 + 按钮
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param video     视频（本地路径、URL、Telegram文件ID）
     * @param button    按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return          视频消息
     */
    public SendVideo replyVideo(Long chatId, Integer messageId, String video, ReplyKeyboard button) {
        return SendVideo.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .video(resolveInputFile(video))
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 回复视频 + 文本说明 + 按钮
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param video     视频（本地路径、URL、Telegram文件ID）
     * @param caption   视频说明文字
     * @param button    按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return          视频消息
     */
    public SendVideo replyVideo(Long chatId, Integer messageId, String video, String caption, ReplyKeyboard button) {
        return SendVideo.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .video(resolveInputFile(video))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 发送文件
     * @param chatId 目标
     * @param file   文件（本地路径、URL、Telegram文件ID）
     * @return       文件消息
     */
    public SendDocument sendDocument(Long chatId, String file) {
        return SendDocument.builder()
                .chatId(chatId)
                .document(resolveInputFile(file))
                .build();
    }

    /**
     * 发送文件 + 文本说明
     * @param chatId 目标
     * @param file   文件（本地路径、URL、Telegram文件ID）
     * @param caption 文件文字说明
     * @return       文件消息
     */
    public SendDocument sendDocument(Long chatId, String file, String caption) {
        return SendDocument.builder()
                .chatId(chatId)
                .document(resolveInputFile(file))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 发送文件 + 按钮
     * @param chatId 目标
     * @param file   文件（本地路径、URL、Telegram文件ID）
     * @param button 按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return       文件消息
     */
    public SendDocument sendDocument(Long chatId, String file, ReplyKeyboard button) {
        return SendDocument.builder()
                .chatId(chatId)
                .document(resolveInputFile(file))
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 发送文件 + 文本说明 + 按钮
     * @param chatId 目标
     * @param file   文件（本地路径、URL、Telegram文件ID）
     * @param caption 文件说明文字
     * @param button 按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return       文件消息
     */
    public SendDocument sendDocument(Long chatId, String file, String caption, ReplyKeyboard button) {
        return SendDocument.builder()
                .chatId(chatId)
                .document(resolveInputFile(file))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 回复文件
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param file      文件（本地路径、URL、Telegram文件ID）
     * @return          文件消息
     */
    public SendDocument replyDocument(Long chatId, Integer messageId, String file) {
        return SendDocument.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .document(resolveInputFile(file))
                .build();
    }

    /**
     * 回复文件 + 文本说明
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param file      文件（本地路径、URL、Telegram文件ID）
     * @param caption   文件说明文字
     * @return          文件消息
     */
    public SendDocument replyDocument(Long chatId, Integer messageId, String file, String caption) {
        return SendDocument.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .document(resolveInputFile(file))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 回复文件 + 按钮
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param file      文件（本地路径、URL、Telegram文件ID）
     * @param button    按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return          文件消息
     */
    public SendDocument replyDocument(Long chatId, Integer messageId, String file, ReplyKeyboard button) {
        return SendDocument.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .document(resolveInputFile(file))
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 回复文件 + 文本说明 + 按钮
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param file      文件（本地路径、URL、Telegram文件ID）
     * @param caption   文件说明文字
     * @param button    按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return          文件消息
     */
    public SendDocument replyDocument(Long chatId, Integer messageId, String file, String caption, ReplyKeyboard button) {
        return SendDocument.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .document(resolveInputFile(file))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 发送动图（Animation / GIF）
     * @param chatId   目标
     * @param animation 动图（本地路径、URL、Telegram文件ID）
     * @return         动图消息
     */
    public SendAnimation sendAnimation(Long chatId, String animation) {
        return SendAnimation.builder()
                .chatId(chatId)
                .animation(resolveInputFile(animation))
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 发送动图 + 文本说明
     * @param chatId   目标
     * @param animation 动图（本地路径、URL、Telegram文件ID）
     * @param caption  文本说明
     * @return         动图消息
     */
    public SendAnimation sendAnimation(Long chatId, String animation, String caption) {
        return SendAnimation.builder()
                .chatId(chatId)
                .animation(resolveInputFile(animation))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 发送动图 + 按钮
     * @param chatId   目标
     * @param animation 动图（本地路径、URL、Telegram文件ID）
     * @param button   按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return         动图消息
     */
    public SendAnimation sendAnimation(Long chatId, String animation, ReplyKeyboard button) {
        return SendAnimation.builder()
                .chatId(chatId)
                .animation(resolveInputFile(animation))
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 发送动图 + 文本说明 + 按钮
     * @param chatId   目标
     * @param animation 动图（本地路径、URL、Telegram文件ID）
     * @param caption  文本说明
     * @param button   按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return         动图消息
     */
    public SendAnimation sendAnimation(Long chatId, String animation, String caption, ReplyKeyboard button) {
        return SendAnimation.builder()
                .chatId(chatId)
                .animation(resolveInputFile(animation))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 回复动图
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param animation 动图（本地路径、URL、Telegram文件ID）
     * @return          动图消息
     */
    public SendAnimation replyAnimation(Long chatId, Integer messageId, String animation) {
        return SendAnimation.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .animation(resolveInputFile(animation))
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 回复动图 + 文本说明
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param animation 动图（本地路径、URL、Telegram文件ID）
     * @param caption   文本说明
     * @return          动图消息
     */
    public SendAnimation replyAnimation(Long chatId, Integer messageId, String animation, String caption) {
        return SendAnimation.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .animation(resolveInputFile(animation))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 回复动图 + 按钮
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param animation 动图（本地路径、URL、Telegram文件ID）
     * @param button    按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return          动图消息
     */
    public SendAnimation replyAnimation(Long chatId, Integer messageId, String animation, ReplyKeyboard button) {
        return SendAnimation.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .animation(resolveInputFile(animation))
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 回复动图 + 文本说明 + 按钮
     * @param chatId    目标
     * @param messageId 被回复消息ID
     * @param animation 动图（本地路径、URL、Telegram文件ID）
     * @param caption   文本说明
     * @param button    按钮（ReplyKeyboard 或 InlineKeyboard）
     * @return          动图消息
     */
    public SendAnimation replyAnimation(Long chatId, Integer messageId, String animation, String caption, ReplyKeyboard button) {
        return SendAnimation.builder()
                .chatId(chatId)
                .replyToMessageId(messageId)
                .animation(resolveInputFile(animation))
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 编辑文本消息内容
     * <p>常用于更新文字或替换内联按钮的文字内容</p>
     *
     * @param chatId    聊天ID
     * @param messageId 消息ID
     * @param text      新的文本内容
     * @return          编辑消息对象
     */
    public EditMessageText editMessageText(Long chatId, Integer messageId, String text) {
        return EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .build();
    }

    /**
     * 编辑文本消息 + 按钮
     * <p>修改文字的同时替换或更新底部按钮</p>
     *
     * @param chatId    聊天ID
     * @param messageId 消息ID
     * @param text      新的文本内容
     * @param button    按钮（InlineKeyboard）
     * @return          编辑消息对象
     */
    public EditMessageText editMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup button) {
        return EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .replyMarkup(button)
                .build();
    }

    /**
     * 编辑媒体说明（Caption）
     * <p>用于修改已发送的图片、视频、动图、文件的文字说明或按钮</p>
     *
     * @param chatId    聊天ID
     * @param messageId 消息ID
     * @param caption   新的文字说明
     * @return          编辑对象
     */
    public EditMessageCaption editMessageCaption(Long chatId, Integer messageId, String caption) {
        return EditMessageCaption.builder()
                .chatId(chatId)
                .messageId(messageId)
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .build();
    }

    /**
     * 编辑媒体说明 + 按钮
     * <p>修改说明文字并同时替换底部按钮</p>
     *
     * @param chatId    聊天ID
     * @param messageId 消息ID
     * @param caption   新的文字说明
     * @param button    新的按钮（InlineKeyboard）
     * @return          编辑对象
     */
    public EditMessageCaption editMessageCaption(Long chatId, Integer messageId, String caption, InlineKeyboardMarkup button) {
        return EditMessageCaption.builder()
                .chatId(chatId)
                .messageId(messageId)
                .caption(caption)
                .parseMode(ParseMode.HTML)
                .replyMarkup(button)
                .build();
    }

    /**
     * 编辑消息媒体内容（图片、视频、动图、文件）
     * <p>自动根据文件类型选择对应 InputMedia（Photo、Video、Animation、Document），
     * 并兼容本地文件、网络URL、以及Telegram文件ID。</p>
     *
     * @param chatId    聊天ID
     * @param messageId 消息ID
     * @param mediaPath 新的媒体路径（本地路径、网络URL、或Telegram文件ID）
     * @param caption   可选说明文字
     * @param button    可选按钮（InlineKeyboard）
     * @return          编辑媒体对象
     */
    public EditMessageMedia editMessageMedia(Long chatId, Integer messageId, String mediaPath, String caption, InlineKeyboardMarkup button) {
        if (mediaPath == null || mediaPath.isEmpty()) {
            throw new BotException("mediaPath 不能为空");
        }

        InputMedia media = resolveInputMediaSmart(mediaPath, caption);

        return EditMessageMedia.builder()
                .chatId(chatId)
                .messageId(messageId)
                .media(media)
                .replyMarkup(button)
                .build();
    }

    /**
     * 编辑消息按钮（ReplyMarkup）
     * <p>不改变文字内容，只替换底部 Inline 按钮区域。</p>
     *
     * @param chatId    聊天ID
     * @param messageId 消息ID
     * @param button    新按钮布局（InlineKeyboard）
     * @return          编辑按钮对象
     */
    public EditMessageReplyMarkup editMessageReplyMarkup(Long chatId, Integer messageId, InlineKeyboardMarkup button) {
        return EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(button)
                .build();
    }

    /**
     * 媒体类型枚举
     */
    public enum MediaType {
        PHOTO,
        VIDEO,
        ANIMATION,
        DOCUMENT
    }

    /**
     * 解析文件路径为 InputFile
     * <p>支持三种来源：
     * <ul>
     *   <li>网络链接（http/https）→ Telegram 自动拉取</li>
     *   <li>本地文件路径 → 上传文件</li>
     *   <li>Telegram FileId → 直接引用</li>
     * </ul>
     *
     * @param path 文件路径（本地路径、URL、Telegram文件ID）
     * @return     InputFile 实例
     */
    private InputFile resolveInputFile(String path) {
        if (path == null || path.isBlank()) {
            throw new BotException("文件路径不能为空");
        }

        // 网络链接
        if (isHttpUrl(path)) {
            return new InputFile(path);
        }

        // 本地文件
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            return new InputFile(file);
        }

        // 默认视为 Telegram FileId
        return new InputFile(path);
    }

    /**
     * 智能识别媒体类型并创建对应 InputMedia（自动判断类型与来源）
     * <p>支持本地文件、HTTP(S)链接、Telegram FileId。</p>
     *
     * @param path    媒体路径（支持 本地路径 / URL / Telegram FileId）
     * @param caption 说明文字，可为空
     * @return        InputMedia 实例
     */
    private InputMedia resolveInputMediaSmart(String path, String caption) {
        if (path == null || path.isBlank()) {
            throw new BotException("媒体路径不能为空");
        }

        var file = new File(path);
        var lower = path.toLowerCase();
        var mediaType = resolveMediaType(lower);

        // 创建 builder（根据类型）
        InputMedia.InputMediaBuilder<?, ?> builder = switch (mediaType) {
            case PHOTO -> InputMediaPhoto.builder();
            case VIDEO -> InputMediaVideo.builder();
            case ANIMATION -> InputMediaAnimation.builder();
            case DOCUMENT -> InputMediaDocument.builder();
        };

        // 识别来源
        if (isHttpUrl(path)) {
            builder.media(path);
        } else if (file.exists() && file.isFile()) {
            builder.media(file, file.getName());
        } else {
            builder.media(path); // Telegram FileId
        }

        // 附加说明
        builder.parseMode(ParseMode.HTML);
        if (caption != null && !caption.isBlank()) {
            builder.caption(caption);
        }

        return builder.build();
    }

    /**
     * 判断是否为 HTTP(S) 链接
     */
    private boolean isHttpUrl(String path) {
        try {
            URL url = new URL(path);
            String protocol = url.getProtocol();
            return "http".equalsIgnoreCase(protocol) || "https".equalsIgnoreCase(protocol);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据文件后缀智能识别媒体类型
     * <p>默认返回 DOCUMENT 类型。</p>
     *
     * @param lowerPath 小写文件路径
     * @return 媒体类型
     */
    private MediaType resolveMediaType(String lowerPath) {
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")
                || lowerPath.endsWith(".png") || lowerPath.endsWith(".webp")) {
            return MediaType.PHOTO;
        }
        if (lowerPath.endsWith(".mp4") || lowerPath.endsWith(".mov") || lowerPath.endsWith(".mkv")) {
            return MediaType.VIDEO;
        }
        if (lowerPath.endsWith(".gif")) {
            return MediaType.ANIMATION;
        }
        return MediaType.DOCUMENT;
    }

    /**
     * 置顶消息（用户、群组、频道）
     *
     * @param chatId    聊天ID
     * @param messageId 消息ID
     * @param notify    是否通知所有成员（true 表示发送置顶通知）
     */
    public void pinMessage(Long chatId, Integer messageId, boolean notify) {
        PinChatMessage pinChatMessage = PinChatMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .disableNotification(!notify)
                .build();
        try {
            client.execute(pinChatMessage);
        } catch (TelegramApiException e) {
            throw new BotException("置顶消息失败", e);
        }
    }

    /**
     * 取消单条置顶消息（用户、群组、频道）
     *
     * @param chatId 聊天ID
     * @param messageId 消息ID
     */
    public void unpinMessage(Long chatId, Integer messageId) {
        UnpinChatMessage unpinChatMessage = UnpinChatMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
        try {
            client.execute(unpinChatMessage);
        } catch (TelegramApiException e) {
            throw new BotException("取消置顶消息失败", e);
        }
    }

    /**
     * 取消所有置顶消息（用户、群组、频道）
     *
     * @param chatId 聊天ID
     */
    public void unpinAllMessages(Long chatId) {
        UnpinAllChatMessages unpinAllChatMessages = UnpinAllChatMessages.builder()
                .chatId(chatId)
                .build();
        try {
            client.execute(unpinAllChatMessages);
        } catch (TelegramApiException e) {
            throw new BotException("取消所有置顶消息失败", e);
        }
    }

    /**
     * 提升群组、频道成员为管理员（授予全部权限）
     *
     * @param chatId 群组ID
     * @param userId 用户ID
     */
    public void promoteUser(Long chatId, Long userId) {
        PromoteChatMember chatMember = PromoteChatMember.builder()
                .chatId(chatId)
                .userId(userId)
                .canManageChat(true)
                .canChangeInformation(true)
                .canPostMessages(true)
                .canEditMessages(true)
                .canDeleteMessages(true)
                .canInviteUsers(true)
                .canRestrictMembers(true)
                .canPinMessages(true)
                .canPromoteMembers(true)
                .isAnonymous(false)
                .canManageVideoChats(true)
                .canManageTopics(true)
                .canPostStories(true)
                .canEditStories(true)
                .canDeleteStories(true)
                .build();
        try {
            client.execute(chatMember);
        } catch (TelegramApiException e) {
            throw new BotException("提升群组、频道成员为管理员失败", e);
        }
    }

    /**
     * 撤销群组、频道管理员权限（降级为普通成员）
     *
     * @param chatId 群组、频道ID
     * @param userId 用户ID
     */
    public void demoteUser(Long chatId, Long userId) {
        PromoteChatMember chatMember = PromoteChatMember.builder()
                .chatId(chatId)
                .userId(userId)
                .canManageChat(false)
                .canChangeInformation(false)
                .canPostMessages(false)
                .canEditMessages(false)
                .canDeleteMessages(false)
                .canInviteUsers(false)
                .canRestrictMembers(false)
                .canPinMessages(false)
                .canPromoteMembers(false)
                .isAnonymous(false)
                .canManageVideoChats(false)
                .canManageTopics(false)
                .canPostStories(false)
                .canEditStories(false)
                .canDeleteStories(false)
                .build();
        try {
            client.execute(chatMember);
        } catch (TelegramApiException e) {
            throw new BotException("撤销群组、频道管理员权限失败", e);
        }
    }

    /**
     * 获群组、频道群信息
     *
     * @param chatId 群组、频道ID
     * @return Chat 对象
     */
    public Chat getChat(Long chatId) {
        try {
            return this.client.execute(GetChat.builder().chatId(chatId).build());
        } catch (TelegramApiException e) {
            throw new BotException("获群组、频道群信息失败", e);
        }
    }

    /**
     * 获取群组、频道成员数量
     *
     * @param chatId 群组、频道ID
     * @return 成员数量
     */
    public Integer getChatMemberCount(Long chatId) {
        try {
            return this.client.execute(GetChatMemberCount.builder().chatId(chatId).build());
        } catch (TelegramApiException e) {
            throw new BotException("获取群组、频道成员数量失败", e);
        }
    }

    /**
     * 获取群组、频道管理员列表
     *
     * @param chatId 群组、频道ID
     * @return 管理员列表
     */
    public List<ChatMember> getChatAdministrators(Long chatId) {
        try {
            return this.client.execute(GetChatAdministrators.builder().chatId(chatId).build());
        } catch (TelegramApiException e) {
            throw new BotException("获取群组、频道管理员列表失败", e);
        }
    }

    /**
     * 获取群组、频道成员信息
     *
     * @param chatId 群组、频道ID
     * @param userId 用户ID
     * @return ChatMember 对象
     */
    public ChatMember getChatMember(Long chatId, Long userId) {
        try {
            return this.client.execute(GetChatMember.builder()
                    .chatId(chatId)
                    .userId(userId)
                    .build());
        } catch (TelegramApiException e) {
            throw new BotException("获取群组、频道成员信息失败", e);
        }
    }

    /**
     * 机器人退出群组、频道
     *
     * @param chatId 群组、频道ID
     */
    public void leaveChat(Long chatId) {
        LeaveChat leaveChat = LeaveChat.builder()
                .chatId(chatId)
                .build();
        try {
            client.execute(leaveChat);
        } catch (TelegramApiException e) {
            throw new BotException("机器人退出群组、频道失败", e);
        }
    }
}
