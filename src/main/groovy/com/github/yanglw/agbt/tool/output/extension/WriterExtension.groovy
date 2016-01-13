package com.github.yanglw.agbt.tool.output.extension

import com.github.yanglw.agbt.action.BuildActionGroup
import com.github.yanglw.agbt.tool.collect.task.collector.Collector
import com.github.yanglw.agbt.tool.output.MailOutput
import com.github.yanglw.agbt.tool.output.Output
import com.github.yanglw.agbt.tool.output.action.OutputAction
import com.github.yanglw.agbt.util.FileUtil
import com.github.yanglw.agbt.util.ProjectUtil
import com.github.yanglw.agbt.util.QRCodeUtil
import com.github.yanglw.agbt.util.VariantUtil
import org.gradle.api.Project

import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.BodyPart
import javax.mail.Multipart
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart

import static com.github.yanglw.agbt.util.HtmlUtil.*

/**
 * <p>
 * 信息输出 Module 。
 * </p>
 * Created by yanglw on 2016-1-8.
 */
class WriterExtension {
    final StringBuilder sb = new StringBuilder()
    final Map<String, File> images = new LinkedHashMap<>()
    final Map<String, String> fileTypes = new LinkedHashMap<>()
    final Project project

    /**
     * <p>
     * 信息处理者。
     * 将传递 {@code List&#60;Map&#62;} 作为参数。
     * 需返回一个 {@link Multipart} 对象或者一个 {@link String} 对象。<br/>
     *
     * 本类提供一系列的添加文本和图片的方法，以及将添加的文字和图片转化为 {@link Multipart} 对象的方法。
     * </p>
     *
     * 如果用户没有设置新信息处理者，则使用默认的信息处理对象。<br/>
     *
     * 用户可以通过设置本字段，修改邮件的输出内容。
     *
     * @see #getDefaultInfoBuilder()
     * @see #getMultiPart()
     * @see #addText(java.lang.String)
     * @see #addImage(java.io.File, java.lang.String)
     * @see #addImagePart(java.io.File, java.lang.String, java.lang.String)
     * @see #addQRCodeImage(java.lang.String)
     * @see #addKeyValueTextWithLF(java.lang.String, java.lang.String)
     */
    Closure infoBuilder
    /**
     * <p>
     * 信息输出者。
     * <p>
     *
     * 如果用户没有设置新的输出对象，则使用默认的 {@link MailOutput} 对象。<br/>
     * 如果用户设置了新的输出对象，则 {@link #mail} 和 {@link #infoBuilder} 对象将不再有意义。
     * @see #getOutput()
     * @see MailOutput
     */
    Output output
    MailExtension mail
    VcsExtension vcs

    WriterExtension(Project project, BuildActionGroup group) {
        this.project = project
        mail = extensions.create('mail', MailExtension, project)
        vcs = extensions.create('vcs', VcsExtension, project, group)
        group << new OutputAction(this)
    }

    /**
     * 获取默认的信息处理者。
     * @return 返回一个将所有的信息处理成一个 {@link Multipart} 对象的处理者。
     */
    Closure getDefaultInfoBuilder() {
        return { List<Map> allInfo ->
            List<Map> variantList = allInfo.findAll {
                it.containsKey(Collector.INFO_KEY_GROUP_FLAG_FILE_INFO)
            }
            if (variantList) {
                addText(bold(font('文件信息', "3", null)))
                addText(blockStart())

                variantList.each {
                    String path = it.get(Collector.INFO_KEY_OUTPUT)
                    File file
                    if (path && (file = new File(path)).exists()) {
                        addKeyValueTextWithLF('文件名称 ：', file.name)

                        def variant = it.get(Collector.INFO_KEY_VARIANT)
                        if (variant != null) {
                            // library project 无法获取 applicationId
                            if (ProjectUtil.getProjectType() == ProjectUtil.PROJECT_TYPE_APPLICATION) {
                                addKeyValueTextWithLF('package ：', VariantUtil.getApplicationId(variant))
                            }
                            addKeyValueTextWithLF('versionName ：',
                                                  VariantUtil.getVersionName(variant))
                            addKeyValueTextWithLF('versionCode ：',
                                                  String.valueOf(VariantUtil.getVersionCode(variant)))
                            addKeyValueTextWithLF('debuggable ：',
                                                  String.valueOf(VariantUtil.getDebuggable(variant)))
                        }

                        addKeyValueTextWithLF('文件路径 ：', path)
                        addKeyValueTextWithLF('文件 MD5 ：', FileUtil.generateMD5(file))
                        addKeyValueTextWithLF('文件 SHA-256 ：', FileUtil.generateSHA256(file))
                    }

                    String url = it.get(Collector.INFO_KEY_DOWNLOAD_URL)
                    if (url != null) {
                        addKeyValueTextWithLF('下载地址 ：', a(url, url))
                        addQRCodeImage(url)
                        addText(enter())
                    }
                }

                addText(blockEnd())
            }

            List<Map> vcsList = allInfo.findAll {
                it.containsKey(Collector.INFO_KEY_GROUP_FLAG_VCS_INFO)
            }
            if (vcsList) {
                addText(bold(font('VCS 信息', "3", null)))
                addText(blockStart())

                vcsList.each {
                    String url = it.get(Collector.INFO_KEY_VCS_REMOTE_URL)
                    String commitCode = it.get(Collector.INFO_KEY_VCS_COMMIT_CODE)
                    String log = it.get(Collector.INFO_KEY_VCS_LOG)
                    if (url) {
                        addKeyValueTextWithLF('远程地址 ：', url)
                    }
                    if (commitCode) {
                        addKeyValueTextWithLF('commit code ：', commitCode)
                    }
                    if (log) {
                        addKeyValueTextWithLF('log ：', '')
                        log.eachLine {
                            addText(it)
                            addText(enter())
                        }
                    }
                }

                addText(blockEnd())
            }

            return getMultiPart()
        }
    }

    /** 获取输出对象。如果用户没有设置新的输出对象，则使用默认的 {@link MailOutput} 对象。 */
    Output getOutput() {
        if (output == null) {
            return new MailOutput(this)
        }
        return output
    }

    Multipart getMultiPart() {
        final Multipart part = new MimeMultipart('related')

        if (sb.length() > 0) {
            BodyPart htmlPart = new MimeBodyPart()
            htmlPart.setContent(sb.toString(), "text/html; charset=$mail.charset");
            part.addBodyPart(htmlPart)
        }

        if (images) {
            images.each {
                String id = it.key
                File file = it.value
                String fileType = fileTypes.get(id) ?: QRCodeUtil.QR_CODE_FILE_FORMAT_NAME
                BodyPart imagePart = new MimeBodyPart()
                imagePart.setDataHandler(new DataHandler(new FileDataSource(file)))
                imagePart.setContentID(id)
                imagePart.setHeader('Content-Type', "image/$fileType")
                part.addBodyPart(imagePart)
            }
        }
        return part
    }

    /** 清除已经添加的文本内容和图片。 */
    WriterExtension clean() {
        sb.delete(0, sb.length())
        images.clear()
        fileTypes.clear()
        return this
    }

    /** 添加文本内容。 */
    WriterExtension addText(String text) {
        sb << text
        return this
    }

    /** 添加具有键值对的文本内容，并自动增加换行符。 */
    WriterExtension addKeyValueTextWithLF(String key, String value) {
        this.addText(bold(font(key, "3", null)))
            .addText(font(value, "3", "blue"))
            .addText(enter())
        return this
    }

    /**
     * 添加图片。
     * @param imageFile 图片文件。
     * @param fileType 图片文件类型。
     */
    WriterExtension addImage(File imageFile, String fileType) {
        if (imageFile.exists()) {
            String id = toBase64(String.valueOf(imageFile.absolutePath.hashCode()))

            addText(img("cid:$id"))
            addImagePart(imageFile, id, fileType)
        }
        return this
    }

    /**
     * 添加图片。
     * @param imageFile 图片文件。
     * @param id 当前图片在邮件中的索引 Id 。
     * @param fileType 图片文件类型。
     */
    WriterExtension addImagePart(File imageFile, String id, String fileType) {
        if (imageFile.exists()) {
            images.put(id, imageFile)
            fileTypes.put(id, fileType)
        }
        return this
    }

    /** 添加二维码图片。 */
    WriterExtension addQRCodeImage(String text) {
        if (text) {
            String info = toBase64(text)

            File qRCodeFile = new File("${info}.${QRCodeUtil.QR_CODE_FILE_FORMAT_NAME}")
            qRCodeFile.deleteOnExit()
            QRCodeUtil.createQRImage(qRCodeFile,
                                     text,
                                     QRCodeUtil.QR_CODE_FILE_IMAGE_SIZE,
                                     QRCodeUtil.QR_CODE_FILE_FORMAT_NAME)
            addImage(qRCodeFile, QRCodeUtil.QR_CODE_FILE_FORMAT_NAME)
        }
        return this
    }
}
