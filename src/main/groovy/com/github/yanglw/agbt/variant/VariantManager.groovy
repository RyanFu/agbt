package com.github.yanglw.agbt.variant

/**
 * <p>
 * 用于统计执行了 assemble task 的 variant 的管理类。
 * </p>
 * Created by yanglw on 2015-12-10.
 */
class VariantManager {
    /** 执行了 assemble task 的 variant 列表。 */
    public static final List<SimpleVariant> RUN_VARIANT_LIST = []

    /** 添加一个 执行了 assemble task 的 variant 。 */
    static void add(SimpleVariant variant) {
        RUN_VARIANT_LIST << variant
    }

    /** 获取执行了 assemble task 的 variant 列表。 */
    static List<SimpleVariant> getRunVariants() {
        return RUN_VARIANT_LIST
    }
}
