package com.github.yanglw.agbt.tool.collect.action

import com.github.yanglw.agbt.action.BuildAction
import com.github.yanglw.agbt.tool.collect.extension.CollectConfig
import com.github.yanglw.agbt.tool.collect.extension.CollectDefaultConfig
import com.github.yanglw.agbt.tool.collect.task.CollectTask
import com.github.yanglw.agbt.tool.collect.task.collector.Collector
import com.github.yanglw.agbt.tool.collect.task.collector.FileCopyCollector
import com.github.yanglw.agbt.util.ProjectUtil
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

/**
 * <p>
 * 收集动作，用于创建收集生成物 Task 。
 * </p>
 * Created by yanglw on 2015-12-24.
 */
class CollectAction implements BuildAction {
    final Project project

    final CollectDefaultConfig defaultConfig
    final NamedDomainObjectContainer<CollectConfig> configs

    CollectAction(Project project, CollectDefaultConfig defaultConfig, NamedDomainObjectContainer<CollectConfig> configs) {
        this.project = project
        this.defaultConfig = defaultConfig
        this.configs = configs
    }

    @Override
    void onInit() {
        if (defaultConfig.defaultCollectorEnable) {
            CollectConfig config = configs.findByName(FileCopyCollector.class.simpleName)
            if (config == null) {
                config = configs.create(FileCopyCollector.class.simpleName)
                config.collector = new FileCopyCollector(config)
            }
        }
    }

    @Override
    void onVariantsAll(Object variant, int projectType) {
        // all
        def allCollectTask = project.tasks.findByName("collect")
        if (allCollectTask == null) {
            allCollectTask = project.task("collect", group: ProjectUtil.GRADLE_TASK_GROUP)
        }
        Collector collector = getCollector()
        def task = project.task("collect${variant.name.capitalize()}",
                                type: CollectTask) {
            it.currentVariant = variant
            it.collector = collector
            dependsOn variant.assemble
        }
        allCollectTask.dependsOn task

        // add collect mergedFlavor Task
        String mergedFlavorName = variant.getProductFlavors().sum { it.name.capitalize() }
        String mergedFlavorTaskName = "collect$mergedFlavorName"
        def mergedFlavorTask = project.tasks.findByName(mergedFlavorTaskName)
        if (mergedFlavorTask == null) {
            mergedFlavorTask = project.task(mergedFlavorTaskName, group: ProjectUtil.GRADLE_TASK_GROUP)
        }
        mergedFlavorTask.dependsOn task

        // add collect buildType Task
        String buildTypeTaskName = "collect${variant.buildType.name.capitalize()}"
        def buildTypeTask = project.tasks.findByName(buildTypeTaskName)
        if (buildTypeTask == null) {
            buildTypeTask = project.task(buildTypeTaskName, group: ProjectUtil.GRADLE_TASK_GROUP)
        }
        buildTypeTask.dependsOn task

        // add collect productFlavors Task
        variant.getProductFlavors().each { flavor ->
            String productFlavorsTaskName = "collect${flavor.name.capitalize()}"
            def productFlavorTask = project.tasks.findByName(productFlavorsTaskName)
            if (productFlavorTask == null) {
                productFlavorTask = project.task(productFlavorsTaskName, group: ProjectUtil.GRADLE_TASK_GROUP)
            }
            productFlavorTask.dependsOn task
        }
    }

    @Override
    void onFinish() {
    }

    Collector getCollector() {
        return new Collector() {
            @Override
            void collectFile(Object variant, File file, int type, Map info) {
                configs.all {
                    it.getCollector()?.collectFile(variant, file, type, info)
                }
            }
        }
    }
}
