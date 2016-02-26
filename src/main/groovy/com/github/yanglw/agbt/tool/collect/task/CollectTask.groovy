package com.github.yanglw.agbt.tool.collect.task

import com.github.yanglw.agbt.tool.collect.task.collector.Collector
import com.github.yanglw.agbt.tool.collect.output.info.BuildInfoOutputManager
import com.github.yanglw.agbt.util.ProjectUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * <p>
 * 收集生成物的 Task 。
 * </p>
 * Created by yanglw on 2015-12-8.
 */
class CollectTask extends DefaultTask {
    /** 标记是否执行了 collect 任务。 */
    public static boolean isRunCollectTask = false;

    Map info
    def currentVariant
    Collector collector

    CollectTask() {
        setGroup(ProjectUtil.GRADLE_TASK_GROUP)
    }

    /** 标记执行了自定义任务。 */
    @TaskAction
    void run() {
        isRunCollectTask = true
    }

    /** 复制生成物文件，例如 apk 、aar 文件。 */
    @TaskAction
    void collectOutputFile() {
        // 添加目标文件，例如 apk 、aar 文件。
        File outputFile = currentVariant.outputs[0].outputFile
        collector?.collectFile(currentVariant, outputFile, Collector.FILE_TYPE_OUTPUT, getInfo())
    }

    /** 复制最终的 AndroidManifest.xml 文件。 */
    @TaskAction
    void collectManifestFile() {
        // 最终的 AndroidManifest.xml 文件。
        File manifestFile = currentVariant.outputs[0].getProcessManifest().manifestOutputFile
        collector?.collectFile(currentVariant, manifestFile, Collector.FILE_TYPE_MANIFEST, getInfo())
    }

    /** 复制 Proguard 的输出 mapping 文件夹。 */
    @TaskAction
    void collectProguardOutputFolder() {
        // 有 proguard mapping 文件，说明进行了 proguard 操作，需要复制 mapping 文件。
        if (currentVariant.mappingFile) {
            // mapping 文件夹。
            File proguardOutputFolder = currentVariant.variantData.scope.getProguardOutputFolder()

            collector?.collectFile(currentVariant, proguardOutputFolder, Collector.FILE_TYPE_MAPPING, getInfo())
        }
    }

    /** 复制 Proguard 输出的 jar 文件 。 */
    @TaskAction
    void collectProguardOutputFile() {
        // proguard 输出的 jar 文件。
        // 有 proguard mapping 文件，说明进行了 proguard 操作，需要复制 mapping 文件。
        if (currentVariant.mappingFile) {
            // android gradle plugin 1.3 时， proguard 输出文件 variant.variantData.scope.getProguardOutputFile() 。
            // android gradle plugin 1.5 及以后，由于 transform api ，proguard 输出文件路径发生变化。
            File outJar = currentVariant.variantData.scope.getProguardOutputFile()
            if (outJar.exists()) {
                list?.each {
                    collector?.collectFile(currentVariant, outJar, Collector.FILE_TYPE_JAR, getInfo())
                }
            } else {
                // 查找 ProGuardTransform 。
                def proguardTransform = currentVariant.variantData.scope.transformManager.transforms.find {
                    it.name == "proguard"
                }
                if (proguardTransform) {
                    proguardTransform.configuration.programJars.classPathEntries.each { entry ->
                        if (entry.output) {
                            collector?.collectFile(currentVariant, (File) entry.file, Collector.FILE_TYPE_JAR,
                                                   getInfo())
                        }
                    }
                }
            }
        }
    }

    /** 复制 dex 文件。 */
    @TaskAction
    void collectDexFile() {
        if (ProjectUtil.getProjectType() == ProjectUtil.PROJECT_TYPE_APPLICATION) {
            try {
                // dex 文件。
                File dexFile = currentVariant.dex.outputFolder
                collector?.collectFile(currentVariant, dexFile, Collector.FILE_TYPE_DEX, getInfo())
            } catch (RuntimeException e) {
                logger.error("${ProjectUtil.LINE_SEPARATOR}get dex file err.${ProjectUtil.LINE_SEPARATOR}" + e)
                // def dexTransform = variant.variantData.scope.transformManager.transforms.find {
                //     it.name == "dex"
                // }
            }
        }
    }

    /** 获取当前 task 的信息内容对应的 Map 。 */
    Map getInfo() {
        if (info == null) {
            info = [:]
            info.put(Collector.INFO_KEY_VARIANT, currentVariant)
            info.put(Collector.INFO_KEY_GROUP_FLAG_FILE_INFO, '')
            BuildInfoOutputManager.getInstance().addInfo(info)
        }
        return info
    }
}
