package com.github.yanglw.agbt.tool.output.vcs

import com.github.yanglw.agbt.util.ShellUtil

/**
 * <p>
 * 匹配 svn 的 {@link Vcs} 。
 * </p>
 * Created by yanglw on 2015-12-22.
 */
class SVNDelegate implements Vcs {
    File svnDir
    String userName
    String password
    String remoteUrl
    String commitCode

    @Override
    boolean check(File projectDir, File rootDir) {
        try {
            ShellUtil.execute('svn', '--version')
        } catch (ignore) {
            return false
        }

        svnDir = [projectDir, rootDir].find {
            // svn info --xml
            ShellUtil.ShellValue value = ShellUtil.execute(it, 'svn', 'info', '--xml')
            if (value.exitValue == 0) {
                def info = new XmlSlurper().parseText(value.text)
                remoteUrl = info.entry.url.text()
                commitCode = info.entry.commit.@revision.text()
                return true
            }
        }
        svnDir ? true : false
    }

    @Override
    void setAuth(String userName, String password) {
        this.userName = userName
        this.password = password
    }

    @Override
    String getName() {
        return 'svn'
    }

    @Override
    String getRemoteUrl() {
        // svn info --xml
        return remoteUrl
    }

    @Override
    String getCommitCode() {
        // svn info --xml
        return commitCode
    }

    @Override
    String getLog(String lastBuildCommitCode) {
        // svn log -l 5 --xml --username userName --password password
        // svn log -r 77740:77694 --xml --username userName --password password
        List<String> command = ['svn', 'log']
        String currentCommitCOde = getCommitCode()
        if (lastBuildCommitCode && currentCommitCOde && currentCommitCOde != lastBuildCommitCode) {
            command << '-r'
            command << "$lastBuildCommitCode:$currentCommitCOde}"
        } else {
            command << '-l'
            command << '1'
        }
        if (userName) {
            command << '--username'
            command << userName
        }
        if (password) {
            command << '--password'
            command << password
        }
        def execute = ShellUtil.execute(svnDir, command)
        if (execute.exitValue == 0) {
            return execute.text
        }
        return '获取日志信息失败。'
    }
}
