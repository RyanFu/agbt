package com.github.yanglw.agbt.tool.channel.task

import com.github.yanglw.agbt.tool.channel.ChannelInfo
import com.github.yanglw.agbt.tool.channel.ChannelUtil
import com.github.yanglw.agbt.util.ProjectUtil

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

/**
 * <p>
 * 对一个 apk 文件进行渠道打包的 Task 。
 * </p>
 * Created by yanglw on 2015-11-27.
 */
class ChannelSingleApkTask extends DefaultTask {

    /** 渠道文件的输出目录。*/
    @Optional
    String outputsFolder
    /** 渠道列表文件的路径。 */
    @Optional
    String channelFilePath
    /** apk 文件路径。 */
    @Optional
    String apkPath

    ChannelSingleApkTask() {
        setGroup(ProjectUtil.GRADLE_TASK_GROUP)
    }

    @TaskAction
    def run() {
        List<ChannelInfo> list = ChannelUtil.getChannelList(new File(channelFilePath))
        if (!list) {
            return
        }

        File channelOutputFolder = new File(outputsFolder)
        if (!channelOutputFolder.exists()) {
            channelOutputFolder.mkdirs()
        }
        File apkFile = new File(apkPath)
        int index = apkFile.name.lastIndexOf('.')
        String startStr = apkFile.name.substring(0, index)
        String endStr = apkFile.name.substring(index, apkFile.name.length())

        // 将源 apk 文件读入内存当做缓存。
        def apkBytes = apkFile.readBytes()

        list.each { channel ->
            // 将内存缓存文件写到磁盘中。
            File channelApkFile = new File(channelOutputFolder, "$startStr$ProjectUtil.SEPARATOR$channel.appendStr$endStr")
            channelApkFile.withOutputStream {
                it.write apkBytes
            }

            File temp = new File(outputsFolder, channel.fileName)
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
