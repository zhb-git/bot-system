package com.bot_system.common.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @className: BotUtil
 * @author: Java之父
 * @date: 2025/10/4 23:40
 * @version: 1.0.0
 * @description: 机器人工具类
 */
public final class BotUtil {
    /**
     * 按钮构建相关工具。
     */
    public static final class Button {
        /**
         * 构建 InlineKeyboard（行列自动排布）。
         *
         * @param buttonInfos 混合类型按钮列表
         * @param rowSize     每行按钮数量（&gt;=1）
         * @return 内联键盘；当入参为空时返回 null
         */
        public static InlineKeyboardMarkup build(List<ButInfo> buttonInfos, int rowSize) {
            if (buttonInfos == null || buttonInfos.isEmpty() || rowSize <= 0) return null;

            List<InlineKeyboardRow> rows = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();

            for (int i = 0; i < buttonInfos.size(); i++) {
                row.add(buttonInfos.get(i).toInlineKeyboardButton());
                if ((i + 1) % rowSize == 0) {
                    rows.add(new InlineKeyboardRow(row));
                    row = new ArrayList<>();
                }
            }
            if (!row.isEmpty()) rows.add(new InlineKeyboardRow(row));

            return InlineKeyboardMarkup.builder().keyboard(rows).build();
        }

        /**
         * 构建 ReplyKeyboard（纯文本按钮）。
         *
         * @param texts   文本按钮集合
         * @param rowSize 每行按钮数量（&gt;=1）
         * @return 回复键盘；当入参为空时返回 null
         */
        public static ReplyKeyboardMarkup build(Collection<String> texts, int rowSize) {
            if (texts == null || texts.isEmpty() || rowSize <= 0) return null;

            List<KeyboardRow> rows = new ArrayList<>();
            KeyboardRow row = new KeyboardRow();
            int count = 0;

            for (String text : texts) {
                row.add(text);
                count++;
                if (count % rowSize == 0) {
                    rows.add(row);
                    row = new KeyboardRow();
                }
            }
            if (!row.isEmpty()) rows.add(row);

            return ReplyKeyboardMarkup.builder()
                    .keyboard(rows)
                    .resizeKeyboard(true)
                    .oneTimeKeyboard(false)
                    .selective(true)
                    .build();
        }

        /**
         * 直接使用行集合构建 ReplyKeyboard。
         */
        public static ReplyKeyboardMarkup build(List<KeyboardRow> rows) {
            return ReplyKeyboardMarkup.builder()
                    .keyboard(rows)
                    .resizeKeyboard(true)
                    .oneTimeKeyboard(false)
                    .selective(true)
                    .build();
        }
    }

    /**
     * 文本格式化工具（HTML 模式）。
     */
    public static final class Text {

        /**
         * 多行代码块（HTML &lt;pre&gt;）。
         */
        public static String codeBlock(String text) {
            return "<pre>" + escapeHtml(text) + "</pre>";
        }

        /**
         * 单行代码块（HTML &lt;code&gt;）。
         */
        public static String code(Object text) {
            return "<code>" + escapeHtml(String.valueOf(text)) + "</code>";
        }

        /**
         * 加粗。
         */
        public static String bold(String text) {
            return "<b>" + escapeHtml(text) + "</b>";
        }

        /**
         * 斜体。
         */
        public static String italic(String text) {
            return "<i>" + escapeHtml(text) + "</i>";
        }

        /**
         * 下划线。
         */
        public static String underline(String text) {
            return "<u>" + escapeHtml(text) + "</u>";
        }

        /**
         * 删除线。
         */
        public static String strike(String text) {
            return "<s>" + escapeHtml(text) + "</s>";
        }

        /**
         * 超链接（文本会被转义，链接不转义）。
         */
        public static String link(String url, String text) {
            return "<a href='" + url + "'>" + escapeHtml(text) + "</a>";
        }

        /**
         * 用户链接（优先使用 @username）。
         *
         * @param username 例如 "some_user" 或 "@some_user"
         * @param nickname 展示昵称
         */
        public static String userLink(String username, String nickname) {
            String u = Links.normalizeUsername(username);
            if (u == null || u.isEmpty()) return escapeHtml(nickname);
            return "<a href='https://t.me/" + u + "'>" + escapeHtml(nickname) + "</a>";
        }

        /**
         * HTML 转义。
         */
        public static String escapeHtml(String text) {
            if (text == null) return "";
            return text.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }
    }

    /**
     * 通用按钮描述接口。
     */
    public interface ButInfo {
        InlineKeyboardButton toInlineKeyboardButton();
    }

    /**
     * 链接按钮（打开 URL）。
     */
    public static class LinkButton implements ButInfo {
        private final String name;
        private final String url;

        public LinkButton(String name, String url) {
            this.name = name;
            this.url = url;
        }

        @Override
        public InlineKeyboardButton toInlineKeyboardButton() {
            return InlineKeyboardButton.builder().text(name).url(url).build();
        }
    }

    /**
     * 回调按钮（callback_data）。
     */
    public static class CallbackButton implements ButInfo {
        private final String name;
        private final String callbackData;

        public CallbackButton(String name, String callbackData) {
            this.name = name;
            this.callbackData = callbackData;
        }

        @Override
        public InlineKeyboardButton toInlineKeyboardButton() {
            return InlineKeyboardButton.builder().text(name).callbackData(callbackData).build();
        }
    }

    /**
     * WebApp 按钮（打开 Telegram 内嵌网页）。
     */
    public static class AppButton implements ButInfo {
        private final String name;
        private final String webAppUrl;

        public AppButton(String name, String webAppUrl) {
            this.name = name;
            this.webAppUrl = webAppUrl;
        }

        @Override
        public InlineKeyboardButton toInlineKeyboardButton() {
            return InlineKeyboardButton.builder()
                    .text(name)
                    .webApp(new WebAppInfo(webAppUrl))
                    .build();
        }
    }

    /**
     * 切换到全局内联查询。
     */
    public static class SwitchInlineQueryButton implements ButInfo {
        private final String name;
        private final String query;

        public SwitchInlineQueryButton(String name, String query) {
            this.name = name;
            this.query = query;
        }

        @Override
        public InlineKeyboardButton toInlineKeyboardButton() {
            return InlineKeyboardButton.builder().text(name).switchInlineQuery(query).build();
        }
    }

    /**
     * 在当前聊天切换到内联查询。
     */
    public static class SwitchInlineQueryCurrentChatButton implements ButInfo {
        private final String name;
        private final String query;

        public SwitchInlineQueryCurrentChatButton(String name, String query) {
            this.name = name;
            this.query = query;
        }

        @Override
        public InlineKeyboardButton toInlineKeyboardButton() {
            return InlineKeyboardButton.builder().text(name).switchInlineQueryCurrentChat(query).build();
        }
    }

    /**
     * 支付按钮（用于支付消息）。
     */
    public static class PayButton implements ButInfo {
        private final String name;

        public PayButton(String name) {
            this.name = name;
        }

        @Override
        public InlineKeyboardButton toInlineKeyboardButton() {
            return InlineKeyboardButton.builder().text(name).pay(true).build();
        }
    }

    /* -------------------------------- Links -------------------------------- */

    /**
     * 链接与 ID 处理工具。
     * <p>支持两种引用形式：chatId（Long）与用户名（String）。用户名可写为 "name"、"@name"，或完整 "<a href="https://t.me/name">...</a>"。</p>
     * <p>消息链接构成规则：</p>
     * <ul>
     *   <li>公开聊天（有用户名）：<code><a href="https://t.me/">...</a>{username}/{messageId}</code></li>
     *   <li>私有超级群/频道（chatId 形如 -100xxxxxxxxxx）：<code>https://t.me/c/{absId}/{messageId}</code>，其中 <code>absId</code> 为去掉 -100 的数字。</li>
     *   <li>话题消息（topics）：公开聊天为 <code>https://t.me/{username}/{topicId}/{messageId}</code>；私有聊天为 <code>https://t.me/c/{absId}/{topicId}/{messageId}</code>。</li>
     * </ul>
     */
    public static final class Links {

        /**
         * 规范化用户名。
         * <p>去掉前缀 @，去掉 "<a href="https://t.me/">...</a>" 或 "http://t.me/" 前缀。</p>
         */
        public static String normalizeUsername(String ref) {
            if (ref == null || ref.isBlank()) return null;
            String r = ref.trim();
            if (r.startsWith("https://t.me/")) r = r.substring("https://t.me/".length());
            else if (r.startsWith("http://t.me/")) r = r.substring("http://t.me/".length());
            if (r.startsWith("@")) r = r.substring(1);
            return r;
        }

        /**
         * 判断是否可能是用户名（不是纯数字，也不是以 '-' 开头的内部 ID）。
         */
        public static boolean looksLikeUsername(String ref) {
            if (ref == null || ref.isBlank()) return false;
            String r = normalizeUsername(ref);
            if (r == null) return false;
            // 简单判断：至少 5 位，由字母/数字/下划线组成
            if (r.chars().anyMatch(ch -> !(Character.isLetterOrDigit(ch) || ch == '_'))) return false;
            return r.length() >= 5 && !Character.isDigit(r.charAt(0));
        }

        /**
         * 去除 -100 前缀，得到 t.me/c 使用的数字 ID 字符串。
         * <p>例如：-1001234567890 → "1234567890"</p>
         */
        public static String stripSupergroupPrefix(long chatId) {
            String s = String.valueOf(chatId);
            if (s.startsWith("-100")) return s.substring(4);
            // 非 -100 前缀的一般群（老式群）没有稳定 c 链接；此处返回去掉负号的绝对值字符串。
            if (s.startsWith("-")) return s.substring(1);
            return s;
        }

        /**
         * 构造聊天链接（无消息）。
         * <p>公开聊天返回 <a href="https://t.me/">...</a>{username}；私有聊天返回 <a href="https://t.me/c/">...</a>{absId}</p>
         *
         * @param chatRef 用户名（String）或 chatId（Long）
         */
        public static String chatLink(Object chatRef) {
            if (chatRef == null) return null;
            if (chatRef instanceof String) {
                String u = normalizeUsername((String) chatRef);
                return (u == null || u.isEmpty()) ? null : "https://t.me/" + u;
            }
            if (chatRef instanceof Long) {
                long id = (Long) chatRef;
                String absId = stripSupergroupPrefix(id);
                return "https://t.me/c/" + absId;
            }
            return null;
        }

        /**
         * 构造普通消息链接（不含话题）。
         * <p>公开聊天：t.me/{username}/{messageId}；私有：t.me/c/{absId}/{messageId}</p>
         */
        public static String messageLink(Object chatRef, Integer messageId) {
            String base = resolveBaseLink(chatRef);
            if (base == null) return null;
            if (messageId == null || messageId <= 0) return base;
            return base + "/" + messageId;
        }

        /**
         * 构造话题消息链接（Thread/Topic）。
         * <p>公开聊天：t.me/{username}/{topicId}/{messageId}</p>
         * <p>私有聊天：t.me/c/{absId}/{topicId}/{messageId}</p>
         */
        public static String topicMessageLink(Object chatRef, Integer topicId, Integer messageId) {
            String base = resolveBaseLink(chatRef);
            if (base == null) return null;

            // 无效 topicId → 退化为普通消息链接
            if (topicId == null || topicId <= 0) {
                return messageLink(chatRef, messageId);
            }

            String suffix = (messageId == null || messageId <= 0)
                    ? String.valueOf(topicId)
                    : topicId + "/" + messageId;
            return base + "/" + suffix;
        }

        /**
         * 根据 chatRef 生成基础链接前缀。
         * <p>
         * 输入类型支持：
         * <ul>
         *   <li>String：用户名、@username、t.me/username</li>
         *   <li>Long：chatId（负号代表群/频道）</li>
         * </ul>
         * 输出：
         * <ul>
         *   <li>公开聊天 → <a href="https://t.me/">...</a>{username}</li>
         *   <li>私有聊天 → <a href="https://t.me/c/">...</a>{absId}</li>
         * </ul>
         */
        private static String resolveBaseLink(Object chatRef) {
            if (chatRef == null) return null;

            if (chatRef instanceof String str) {
                if (!looksLikeUsername(str)) return null;
                String u = normalizeUsername(str);
                return (u == null || u.isEmpty()) ? null : "https://t.me/" + u;
            }

            if (chatRef instanceof Long id) {
                String absId = stripSupergroupPrefix(id);
                return "https://t.me/c/" + absId;
            }

            return null;
        }
    }
}
