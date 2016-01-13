package com.github.yanglw.agbt.tool.collect.extension

import com.github.yanglw.agbt.tool.collect.task.collector.ChannelCollector
import com.github.yanglw.agbt.tool.collect.task.collector.Collector
import com.github.yanglw.agbt.tool.collect.task.collector.FileCopyCollector
import com.github.yanglw.agbt.tool.collect.task.collector.FileUploadCollector
import com.github.yanglw.agbt.util.ProjectUtil
import com.github.yanglw.agbt.util.VariantUtil
import groovy.text.SimpleTemplateEngine
import org.gradle.api.Project

import java.text.SimpleDateFormat

/**
 * <p>
 * 收集信息配置 Module 。
 * </p>
 * Created by yanglw on 2016-1-5.
 */
class CollectDefaultConfig {
    /**
     * 替换标记，用于表示 App 模块名称。
     * @see #outputFileMatcher
     * @see #outputFolderMatcher
     */
    public static final String TEMPLATE_ENGINE_APP_NAME = 'appName'
    /**
     * 替换标记，用于表示项目名称。
     * @see #outputFileMatcher
     * @see #outputFolderMatcher
     */
    public static final String TEMPLATE_ENGINE_PROJECT_NAME = 'projectName'
    /**
     * 替换标记，用于表示根项目名称。
     * @see #outputFileMatcher
     * @see #outputFolderMatcher
     */
    public static final String TEMPLATE_ENGINE_ROOT_PROJECT_NAME = 'rootProjectName'
    /**
     * 替换标记，用于表示根项目名称。
     * @see #outputFileMatcher
     * @see #outputFolderMatcher
     */
    public static final String TEMPLATE_ENGINE_APPLICATION_ID = 'applicationId'
    /**
     * 替换标记，用于表示 buildType 。
     * @see #outputFileMatcher
     * @see #outputFolderMatcher
     */
    public static final String TEMPLATE_ENGINE_BUILD_TYPE = 'buildType'
    /**
     * 替换标记，用于表示 flavors（支持 flavorDimensions） 。
     * @see #outputFileMatcher
     * @see #outputFolderMatcher
     */
    public static final String TEMPLATE_ENGINE_FLAVORS = 'flavors'
    /**
     * 替换标记，用于表示版本名称。
     * @see #outputFileMatcher
     * @see #outputFolderMatcher
     */
    public static final String TEMPLATE_ENGINE_VERSION_NAME = 'versionName'
    /**
     * 替换标记，用于表示版本号。
     * @see #outputFileMatcher
     * @see #outputFolderMatcher
     */
    public static final String TEMPLATE_ENGINE_VERSION_CODE = 'versionCode'
    /**
     * 替换标记，用于表示编译时间。
     * @see #outputFileMatcher
     * @see #outputFolderMatcher
     */
    public static final String TEMPLATE_ENGINE_TIME = 'time'
    /**
     * 替换标记，用于表示渠道名称。
     * @see #outputFileMatcher
     * @see #outputFolderMatcher
     * @see ChannelCollector
     */
    public static final String TEMPLATE_ENGINE_CHANNEL = 'channel'

    final Project project

    /** 仓库路径。 */
    String repRoot
    /** 应用程序名称。 */
    String appName
    /**
     * 当前项目指定 variant 生成物存放目录路径内容。
     * <ul>
     *     <li>appName ：App 模块名称</li>
     *     <li>projectName ：项目名称</li>
     *     <li>rootProjectName ：根项目名称</li>
     *     <li>applicationId ： applicationId</li>
     *     <li>buildType ： buildType</li>
     *     <li>flavors ： flavors（支持 flavorDimensions）</li>
     *     <li>versionName ：版本名称</li>
     *     <li>versionCode ：版本号</li>
     *     <li>time ：编译时间</li>
     * </ul>
     * @see #TEMPLATE_ENGINE_APP_NAME
     * @see #TEMPLATE_ENGINE_APP_NAME
     * @see #TEMPLATE_ENGINE_PROJECT_NAME
     * @see #TEMPLATE_ENGINE_ROOT_PROJECT_NAME
     * @see #TEMPLATE_ENGINE_APPLICATION_ID
     * @see #TEMPLATE_ENGINE_BUILD_TYPE
     * @see #TEMPLATE_ENGINE_FLAVORS
     * @see #TEMPLATE_ENGINE_VERSION_NAME
     * @see #TEMPLATE_ENGINE_VERSION_CODE
     * @see #TEMPLATE_ENGINE_TIME
     * @see #TEMPLATE_ENGINE_CHANNEL
     */
    String outputFolderMatcher
    /**
     * 当前项目生成物名称内容。
     * <ul>
     *     <li>appName ：App 模块名称</li>
     *     <li>projectName ：项目名称</li>
     *     <li>rootProjectName ：根项目名称</li>
     *     <li>applicationId ： applicationId</li>
     *     <li>buildType ： buildType</li>
     *     <li>flavors ： flavors（支持 flavorDimensions）</li>
     *     <li>versionName ：版本名称</li>
     *     <li>versionCode ：版本号</li>
     *     <li>time ：编译时间</li>
     * </ul>
     * @see #TEMPLATE_ENGINE_APP_NAME
     * @see #TEMPLATE_ENGINE_APP_NAME
     * @see #TEMPLATE_ENGINE_PROJECT_NAME
     * @see #TEMPLATE_ENGINE_ROOT_PROJECT_NAME
     * @see #TEMPLATE_ENGINE_APPLICATION_ID
     * @see #TEMPLATE_ENGINE_BUILD_TYPE
     * @see #TEMPLATE_ENGINE_FLAVORS
     * @see #TEMPLATE_ENGINE_VERSION_NAME
     * @see #TEMPLATE_ENGINE_VERSION_CODE
     * @see #TEMPLATE_ENGINE_TIME
     * @see #TEMPLATE_ENGINE_CHANNEL
     */
    String outputFileMatcher
    /** 项目仓库文件夹中时间的格式化字符串。 */
    String timeFormat
    /** 表示是否使用默认的 {@link Collector} 收集文件。 */
    boolean defaultCollectorEnable
    /** flavors 名称拼接者。 */
    Closure flavorsBuilder

    CollectDefaultConfig(Project project) {
        this.project = project
    }

    String getRepRoot() {
        return repRoot ?: project.projectDir.absolutePath
    }

    /** 获取用户设置的项目名称。 */
    String getAppName() {
        return appName ?: project.name
    }

    String getOutputFolderMatcher() {
        return outputFolderMatcher ?: '${time}-${versionName}-${versionCode}/${flavors}-${buildType}'
    }

    String getOutputFileMatcher() {
        return outputFileMatcher ?: '${appName}-${flavors}-${buildType}-${versionName}-${versionCode}'
    }

    String getTimeFormat() {
        return timeFormat ?: 'yyyyMMdd'
    }

    /** 获取当前的收集者。 */
    Collector getCollector() {
        return defaultCollectorEnable ? getFileCopyCollector() : null
    }

    /**
     * 获取默认的具有上传功能的收集者。
     * @param downloadUrlPrefix 下载链接前缀。
     * @return {@link FileUploadCollector} 对象。
     * @see FileUploadCollector
     */
    Collector getFileCopyCollector() {
        return new FileCopyCollector(this)
    }

    /**
     * 获取默认的具有上传功能的收集者。
     * @param downloadUrlPrefix 下载链接前缀。
     * @return {@link FileUploadCollector} 对象。
     * @see FileUploadCollector
     */
    Collector getFileUploadCollector(String downloadUrlPrefix) {
        return new FileUploadCollector(this, downloadUrlPrefix)
    }

    /**
     * 获取默认的具有打渠道包功能的收集者。
     * @param getChannelClosure 获取当前 variant 对象的闭包对象。将传递当前 variant 作为参数。
     * @return {@link ChannelCollector} 对象。
     * @see ChannelCollector
     */
    Collector getChannelCollector(Closure getChannelClosure) {
        return new ChannelCollector(this, getChannelClosure)
    }

    /** 获取当前 variant 目录对象。 */
    File getVariantOutputFolder(variant) {
        return getVariantOutputFolder(variant, null)
    }

    /** 获取当前 variant 目录对象。 */
    File getVariantOutputFolder(variant, Map<String, String> map) {
        File repRootFolder = new File(getRepRoot())
        File projectFolder = new File(repRootFolder, getAppName())
        File variantFolder = new File(projectFolder, templateEngine(variant, getOutputFolderMatcher(), map))
        return variantFolder
    }

    /** 获取当前 variant 目标文件的名称。 */
    String getOutputFileName(variant) {
        return getOutputFileName(variant, null)
    }

    /** 获取当前 variant 目标文件的名称。 */
    String getOutputFileName(variant, Map<String, String> map) {
        String name = templateEngine(variant, getOutputFileMatcher(), map)
        // 标记 apk 是否进行了签名。
        if (ProjectUtil.getProjectType() == ProjectUtil.PROJECT_TYPE_APPLICATION) {
            if (!VariantUtil.isSigningReady(variant)) {
                name = "$name${ProjectUtil.SEPARATOR}unsigned"
            }
        }
        return name
    }

    protected String templateEngine(variant, String matcher, Map<String, String> map) {
        def nameMap = [(TEMPLATE_ENGINE_APP_NAME)         : getAppName(),
                       (TEMPLATE_ENGINE_PROJECT_NAME)     : project.name,
                       (TEMPLATE_ENGINE_ROOT_PROJECT_NAME): project.rootProject.name,
                       (TEMPLATE_ENGINE_APPLICATION_ID)   : variant.applicationId,
                       (TEMPLATE_ENGINE_BUILD_TYPE)       : variant.buildType.name,
                       (TEMPLATE_ENGINE_VERSION_NAME)     : variant.versionName,
                       (TEMPLATE_ENGINE_VERSION_CODE)     : variant.versionCode,
                       (TEMPLATE_ENGINE_TIME)             : getTime()]
        if (map) {
            nameMap.putAll(map)
        }

        // 当前 variants 的 productFlavors 。
        if (variant.getProductFlavors()) {
            String flavors = getFlavorsBuilder()?.call(VariantUtil.getProductFlavorNameList(variant))
            nameMap.put(TEMPLATE_ENGINE_FLAVORS, flavors ?: '')
        } else {
            matcher = matcher.replace("\${$TEMPLATE_ENGINE_FLAVORS}", '')
        }

        // 渠道信息
        if (matcher.contains("\${$TEMPLATE_ENGINE_CHANNEL}")) {
            if (!nameMap.containsKey(TEMPLATE_ENGINE_CHANNEL)) {
                matcher.replace("\${$TEMPLATE_ENGINE_CHANNEL}", '')
            }
        }
        nameMap.each {
            if (it.value == null) {
                it.value = ''
            }
        }
        return new SimpleTemplateEngine().createTemplate(matcher).make(nameMap).toString()
    }

    /** 获取当前时间的格式化字符串。 */
    protected String getTime() {
        return new SimpleDateFormat(getTimeFormat()).format(ProjectUtil.getBuildTime())
    }
}
