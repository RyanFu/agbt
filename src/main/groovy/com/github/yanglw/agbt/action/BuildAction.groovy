package com.github.yanglw.agbt.action

/**
 * <p>
 * 动作。用于在特定的事件中进行操作。
 * </p>
 * Created by yanglw on 2015-12-4.
 */
interface BuildAction {
    /** 初始化操作，{@link org.gradle.api.Project#afterEvaluate(groovy.lang.Closure)} 后执行。 */
    void onInit()

    /**
     * 对 variant 进行操作。
     * @param variant
     * @param projectType 当前项目类型。
     * <ul>
     *     <li>{@link com.github.yanglw.agbt.util.ProjectUtil#PROJECT_TYPE_APPLICATION}：application</li>
     *     <li>{@link com.github.yanglw.agbt.util.ProjectUtil#PROJECT_TYPE_LIBRARY}：library</li>
     *     <li>{@link com.github.yanglw.agbt.util.ProjectUtil#PROJECT_TYPE_OTHER}：其他</li>
     * </ul>
     */
    void onVariantsAll(def variant, int projectType)

    /** 结束操作，gradle task 全部执行结束之后执行。 */
    void onFinish()
}