package com.github.yanglw.agbt.extension

import com.github.yanglw.agbt.action.BuildActionGroup
import com.github.yanglw.agbt.tool.collect.extension.BeeExtension
import com.github.yanglw.agbt.tool.output.extension.WriterExtension
import com.github.yanglw.agbt.tool.version.extension.VersionExtension
import org.gradle.api.Project

/**
 * Created by yanglw on 2015-11-23.
 */
class ToolPluginExtension {
    final Project project
    final BuildActionGroup actions

    /** 文件收集。 */
    final BeeExtension bee
    /** apk 版本控制。 */
    final VersionExtension ver
    /** 信息输出。 */
    final WriterExtension writer

    ToolPluginExtension(Project project, BuildActionGroup actions) {
        this.project = project
        this.actions = actions

        this.bee = extensions.create("bee", BeeExtension, project, actions)
        this.ver = extensions.create("ver", VersionExtension, project, actions)
        this.writer = extensions.create("writer", WriterExtension, project, actions)
    }
}
