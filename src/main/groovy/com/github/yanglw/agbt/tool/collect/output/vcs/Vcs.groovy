package com.github.yanglw.agbt.tool.collect.output.vcs

/**
 * <p>
 * 项目版本控制系统信息处理者。
 * </p>
 * Created by yanglw on 2015-12-22.
 */
interface Vcs {
    /** 判断是否能处理当前项目的版本控制系统。 */
    boolean check(File projectDir, File rootDir)

    /** 设置用户认证信息。目前只支持用户名和密码的方式。 */
    void setAuth(String userName, String password)

    /** 版本控制系统的名称。 */
    String getName()

    /** 获取当前项目的服务器地址。 */
    String getRemoteUrl()

    /** 获取项目当前的提交 Id ，用于专一性识别某一次提交。 */
    String getCommitCode()

    /** 获取本次编译与上一次编译之间的项目更新日志。 */
    String getLog(String lastBuildCommitCode)
}