package com.github.yanglw.agbt.tool.version.action

import com.github.yanglw.agbt.action.BuildAction
import com.github.yanglw.agbt.tool.version.extension.VersionExtension
import com.github.yanglw.agbt.tool.version.extension.VersionExtension.Version
import com.github.yanglw.agbt.util.FileUtil
import com.github.yanglw.agbt.variant.SimpleVariant
import com.github.yanglw.agbt.variant.VariantManager

/**
 * <p>
 * 修改生成物版本信息的动作。
 * </p>
 * Created by yanglw on 2015-12-24.
 */
class VersionAction implements BuildAction {
    VersionExtension ver

    /** 用户指定的版本信息配置文件对应的 Properties 对象。 */
    private Properties versionProps

    /** 执行了修改版本信息的 variant 列表。 */
    List<SimpleVariant> changeVerVariants = []

    VersionAction(VersionExtension extension) {
        ver = extension
    }

    @Override
    void onInit() {

    }

    Properties getVersionProps() {
        if (versionProps) {
            return versionProps
        }
        // 获取版本号。
        return versionProps = FileUtil.loadProperties(ver.verFilePath)
    }

    @Override
    void onVariantsAll(Object variant, int projectType) {
        if (ver.onSetVerClosure == null) {
            return
        }
        Version version = Version.newInstance()
        SimpleVariant obj = SimpleVariant.newInstance(variant)
        ver.onSetVerClosure.call(getVersionProps(), version, obj.buildType, obj.flavors)

        if (!version.verify()) {
            return
        }

        changeVerVariants << obj

        // 修改生成物的版本信息。
        // 修改 apk 版本号。
        variant.mergedFlavor.versionCode = version.versionCode
        // 修改 apk 版本名称。
        variant.mergedFlavor.versionName = version.versionName
    }

    @Override
    void onFinish() {
        saveVersion()
    }

    /** 存储版本信息。 */
    void saveVersion() {
        if (changeVerVariants) {
            List<SimpleVariant> list = changeVerVariants.findAll {
                VariantManager.getRunVariants().contains(it)
            }
            if (list) {
                // 升级版本号。
                if (ver.onSaveVerClosure) {
                    ver.onSaveVerClosure.call(getVersionProps(), list)
                }

                // 保存版本号。
                FileUtil.saveProperties(getVersionProps(), ver.verFilePath)
            }
        }
    }
}
