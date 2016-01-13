package com.github.yanglw.agbt.tool.channel

/**
 * <p>
 * 渠道列表文件解析工具类。
 * </p>
 * Created by yanglw on 2015-11-27.
 */
class ChannelUtil {
    /**
     * 渠道文件名称与渠道号分隔符。前面的是渠道文件名称，后面的是渠道号。
     * 渠道文件名称即 {@code META-INF/} 目录下渠道文件的名称。
     */
    public static final String SPLIT_CHARACTER = "-";
    /** apk 文件中的 {@code META-INF} 文件夹的名称。 */
    public static final String META_INF = "META-INF/";


    /**
     * 从渠道列表文件中获取渠道列表。
     * 关于渠道列表文件的每一行内容，有以下准则：
     * <ol>
     * <li>一行只有一个渠道号。</li>
     * <li>每一行均需要含有 {@link #SPLIT_CHARACTER} 字符。</li>
     * <li>{@link #SPLIT_CHARACTER} 字符前的内容为写入 {@link #META_INF} 中的渠道文件的名称，字符后的内容为 apk 文件名称追加的内容。</li>
     * <li>以 # 开头的行将会被忽略。</li>
     * </ol>
     * 例如，在这么一个渠道列表文件中：
     * <pre>
     *     channel_channel1-channel1
     *     #channel_channel2-channel2
     * </pre>
     * 第一行为有效的数据，第二行会被自动忽略。
     *
     * @param file
     *         渠道列表文件。
     *
     * @return 渠道列表。
     */
    static List<ChannelInfo> getChannelList(File file) {
        ArrayList<ChannelInfo> list = new ArrayList<>();

        file.eachLine { line ->
            if (line.startsWith("#")) {
                return
            }
            int index = line.indexOf(SPLIT_CHARACTER);
            if (index <= 0 || index >= line.length()) {
                return
            }
            String[] split = line.split(SPLIT_CHARACTER)
            list.add(new ChannelInfo(fileName: split[0],
                                     appendStr: split[1]))
        }
        return list
    }
}
