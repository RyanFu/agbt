package com.github.yanglw.agbt.tool.collect.task.collector

import com.github.yanglw.agbt.tool.collect.extension.CollectDefaultConfig

/**
 * <p>
 * 默认的文件上传类。通过复制文件到服务器下载目录实现下载功能。
 * </p>
 * Created by yanglw on 2015-12-24.
 */
class FileUploadCollector extends FileCopyCollector {
    /**
     * <p>
     * 下载链接前缀。
     * </p>
     *
     * 本类将通过将收集到的文件的路径裁剪去仓库路径，拼接至本字段上生成下载链接。
     * @see #getDownloadUrl(java.lang.String)
     */
    String downloadUrlPrefix

    FileUploadCollector(CollectDefaultConfig config, String downloadUrlPrefix) {
        super(config)
        setDownloadUrlPrefix(downloadUrlPrefix)
    }

    @Override
    void collectFile(variant, File file, int type, Map info) {
        if (type == FILE_TYPE_OUTPUT) {
            super.collectFile(variant, file, type, info)
            String filePath = info.get(INFO_KEY_OUTPUT)
            if (filePath) {
                String url = getDownloadUrl(filePath)
                if (url) {
                    info.put(INFO_KEY_DOWNLOAD_URL, url)
                }
            }
        }
    }

    /**
     * <p>
     * 生成下载链接。
     * </p>
     *
     * 操作流程：
     * <ol>
     *     <li>获取仓库路径。{@link CollectDefaultConfig#getRepRoot()}</li>
     *     <li>将下载文件的路径去除仓库路径。</li>
     *     <li>将第二步获取到的字符串拼接在 {@link #downloadUrlPrefix} 后面，组成下载链接。</li>
     * </ol>
     *
     * @param downFilePath 下载文件在当前系统的路径。
     * @return 下载链接。
     * @see CollectDefaultConfig#getRepRoot()
     */
    String getDownloadUrl(String downFilePath) {
        String rootPath = new File(getConfig().getRepRoot()).absolutePath
        int index = downFilePath.indexOf(rootPath)
        if (index >= 0) {
            String relativePath = downFilePath.substring(index + rootPath.length() + 1, downFilePath.length())
            relativePath = relativePath.replaceAll($/\\/$, "/")
            if (relativePath.startsWith('/')) {
                relativePath.substring(1, relativePath.length())
            }
            return downloadUrlPrefix + relativePath
        } else {
            return null
        }
    }

    void setDownloadUrlPrefix(String downloadUrlPrefix) {
        if (downloadUrlPrefix) {
            this.downloadUrlPrefix = downloadUrlPrefix.replaceAll($/\\/$, "/")
            if (!this.downloadUrlPrefix.endsWith('/')) {
                this.downloadUrlPrefix = this.downloadUrlPrefix + '/'
            }
        } else {
            this.downloadUrlPrefix = ''
        }
    }
}
