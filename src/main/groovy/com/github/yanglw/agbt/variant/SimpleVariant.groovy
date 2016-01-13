package com.github.yanglw.agbt.variant

import com.github.yanglw.agbt.util.VariantUtil

/**
 * <p>
 * variant 简单信息 Model 。
 * </p>
 * Created by yanglw on 2015-12-10.
 */
class SimpleVariant {
    /**当前 variant 的 buildType 名称。*/
    String buildType
    /**当前 variant 的 productFlavors 名称列表。*/
    List<String> flavors = []

    public static SimpleVariant newInstance(variant) {
        SimpleVariant obj = new SimpleVariant()
        obj.buildType = VariantUtil.getBuildTypeName(variant)
        obj.flavors = VariantUtil.getProductFlavorNameList(variant)
        return obj;
    }

    boolean equals(other) {
        if (other == null) {
            return false
        }

        if (this.is(other)) {
            return true
        }
        if (getClass() != other.class) {
            return false
        }

        SimpleVariant that = (SimpleVariant) other

        if (buildType != that.buildType) {
            return false
        }
        if (flavors != that.flavors) {
            return false
        }

        return true
    }

    int hashCode() {
        int result
        result = buildType.hashCode()
        result = 31 * result + flavors.hashCode()
        return result
    }

    @Override
    public String toString() {
        return "[buildType='$buildType', productFlavors=$flavors]";
    }
}
