package com.github.yanglw.agbt.tool.collect.extension

import com.github.yanglw.agbt.tool.collect.task.collector.Collector
import org.gradle.api.Project

/**
 * <p>
 * 收集信息配置 Module 。
 * </p>
 *
 * Created by yanglw on 2016-1-5.
 */
class CollectConfig extends CollectDefaultConfig {
    final String name

    CollectDefaultConfig config

    /** 仓库路径。 */
    String repRoot
    /** 应用程序名称。 */
    String appName
    /**
     * 当前项目指定 variant 生成物存放目录路径内容。
     * <ul>
     *     <li>appName ：App 模块名字</li>
     *     <li>projectName ：项目名字</li>
     *     <li>rootProjectName ：根项目名字</li>
     *     <li>applicationId ： applicationId</li>
     *     <li>buildType ： buildType</li>
     *     <li>flavors ： flavors（支持 flavorDimensions）</li>
     *     <li>versionName ：版本名称</li>
     *     <li>versionCode ：版本号</li>
     *     <li>time ：编译时间</li>
     * </ul>
     */
    String outputFolderMatcher
    /**
     * 当前项目生成物名称内容。
     * <ul>
     *     <li>appName ：App 模块名字</li>
     *     <li>projectName ：项目名字</li>
     *     <li>rootProjectName ：根项目名字</li>
     *     <li>applicationId ： applicationId</li>
     *     <li>buildType ： buildType</li>
     *     <li>flavors ： flavors（支持 flavorDimensions）</li>
     *     <li>versionName ：版本名称</li>
     *     <li>versionCode ：版本号</li>
     *     <li>time ：编译时间</li>
     * </ul>
     */
    String outputFileMatcher
    /** 项目仓库文件夹中时间的格式化字符串。 */
    String timeFormat
    /** 收集者。 */
    Collector collector
    /** flavors 名称拼接者。 */
    Closure flavorsBuilder

    CollectConfig(String name, Project project, CollectDefaultConfig config) {
        super(project)
        this.name = name
        this.config = config
    }

    /** 获取输出仓库目录对象。 */
    @Override
    String getRepRoot() {
        return repRoot ?: config.getRepRoot()
    }

    /** 获取用户设置的项目名称。 */
    @Override
    String getAppName() {
        return appName ?: config.getAppName()
    }

    @Override
    String getTimeFormat() {
        return timeFormat ?: config.getTimeFormat()
    }

    @Override
    String getOutputFolderMatcher() {
        return outputFolderMatcher ?: config.getOutputFolderMatcher()
    }

    @Override
    String getOutputFileMatcher() {
        return outputFileMatcher ?: config.getOutputFileMatcher()
    }

    @Override
    Collector getCollector() {
        return collector
    }

    @Override
    Closure getFlavorsBuilder() {
        return flavorsBuilder ?: config.getFlavorsBuilder()
    }
}
