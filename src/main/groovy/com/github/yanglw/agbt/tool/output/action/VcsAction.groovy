package com.github.yanglw.agbt.tool.output.action

import com.github.yanglw.agbt.action.BuildAction
import com.github.yanglw.agbt.tool.collect.task.collector.Collector
import com.github.yanglw.agbt.tool.output.extension.VcsExtension
import com.github.yanglw.agbt.tool.output.info.BuildInfoOutputManager
import com.github.yanglw.agbt.tool.output.vcs.Vcs
import com.github.yanglw.agbt.util.VcsUtil
import org.gradle.api.Project

/**
 * <p>
 * 处理项目 VCS 信息的动作。
 * </p>
 * Created by yanglw on 2015-12-24.
 */
class VcsAction implements BuildAction {
    Project project
    VcsExtension vcs

    Vcs vcsDelegate

    VcsAction(Project project, VcsExtension vcs) {
        this.project = project
        this.vcs = vcs
    }

    @Override
    void onInit() {
        vcsDelegate = VcsUtil.check(project, vcs)
        if (vcsDelegate == null) {
            return
        }
        if (vcs.lastCodeSavePath) {
            // 获取上一次编译的版本信息。
            File vcsFile = new File(vcs.lastCodeSavePath)
            String lastCommitCode = null
            if (vcsFile.exists()) {
                lastCommitCode = vcsFile.text
            }
            Map map = [:]
            BuildInfoOutputManager.getInstance().addInfo(map)
            map.put(Collector.INFO_KEY_GROUP_FLAG_VCS_INFO, vcsDelegate.getName())
            map.put(Collector.INFO_KEY_VCS_COMMIT_CODE, vcsDelegate.getCommitCode())
            map.put(Collector.INFO_KEY_VCS_REMOTE_URL, vcsDelegate.getRemoteUrl())
            map.put(Collector.INFO_KEY_VCS_LOG, vcsDelegate.getLog(lastCommitCode))
        }
    }

    @Override
    void onVariantsAll(Object variant, int projectType) {
    }

    @Override
    void onFinish() {
        // 存储本次编译的版本信息。
        if (vcsDelegate != null && vcs.lastCodeSavePath) {
            new File(vcs.lastCodeSavePath).write(vcsDelegate.getCommitCode())
        }
    }
}
