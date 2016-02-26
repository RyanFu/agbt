package com.github.yanglw.agbt.tool.collect.output.action

import com.github.yanglw.agbt.action.BuildAction
import com.github.yanglw.agbt.tool.collect.task.CollectTask
import com.github.yanglw.agbt.tool.collect.output.extension.WriterExtension
import com.github.yanglw.agbt.tool.collect.output.info.BuildInfoOutputManager

/**
 * <p>
 * 输出信息的动作。
 * </p>
 * Created by yanglw on 2015-12-24.
 */
class OutputAction implements BuildAction {
    private WriterExtension writer

    OutputAction(WriterExtension writer) {
        this.writer = writer
    }

    @Override
    void onInit() {
    }

    @Override
    void onVariantsAll(Object variant, int projectType) {
    }

    @Override
    void onFinish() {
        if (CollectTask.isRunCollectTask) {
            def allInfo = BuildInfoOutputManager.getInstance().getAllInfo()
            if (allInfo == null) {
                return
            }
            writer.getOutput().output(allInfo)
        }
    }
}
