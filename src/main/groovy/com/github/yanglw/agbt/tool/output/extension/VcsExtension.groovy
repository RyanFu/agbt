package com.github.yanglw.agbt.tool.output.extension

import com.github.yanglw.agbt.action.BuildActionGroup
import com.github.yanglw.agbt.tool.output.action.VcsAction
import org.gradle.api.Project

/**
 * <p>
 * 关于版本控制系统的设置内容 。
 * </p>
 * Created by yanglw on 2015-11-23.
 */
class VcsExtension {
    String lastCodeSavePath
    String userName
    String password

    VcsExtension(Project project, BuildActionGroup group) {
        group << new VcsAction(project, this)
    }
}
