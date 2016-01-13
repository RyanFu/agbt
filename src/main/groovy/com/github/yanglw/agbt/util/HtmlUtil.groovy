package com.github.yanglw.agbt.util

/**
 * <p>
 * 将字符串转化为 HTML 文本的工具类。
 * </p>
 * Created by yanglw on 2015-12-14.
 */
class HtmlUtil {

    static String blockStart() {
        return "<blockquote style=\"margin: 10px 0 10px 40px; border: none; padding: 0px;\">";
    }

    static String blockEnd() {
        return "</blockquote>";
    }

    /**
     * 设置超链接。
     * @param href 超链接地址。
     * @param content 文本内容。
     */
    static String a(String href, String content) {
        return "<a href=\"$href\">$content</a>";
    }

    /** 加粗。 */
    static String bold(String str) {
        return "<b>$str</b>";
    }

    /**
     * 设置字体。
     * @param str 文本内容。
     * @param size 字体大小。
     * @param face 字体。
     * @param color 颜色。
     */
    static String font(String str, String size, String color) {
        if (size) {
            size = "size=\"$size\""
        } else {
            size = ''
        }

        if (color) {
            color = "color=\"$color\""
        } else {
            color = ''
        }

        return "<font $size $color>$str</font>"
    }

    /**
     * 设置图片。
     * @param src 图片链接。
     */
    static String img(String src) {
        return "<img src=\"$src\"/>"
    }

    /**
     * 换行符。
     * @return 换行符。
     */
    static String enter() {
        return '<br/>'
    }

    static String toBase64(String text) {
        return text.bytes.encodeBase64().toString()
    }
}
