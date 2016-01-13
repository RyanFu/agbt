package com.github.yanglw.agbt.util

import org.gradle.api.Project

/**
 * <p>
 * 项目工具类。
 * </p>
 *
 * Created by yanglw on 2016-1-5.
 */
class ProjectUtil {/** gradle task group. */
    public static final String GRADLE_TASK_GROUP = 'tool'
    /** 分隔符。 */
    public static final String SEPARATOR = '-'
    /** 当前操作系统的换行符。 */
    public static final String LINE_SEPARATOR = System.getProperties().getProperty("line.separator")
    /** 项目类型，application 。 */
    public static final int PROJECT_TYPE_APPLICATION = 0
    /** 项目类型，library 。 */
    public static final int PROJECT_TYPE_LIBRARY = 1
    /** 项目类型，其他 。 */
    public static final int PROJECT_TYPE_OTHER = 2

    private static ProjectInfo info

    static void init(Project project) {
        if (info == null) {
            info = new ProjectInfo(project)
        }
    }

    /**
     * 判断当前项目类型。
     * <ul>
     *     <li>{@link #PROJECT_TYPE_APPLICATION}：application</li>
     *     <li>{@link #PROJECT_TYPE_LIBRARY}：library</li>
     *     <li>{@link #PROJECT_TYPE_OTHER}：其他</li>
     * </ul>
     */
    public static int checkProjectType(Project project) {
        // 判断当前项目的项目类型。
        if (project.android.hasProperty('applicationVariants')) {
            return PROJECT_TYPE_APPLICATION
        } else {
            if (project.android.hasProperty('libraryVariants')) {
                return PROJECT_TYPE_LIBRARY
            } else {
                return PROJECT_TYPE_OTHER
            }
        }
    }

    /**
     * 获取当前项目类型。
     * <ul>
     *     <li>{@link #PROJECT_TYPE_APPLICATION}：application</li>
     *     <li>{@link #PROJECT_TYPE_LIBRARY}：library</li>
     *     <li>{@link #PROJECT_TYPE_OTHER}：其他</li>
     * </ul>
     */
    static int getProjectType() {
        if (info == null) {
            throw new RuntimeException('need first call init method.')
        }
        return info.projectType
    }

    /** 获取项目编译的时间。 */
    static long getBuildTime() {
        if (info == null) {
            throw new RuntimeException('need first call init method.')
        }
        return info.buildTime
    }

    /** 项目信息。 */
    private static class ProjectInfo {
        int projectType
        long buildTime

        ProjectInfo(Project project) {
            projectType = checkProjectType(project)
            buildTime = new Date().getTime()
        }
    }
}
