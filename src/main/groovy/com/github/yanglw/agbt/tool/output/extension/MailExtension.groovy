package com.github.yanglw.agbt.tool.output.extension

import com.github.yanglw.agbt.util.FileUtil
import com.github.yanglw.agbt.util.MailUtil
import com.github.yanglw.agbt.util.ProjectUtil
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project

import java.text.SimpleDateFormat

/**
 * <p>
 * 邮件内容载体。
 * </p>
 * Created by yanglw on 2015-12-11.
 */
class MailExtension {
    /** 默认邮件发送协议。 */
    private static final String DEFAULT_PROTOCOL = "smtp"
    /** 本地邮件发送服务器配置中服务器地址字段名称。 */
    private static final String MAIL_PROPERTY_KEY_HOST = "mail.host"
    /** 本地邮件发送服务器配置中服务器端口号字段名称。 */
    private static final String MAIL_PROPERTY_KEY_PORT = "mail.port"
    /** 本地邮件发送服务器配置中标记服务器是否需要认证字段名称。 */
    private static final String MAIL_PROPERTY_KEY_AUTH = "mail.auth"
    /** 本地邮件发送服务器配置中账号名称字段名称。 */
    private static final String MAIL_PROPERTY_KEY_USER = "mail.user"
    /** 本地邮件发送服务器配置中账号密码字段名称。 */
    private static final String MAIL_PROPERTY_KEY_PASSWORD = "mail.password"
    /** 本地邮件发送服务器配置中收件人地址字段名称。 */
    private static final String MAIL_PROPERTY_KEY_TO = "mail.to"
    /** 本地邮件发送服务器配置中抄送列表字段名称。 */
    private static final String MAIL_PROPERTY_KEY_CC = "mail.cc"
    /** 本地邮件发送服务器配置中秘密抄送列表字段名称。 */
    private static final String MAIL_PROPERTY_KEY_BCC = "mail.bcc"

    final Project project

    String appName
    String timeFormat

    private Properties mailPros
    /** 邮件编码。 */
    String charset = 'utf-8'
    /** 发送邮件所需的邮箱配置文件。 */
    String mailProsPath
    /** 邮箱服务器是否需要认证，如果需要认证，则需要设置 {@link #password} 。 */
    boolean auth
    /** 邮箱服务器地址。 */
    String host
    /** 邮箱服务器端口号。 */
    int port
    /** 发送邮件的邮箱地址。 */
    String user
    /** 发送邮件的邮箱密码。 */
    String password
    /** 发送邮件的邮箱称呼。 */
    String personal
    /**
     * 邮件主题的匹配字符串。
     * <ul>
     *     <li>appName ：App 模块名字</li>
     *     <li>projectName ：项目名字</li>
     *     <li>rootProjectName ：根项目名字</li>
     *     <li>time ：编译时间</li>
     * </ul>
     */
    String subjectMather
    /**
     * 收件人列表，每一个邮箱之间可以使用一下字符分割：空格、逗号、中文句号、分号、括号、顿号等。
     * @see MailUtil#EMAIL_REGEX
     * @see MailUtil#findEmailStr(String)
     */
    String to
    /**
     * 抄送列表，每一个邮箱之间可以使用一下字符分割：空格、逗号、中文句号、分号、括号、顿号等。
     * @see MailUtil#EMAIL_REGEX
     * @see MailUtil#findEmailStr(String)
     */
    String cc
    /**
     * 秘密抄送列表，每一个邮箱之间可以使用一下字符分割：空格、逗号、中文句号、分号、括号、顿号等。
     * @see MailUtil#EMAIL_REGEX
     * @see MailUtil#findEmailStr(String)
     */
    String bcc

    MailExtension(Project project) {
        this.project = project
    }

    Properties toProperties() {
        mailPros.setProperty("mail.transport.protocol", DEFAULT_PROTOCOL)
        return mailPros
    }

    /** 验证当前邮件配置是否符合发邮件的要求。 */
    boolean verify() {
        loadData()
        // 没有服务器地址，不符合要求。
        if (!getHost()) {
            return false
        }
        // 没有发件人，不符合要求。
        if (!getUser()) {
            return false
        }
        // 服务器需要认证，但是没有密码，不符合要求。
        if (getAuth() && !getPassword()) {
            return false
        }
        // 没有收件人，且没有抄送人，且没有秘密抄送人，不符合要求。
        if (!getCcList() && !getToList() && !getBccList()) {
            return false
        }
        return true
    }

    private void loadData() {
        if (mailProsPath) {
            mailPros = FileUtil.loadProperties(mailProsPath)
        } else {
            mailPros = new Properties()
        }

        if (host) {
            mailPros.setProperty(MAIL_PROPERTY_KEY_HOST, host)
        }
        if (port) {
            mailPros.setProperty(MAIL_PROPERTY_KEY_PORT, String.valueOf(port))
        }
        if (auth) {
            mailPros.setProperty(MAIL_PROPERTY_KEY_AUTH, String.valueOf(auth))
        }
        if (user) {
            mailPros.setProperty(MAIL_PROPERTY_KEY_USER, user)
        }
        if (password) {
            mailPros.setProperty(MAIL_PROPERTY_KEY_PASSWORD, password)
        }

        if (to) {
            mailPros.setProperty(MAIL_PROPERTY_KEY_TO, to)
        }

        if (cc) {
            mailPros.setProperty(MAIL_PROPERTY_KEY_CC, cc)
        }

        if (bcc) {
            mailPros.setProperty(MAIL_PROPERTY_KEY_BCC, bcc)
        }
    }

    boolean getAuth() {
        return Boolean.parseBoolean(mailPros.getProperty(MAIL_PROPERTY_KEY_AUTH))
    }

    String getHost() {
        return mailPros.getProperty(MAIL_PROPERTY_KEY_HOST)
    }

    int getPort() {
        return Integer.getInteger(mailPros.getProperty(MAIL_PROPERTY_KEY_PORT), 0)
    }

    String getUser() {
        return mailPros.getProperty(MAIL_PROPERTY_KEY_USER)
    }

    String getPassword() {
        return mailPros.getProperty(MAIL_PROPERTY_KEY_PASSWORD)
    }

    private String getTo() {
        return mailPros.getProperty(MAIL_PROPERTY_KEY_TO)
    }

    private String getCc() {
        return mailPros.getProperty(MAIL_PROPERTY_KEY_CC)
    }

    private String getBcc() {
        return mailPros.getProperty(MAIL_PROPERTY_KEY_BCC)
    }

    /** 获取邮件主题。 */
    private String getSubjectMather() {
        subjectMather ?: '项目 ${appName} 发布（时间: ${time}）'
    }

    List<String> getToList() {
        String to = getTo()
        if (to) {
            return MailUtil.findEmailStr(to)
        }
        return null
    }

    List<String> getBccList() {
        String bcc = getBcc()
        if (bcc) {
            return MailUtil.findEmailStr(bcc)
        }
        return null
    }

    List<String> getCcList() {
        String cc = getCc()
        if (cc) {
            return MailUtil.findEmailStr(cc)
        }
        return null
    }

    String getSubject() {
        Map keys = ['appName'        : getAppName(),
                    'projectName'    : project.name,
                    'rootProjectName': project.rootProject.name,
                    'time'           : getTime()]
        return new SimpleTemplateEngine().createTemplate(getSubjectMather()).make(keys).toString()
    }

    String getTimeFormat() {
        return timeFormat ?: 'yyyyMMdd'
    }

    /** 获取用户设置的项目名称。 */
    String getAppName() {
        return appName ?: project.name
    }

    /** 获取当前时间的格式化字符串。 */
    protected String getTime() {
        return new SimpleDateFormat(getTimeFormat()).format(ProjectUtil.getBuildTime())
    }
}
