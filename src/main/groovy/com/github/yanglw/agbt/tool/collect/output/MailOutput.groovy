package com.github.yanglw.agbt.tool.collect.output

import com.github.yanglw.agbt.tool.collect.output.extension.WriterExtension
import com.github.yanglw.agbt.util.MailUtil

import javax.mail.BodyPart
import javax.mail.Multipart
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart

/**
 * <p>
 * 邮件输出者。
 * </p>
 * Created by yanglw on 2016-1-7.
 */
class MailOutput implements Output {
    final WriterExtension writer

    MailOutput(WriterExtension writer) {
        this.writer = writer
    }

    @Override
    void output(List<Map> allInfo) {
        def builder = writer.getInfoBuilder()
        if (builder == null) {
            builder = writer.getDefaultInfoBuilder()
        }

        Multipart part
        def info = builder.call(allInfo)
        if (info) {
            if (info instanceof Multipart) {
                part = info
            } else {
                part = new MimeMultipart('related')
                BodyPart htmlPart = new MimeBodyPart()
                htmlPart.setText(info.toString(), writer.mail.charset)
                part.addBodyPart(htmlPart)
            }

            if (writer.mail.verify()) {
                MailUtil.send(writer.mail, part)
            }
        }
    }
}
