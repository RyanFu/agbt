package com.github.yanglw.agbt.tool.collect.output.info

/**
 * <p>
 * 信息管理类，用于集中信息，方便管理。
 * </p>
 * Created by yanglw on 2015-12-10.
 */
class BuildInfoOutputManager {
    private static final BuildInfoOutputManager manager = new BuildInfoOutputManager()

    final List<Map> allInfo = []

    private BuildInfoOutputManager() {
    }

    void addInfo(Map info) {
        allInfo << info
    }

    public static BuildInfoOutputManager getInstance() {
        return manager
    }
}
