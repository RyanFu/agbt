package com.github.yanglw.agbt.action

/**
 * <p>
 * BuildAction 组，一次性执行组中的 BuildAction 事件。
 * </p>
 * Created by yanglw on 2015-12-24.
 */
class BuildActionGroup implements BuildAction {
    List<BuildAction> list = []

    @Override
    void onInit() {
        list.each {
            it.onInit()
        }
    }

    @Override
    void onVariantsAll(Object variant, int projectType) {
        list.each {
            it.onVariantsAll(variant, projectType)
        }
    }

    @Override
    void onFinish() {
        list.each {
            it.onFinish()
        }
    }

    void add(BuildAction action) {
        list << action
    }

    BuildActionGroup leftShift(BuildAction action) {
        add(action)
        return this
    }
}
