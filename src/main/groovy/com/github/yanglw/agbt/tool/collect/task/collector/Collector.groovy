package com.github.yanglw.agbt.tool.collect.task.collector

/**
 * <p>
 * 收集者。用于收集 variant 中的各类有用途的文件。
 * </p>
 * Created by yanglw on 2015-12-24.
 */
interface Collector {
    /** apk 或者 aar 文件。 */
    public static final int FILE_TYPE_OUTPUT = 0
    /** AndroidManifest 文件。 */
    public static final int FILE_TYPE_MANIFEST = 1
    /** java class 经 proguard 后输出的 jar 文件。 */
    public static final int FILE_TYPE_JAR = 2
    /** proguard 输出 mapping 文件夹。 */
    public static final int FILE_TYPE_MAPPING = 3
    /** dex 文件。 */
    public static final int FILE_TYPE_DEX = 4

    /** 用于标记当前 Map 为 variant 文件收集信息集合的字段。 */
    public static final String INFO_KEY_GROUP_FLAG_FILE_INFO = 'group_file_info'
    /** 当前 variant 对象。 */
    public static final String INFO_KEY_VARIANT = 'file_info_variant'
    /** {@link Collector} 收集当前 variant 的 apk 或者 aar 文件所存放的路径。 */
    public static final String INFO_KEY_OUTPUT = 'file_info_output'
    /** {@link Collector} 收集当前 variant 的最终 AndroidManifest 文件所存放的路径。 */
    public static final String INFO_KEY_MANIFEST = 'file_info_manifest'
    /** {@link Collector} 收集当前 variant proguard 输出 mapping 文件夹所存放的路径。 */
    public static final String INFO_KEY_MAPPING = 'file_info_mapping'
    /** {@link Collector} 收集当前 variant 的 java class 经 proguard 后输出的 jar 文件所存放的路径。 */
    public static final String INFO_KEY_JAR = 'file_info_jar'
    /** {@link Collector} 收集当前 variant 的 dex 文件所存放的路径。 */
    public static final String INFO_KEY_DEX = 'file_info_dex'
    /** {@link Collector} 收集当前 variant 的 apk 或者 aar 文件后生成的下载地址。 */
    public static final String INFO_KEY_DOWNLOAD_URL = 'file_info_download_url'

    /** 用于标记当前 Map 为 VCS 信息集合的字段。 */
    public static final String INFO_KEY_GROUP_FLAG_VCS_INFO = 'group_vcs_info'
    /** 最后一次提交的 Commit Code 。 */
    public static final String INFO_KEY_VCS_COMMIT_CODE = 'vcs_info_commit_code'
    /** 远程仓库的地址 。 */
    public static final String INFO_KEY_VCS_REMOTE_URL = 'vcs_info_remote_url'
    /** 更改日志 。 */
    public static final String INFO_KEY_VCS_LOG = 'vcs_info_log'

    /**
     * 收集文件。
     * @param file 需要收集的文件或者文件夹。
     * @param type 文件类型。
     * @return 文件存放路径。
     *
     * @see #FILE_TYPE_OUTPUT
     * @see #FILE_TYPE_MANIFEST
     * @see #FILE_TYPE_JAR
     * @see #FILE_TYPE_MAPPING
     * @see #FILE_TYPE_DEX
     */
    void collectFile(variant, File file, int type, Map info)
}
