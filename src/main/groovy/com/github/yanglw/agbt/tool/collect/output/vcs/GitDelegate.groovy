package com.github.yanglw.agbt.tool.collect.output.vcs

import com.github.yanglw.agbt.util.ShellUtil

/**
 * <p>
 * 匹配 git 的 {@link Vcs} 。
 * </p>
 * Created by yanglw on 2015-12-22.
 */
class GitDelegate implements Vcs {
    File vcsDir
    String userName
    String pwd
    String remoteUrl
    String commitHash

    @Override
    boolean check(File projectDir, File rootDir) {
        try {
            ShellUtil.execute('git', '--version')
        } catch (ignore) {
            return false
        }

        vcsDir = [projectDir, rootDir].find {
            ShellUtil.ShellValue value = ShellUtil.execute(it, 'git', 'rev-parse')
            return value.exitValue == 0
        }
        vcsDir ? true : false
    }

    @Override
    void setAuth(String userName, String password) {
        this.userName = userName
        this.pwd = password
    }

    @Override
    String getName() {
        return 'git'
    }

    @Override
    String getRemoteUrl() {
        if (remoteUrl) {
            return remoteUrl
        }
        // git ls-remote --get-url
        def value = ShellUtil.execute(vcsDir, 'git', 'ls-remote', '--get-url')
        if (value.exitValue == 0) {
            return remoteUrl = value.text
        } else {
            println '获取远程仓库地址失败'
            println value.text
            return ''
        }
    }

    @Override
    String getCommitCode() {
        if (commitHash) {
            return commitHash
        }
        // git rev-parse --verify HEAD
        def value = ShellUtil.execute(vcsDir, 'git', 'rev-parse', '--verify', 'HEAD')
        if (value.exitValue == 0) {
            return commitHash = value.text
        } else {
            println '获取 commit hash失败'
            println value.text
            return ''
        }
    }

    @Override
    String getLog(String lastBuildCommitCode) {
        // git log -5 bbc85bf...0c82e51 --topo-order --pretty=format:"------------------------------------------------------------------------%n%h | %an | %ad%n%n%s%n    %b%n" --date=format:"%Y-%m-%d %H:%M:%S"
        List<String> command = ['git', 'log']
        String currentCommitCode = getCommitCode()
        if (lastBuildCommitCode && currentCommitCode && lastBuildCommitCode != currentCommitCode) {
            if (lastBuildCommitCode.size() > 7) {
                lastBuildCommitCode = lastBuildCommitCode.substring(0, 7)
            }
            if (currentCommitCode.size() > 7) {
                currentCommitCode = currentCommitCode.substring(0, 7)
            }
            command << "$lastBuildCommitCode...$currentCommitCode"
        } else {
            command << '-1'
        }
        command << '--topo-order'
        command << '--pretty=format:"------------------------------------------------------------------------%n%h%x20|%x20%an%x20|%x20%ad%n%n%s%n%b"'
        command << '--date=format:"%Y-%m-%d'
        command << '%H:%M:%S"'
        def execute = ShellUtil.execute(vcsDir, command)
        if (execute.exitValue == 0) {
            return execute.text
        } else {
            println '获取日志信息失败'
            println execute.text
            println execute.exitValue
        }
        return '获取日志信息失败。'
    }
}
