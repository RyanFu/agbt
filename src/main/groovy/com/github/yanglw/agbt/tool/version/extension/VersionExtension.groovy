package com.github.yanglw.agbt.tool.version.extension

import com.github.yanglw.agbt.action.BuildActionGroup
import com.github.yanglw.agbt.tool.version.action.VersionAction
import org.gradle.api.Project

/**
 * <p>
 * 版本号控制工具。
 * </p>
 * Created by yanglw on 2015-11-23.
 */
class VersionExtension {
    Project project
    BuildActionGroup group

    /** 用户指定的版本信息配置文件。 */
    String verFilePath
    /** 从 {@link VersionAction#versionProps} 中获取版本信息的回调闭包。 */
    Closure onSaveVerClosure
    /** 将 {@link VersionAction#versionProps} 中的信息存储到文件之前的回调闭包，用于让用户修改版本信息。 */
    Closure onSetVerClosure

    VersionExtension(Project project, BuildActionGroup group) {
        this.project = project
        this.group = group
        group << new VersionAction(this)
    }

    /**
     * 获取当前 variant 的版本信息。如果修改后的 Version 无意义，不再修改版本信息。
     * @param closure
     *          <ul>
     *              <li>第一个参数，类型： {@link Properties} ，指定的配置文件对应的 Properties 对象。</li>
     *              <li>第而个参数，类型： {@link Version} ，通过设置该对象的 versionCode 和 versionName 字段实现控制更改版本号。Version 信息如下：</li>
     *              <ul>
     *                  <li>字段名称： versionName ；类型： {@link String} ；含义： 版本名称。</li>
     *                  <li>字段名称： versionCode ；类型： int ；含义： 版本号。</li>
     *              </ul>
     *              <li>第三个参数，类型： {@link String} ，当前 variant 的 buildType 名称。</li>
     *              <li>第四个参数，类型： {@link List}&#60;String&#62; ，当前 variant 的 productFlavors 名称列表。</li>
     *          </ul>
     */
    void onSetVer(Closure closure) {
        onSetVerClosure = closure
    }

    /**
     * 修改 Properties 中的版本信息。修改后的 Properties 信息将会进行存储。
     * @param closure
     *          <ul>
     *              <li>第一个参数，类型：{@link Properties} ，指定的配置文件对应的 Properties 对象。</li>
     *              <li>第二个参数，类型：{@link List}&#60;SimpleVariant&#62; ，执行了 assemble task 同时修改了版本信息的 variant 列表。SimpleVariant 信息如下：</li>
     *              <ul>
     *                  <li>字段名称： buildType ；类型： {@link String} ；含义： variant 的 buildType 名称。</li>
     *                  <li>字段名称： flavors ；类型： {@link List}&#60;String&#62; ；含义： variant 的 productFlavors 名称列表。</li>
     *              </ul>
     *          </ul>
     */
    void onSaveVer(Closure closure) {
        onSaveVerClosure = closure
    }

    /** 用于表示版本信息的 Model  。*/
    public static class Version {
        /** 版本名称高。 */
        String versionName
        /** 版本号。 */
        int versionCode

        /**
         * 判断当前的 Version 对象是否有意义。
         * @return true ：有意义； false ：无意义。
         */
        boolean verify() {
            if (versionCode <= 0) {
                return false
            }
            if (versionName == null || versionName.size() == 0) {
                return false
            }
            return true
        }

        public static Version newInstance() {
            return new Version();
        }
    }
}
