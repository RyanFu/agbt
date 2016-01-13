package com.github.yanglw.agbt.util

import com.github.yanglw.agbt.tool.output.extension.MailExtension

import javax.mail.Message
import javax.mail.Multipart
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * <p>
 * 发送邮件的工具类。
 * </p>
 * Created by yanglw on 2015-12-11.
 */
class MailUtil {
    /** 匹配邮箱的正则表达式。 */
    public static
    final String EMAIL_REGEX = /[\w!#$&'*+\/=?^_`{|}~-]+(?:\.[\w!#$%&'*+\/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/

    /**
     * 发送邮件。
     * @param mail 邮件内容载体。
     */
    static void send(MailExtension mail, Multipart content) {
        Session session = Session.getDefaultInstance(mail.toProperties())
        Transport transport = session.getTransport()

        // 创建邮件消息。
        MimeMessage message = new MimeMessage(session)
        // 设置邮件标题。
        message.setSubject(mail.getSubject(), mail.charset)
        // 设置发件人。
        InternetAddress form = new InternetAddress(mail.getUser(), mail.personal, mail.charset)
        message.setFrom(form)
        // 设置收件人。
        addInternetAddress(message, mail.getToList(), Message.RecipientType.TO)
        // 设置抄送。
        addInternetAddress(message, mail.getCcList(), Message.RecipientType.CC)
        // 设置密送。
        addInternetAddress(message, mail.getBccList(), Message.RecipientType.BCC)

        // 设置邮件的内容体。
        message.setContent(content)

        // start
        // fix Exception
        // javax.activation.UnsupportedDataTypeException: no object DCH for MIME type multipart/mixed;
        // boundary="----=_Part_0_1916061558.1449843354928"
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader()
        Thread.currentThread().setContextClassLoader(Session.class.getClassLoader())

        transport.connect(mail.getUser(), mail.getPassword())
        // 发送邮件
        transport.sendMessage(message, message.getAllRecipients())
        transport.close()

        Thread.currentThread().setContextClassLoader(classLoader)
        // fix Exception
        // javax.activation.UnsupportedDataTypeException: no object DCH for MIME type multipart/mixed;
        // boundary="----=_Part_0_1916061558.1449843354928"
        // end
    }

    private static void addInternetAddress(Message message, List<String> list, Message.RecipientType type) {
        list?.each {
            InternetAddress to = new InternetAddress(it)
            message.addRecipient(type, to)
        }
    }

    /**
     * 从字符串中获取邮箱列表。每一个邮箱之间可以使用一下字符分割：空格、逗号、中文句号、分号、括号、顿号等。
     * @param text 字符串。
     * @return 邮箱地址列表。
     */
    static List<String> findEmailStr(String text) {
        List<String> list = []

        def finder = text =~ EMAIL_REGEX
        finder.each {
            list << it
        }
        return list
    }
}
