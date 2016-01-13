package com.github.yanglw.agbt

import com.github.yanglw.agbt.action.BuildActionGroup
import com.github.yanglw.agbt.extension.ToolPluginExtension
import com.github.yanglw.agbt.tool.channel.task.ChannelSingleApkTask
import com.github.yanglw.agbt.util.ProjectUtil
import com.github.yanglw.agbt.variant.SimpleVariant
import com.github.yanglw.agbt.variant.VariantManager
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by yanglw on 2015-11-23.
 */
class ToolPlugin implements Plugin<Project> {
    BuildActionGroup actions

    @Override
    void apply(Project project) {
        project.task('channelSingleApk', type: ChannelSingleApkTask)

        actions = new BuildActionGroup()

        project.extensions.create("tool", ToolPluginExtension, project, actions)

        project.afterEvaluate {
            ProjectUtil.init(project)

            int projectType = ProjectUtil.getProjectType()

            actions.onInit()

            Closure closure = { variant ->
                variant.assemble.doFirst {
                    VariantManager.add(SimpleVariant.newInstance(variant))
                }

                actions.onVariantsAll(variant, projectType)
            }
            if (projectType == ProjectUtil.PROJECT_TYPE_APPLICATION) {
                project.android.applicationVariants.all(closure)
            } else {
                if (projectType == ProjectUtil.PROJECT_TYPE_LIBRARY) {
                    project.android.libraryVariants.all(closure)
                }
            }
        }

        project.gradle.buildFinished {
            if (it.failure == null && VariantManager.getRunVariants()) {
                actions.onFinish()
            }
        }
    }
}

