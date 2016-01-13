package com.github.yanglw.agbt.util

import com.github.yanglw.agbt.tool.output.extension.VcsExtension
import com.github.yanglw.agbt.tool.output.vcs.GitDelegate
import com.github.yanglw.agbt.tool.output.vcs.SVNDelegate
import com.github.yanglw.agbt.tool.output.vcs.Vcs
import org.gradle.api.Project

/**
 * <p>
 * VCS 工具类。
 * </p>
 * Created by yanglw on 2015-12-24.
 */
class VcsUtil {
    /**
     * 检查当前项目的 VCS 类型。
     * @param project 当前项目。
     * @param vcs vcs 设置信息。
     * @return 能够处理当前项目 VCS 的 {@link Vcs} 对象。
     */
    static Vcs check(Project project, VcsExtension vcs) {
        return [new GitDelegate(), new SVNDelegate()].find { delegate ->
            delegate.setAuth(vcs.userName, vcs.password)
            delegate.check(project.getProjectDir(), project.getRootDir())
        }
    }
}
