package com.github.yanglw.agbt.tool.collect.task.collector

import com.github.yanglw.agbt.tool.collect.extension.CollectDefaultConfig
import com.github.yanglw.agbt.util.FileUtil

/**
 * <p>
 * 默认的文件收集类。
 * <p>
 * Created by yanglw on 2015-12-24.
 */
class FileCopyCollector implements Collector {

    final CollectDefaultConfig config

    FileCopyCollector(CollectDefaultConfig config) {
        this.config = config
    }

    @Override
    void collectFile(variant, File file, int type, Map info) {
        String path
        String key
        switch (type) {
            case FILE_TYPE_OUTPUT:
                path = collectOutputFile(variant, file)
                key = INFO_KEY_OUTPUT
                break
            case FILE_TYPE_MANIFEST:
                path = collectManifestFile(variant, file)
                key = INFO_KEY_MANIFEST
                break
            case FILE_TYPE_MAPPING:
                path = collectProguardOutputFolder(variant, file)
                key = INFO_KEY_MAPPING
                break
            case FILE_TYPE_JAR:
                path = collectProguardOutputFile(variant, file)
                key = INFO_KEY_JAR
                break
            case FILE_TYPE_DEX:
                path =  collectDexFile(variant, file)
                key = INFO_KEY_DEX
                break
            default:
                return
        }
        info.put(key, path)
    }

    /** 复制生成物文件，例如 apk 、aar 文件。 */
    String collectOutputFile(variant, File outputFile) {
        // 根据目标文件后缀名决定目标文件存放的文件夹的名称。
        String outputDirName = FileUtil.getExtensionName(outputFile.name, 'output')
        String extensionName = FileUtil.getExtensionName(outputFile.name, '')
        if (extensionName) {
            extensionName = '.' + extensionName
        }
        // 生成物存放的文件夹。
        File outputDir = new File(getVariantOutFolder(variant), outputDirName)
        // 设置复制后的文件的名称。
        File newOutputFile = new File(outputDir,
                                      "${config.getOutputFileName(variant)}${extensionName}")

        FileUtil.copy(outputFile, newOutputFile, false)
        return newOutputFile.absolutePath
    }

    /** 复制最终的 AndroidManifest.xml 文件。 */
    String collectManifestFile(variant, File manifestFile) {
        File newManifestFile = new File(new File(getVariantOutFolder(variant), 'manifest'), manifestFile.name)
        FileUtil.copy(manifestFile, newManifestFile, false)
        return newManifestFile.absolutePath
    }

    /** 复制 Proguard 的输出 mapping 文件夹。 */
    String collectProguardOutputFolder(variant, File proguardOutputFolder) {
        File newProguardOutputFolder = new File(getVariantOutFolder(variant), 'mapping')
        FileUtil.copy(proguardOutputFolder, newProguardOutputFolder, true)
        return newProguardOutputFolder.absolutePath
    }

    /** 复制 Proguard 输出的 jar 文件 。 */
    String collectProguardOutputFile(variant, File jarFile) {
        File newJarFile = new File(new File(getVariantOutFolder(variant), 'jar'), jarFile.name)
        FileUtil.copy(jarFile, newJarFile, false)
        return newJarFile.absolutePath
    }

    /** 复制 dex 文件。 */
    String collectDexFile(variant, File dexFile) {
        File newDexFile = new File(new File(getVariantOutFolder(variant), 'dex'), dexFile.name)
        FileUtil.copy(dexFile, newDexFile, false)
        return newDexFile.absolutePath
    }

    /** 当前 variant 生成物存放的文件夹。 */
    File getVariantOutFolder(variant) {
        return config.getVariantOutputFolder(variant)
    }
}
