package com.github.yanglw.agbt.tool.collect.output

/**
 * <p>
 * 信息输出者。用于将所有收集到的信息进行输出。
 * </p>
 * Created by yanglw on 2016-1-7.
 */
interface Output {
    /**
     * 输出所有信息。
     * @param allInfo 所有信息列表。
     */
    void output(List<Map> allInfo)
}