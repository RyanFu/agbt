package com.github.yanglw.agbt.tool.collect.extension

import com.github.yanglw.agbt.action.BuildActionGroup
import com.github.yanglw.agbt.tool.collect.action.CollectAction
import com.github.yanglw.agbt.tool.collect.output.extension.WriterExtension
import org.gradle.api.Project

/**
 * <p>
 * 收集生成物工具。
 * </p>
 * Created by yanglw on 2015-12-4.
 */
class BeeExtension {
    Project project

    BeeExtension(Project project, BuildActionGroup group) {
        this.project = project
        // defaultConfig
        CollectDefaultConfig defaultConfig = extensions.create('defaultConfig', CollectDefaultConfig, project)
        // configs
        def configs = project.container(CollectConfig) { String name ->
            return new CollectConfig(name, project, defaultConfig)
        }
        extensions.configs = configs
        CollectAction collectAction = new CollectAction(project, defaultConfig, configs)
        group.add(collectAction)

        extensions.create("writer", WriterExtension, project, group)
    }
}
