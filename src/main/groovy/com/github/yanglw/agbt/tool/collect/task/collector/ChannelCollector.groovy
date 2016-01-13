package com.github.yanglw.agbt.tool.collect.task.collector

import com.github.yanglw.agbt.tool.collect.extension.CollectDefaultConfig
import com.github.yanglw.agbt.tool.channel.ChannelInfo
import com.github.yanglw.agbt.tool.channel.ChannelUtil
import com.github.yanglw.agbt.util.ProjectUtil

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants

import static CollectDefaultConfig.TEMPLATE_ENGINE_CHANNEL

/**
 * <p>
 * 具有打渠道包功能的收集者。
 * </p>
 *
 * Created by yanglw on 2016-1-6.
 */
class ChannelCollector implements Collector {
    final CollectDefaultConfig config
    /**
     * <p>
     * 获取当前 variant 的渠道文件的闭包。将传递当前 variant 作为参数。
     * </p>
     *
     * <p>
     * 关于渠道列表文件的每一行内容，有以下准则：
     * <ul>
     *     <li>一行只有一个渠道号。</li>
     *     <li>每一行均需要含有 {@code -} 字符。</li>
     *     <li>{@code -} 字符前为写入 apk 文件的渠道文件的名称，字符后为写入 apk 文件名称的内容。</li>
     *     <li>以 {@code #} 开头的行将会被忽略。</li>
     * </ul>
     *
     * 例如，在这么一个渠道列表文件中：
     *
     * <pre>
     * #channel_channel1-channel1
     * channel_channel2-channel2
     * </pre>
     *
     * 第一行会被自动忽略；
     * 第二行为有效的数据，写入 apk 渠道文件名称为 {@code channel_channel2} ，生成的渠道 apk 文件的名称将会追加 {@code -channel2} 。
     * </p>
     */
    Closure getChannelClosure

    ChannelCollector(CollectDefaultConfig config, Closure getChannelClosure) {
        this.config = config
        this.getChannelClosure = getChannelClosure
    }

    @Override
    void collectFile(Object variant, File file, int type, Map info) {
        if (type == FILE_TYPE_OUTPUT) {
            channelApk(variant, file)
        }
    }

    protected void channelApk(variant, File apkFile) {
        if (ProjectUtil.getProjectType() != ProjectUtil.PROJECT_TYPE_APPLICATION) {
            return
        }

        String path = getChannelClosure?.call(variant)
        if (path == null) {
            return
        }

        File channelFile = new File(path)
        if (!channelFile.exists()) {
            return
        }

        List<ChannelInfo> list = ChannelUtil.getChannelList(new File(path))
        if (!list) {
            return
        }

        // 将源 apk 文件读入内存当做缓存。
        def apkBytes = apkFile.readBytes()

        list.each { channel ->
            Map<String, String> map = [(TEMPLATE_ENGINE_CHANNEL): channel.appendStr]
            File channelOutputFolder = config.getVariantOutputFolder(variant, map)
            String channelApkName = config.getOutputFileName(variant, map) + '.apk'
            // 将内存缓存文件写到磁盘中。
            File channelApkFile = new File(channelOutputFolder, channelApkName)

            if (!channelApkFile.getParentFile().exists()) {
                if (!channelApkFile.getParentFile().mkdirs()) {
                    throw new RuntimeException("mk dir ${channelApkFile.getParentFile().absolutePath} fail.")
                }
            }
            if (!channelApkFile.exists()) {
                if (!channelApkFile.createNewFile()) {
                    throw new RuntimeException("create channel apk file ${channelApkFile.absolutePath} fail.")
                }
            }
            channelApkFile.withOutputStream {
                it.write apkBytes
            }
            File temp = new File(channelOutputFolder, channel.fileName)
            temp.createNewFile()
            // 将渠道文件写入到新创建的 apk 文件中。
            def zipFile = new ZipFile(channelApkFile)
            def parameters = new ZipParameters()
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE)
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL)
            parameters.setRootFolderInZip(ChannelUtil.META_INF)
            // 由于 zip4j 的 ZipParameters#setFileNameInZip(String) 方法不起作用，所以无限创建以渠道文件名称为名称的空文件加入 zip 。
            parameters.setFileNameInZip(channel.fileName)
            zipFile.addFile(temp, parameters)
            temp.delete()
        }
    }
}
