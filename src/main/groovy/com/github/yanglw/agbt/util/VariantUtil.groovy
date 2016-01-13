package com.github.yanglw.agbt.util

/**
 * <p>
 * Variant 工具类。
 * </p>
 *
 * Created by yanglw on 2016-1-5.
 */
class VariantUtil {
    /** 获取当前 variant 的 buildType 的名称。 */
    static String getBuildTypeName(def variant) {
        return variant.buildType.name
    }

    /** 获取当前 variant 的 flavors 的名称列表。 */
    static List<String> getProductFlavorNameList(def variant) {
        List<String> list = []
        def flavors = variant.getProductFlavors()
        flavors?.each {
            list << it.name
        }
        return list
    }

    /** 获取当前 variant 是否已经签名。 */
    static boolean isSigningReady(def variant) {
        return variant.isSigningReady()
    }

    /** 获取当前 variant 的 applicationId。 */
    static String getApplicationId(def variant) {
        variant.mergedFlavor.applicationId
    }

    /** 获取当前 variant 的 versionName。 */
    static String getVersionName(def variant) {
        variant.mergedFlavor.versionName
    }

    /** 获取当前 variant 的 versionCode。 */
    static int getVersionCode(def variant) {
        variant.mergedFlavor.versionCode
    }

    /** 获取当前 variant 的 debuggable 是否开启。 */
    static boolean getDebuggable(def variant) {
        return variant.buildType.debuggable
    }
}
