package com.github.yanglw.agbt.util

import org.apache.commons.io.FileUtils

import java.security.MessageDigest

/**
 * <p>
 * 文件操作工具类。
 * </p>
 *
 * Created by yanglw on 2015-12-9.
 */
class FileUtil {
    /**
     * 获取文件后缀名，如果无法获取到后缀名，返回文件名称。
     * @param fileName 文件名称。
     * @return 文件后缀名。
     */
    static String getExtensionName(String fileName) {
        return getExtensionName(fileName, fileName);
    }

    /** 获取文件扩展名。 */
    /**
     * 获取文件后缀名。
     * @param fileName 文件名称。
     * @param defaultName 如果无法获取到后缀名，返回的默认后缀名。
     * @return 文件后缀名。
     */
    static String getExtensionName(String fileName, String defaultName) {
        if (fileName) {
            int dot = fileName.lastIndexOf('.');
            if (dot > -1 && dot < fileName.length() - 1) {
                return fileName.substring(dot + 1);
            }
        }
        return defaultName;
    }

    /**
     * 复制文件。
     * <ul>
     *     <ul>
     *         源文件为文件夹时
     *         <li>将源文件夹中的所有文件复制到目标文件夹。</li>
     *     </ul>
     *     <ul>
     *         源文件为文件时
     *         <li>isDirectory 为 true ，将源文件复制到目标文件夹中。</li>
     *         <li>isDirectory 为 false ，将源文件复制到目标文件。</li>
     *     </ul>
     * </ul>
     * @param srcFile 源文件。
     * @param destFile 目标文件路径。
     * @param isDirectory 当复制文件时，标记目标文件是否是文件夹。
     */
    static void copy(File srcFile, File destFile, boolean isDirectory) {
        if (!srcFile.exists()) {
            return
        }

        if (srcFile.isDirectory()) {
            FileUtils.copyDirectory(srcFile, destFile)
        } else {
            if (isDirectory) {
                FileUtils.copyFileToDirectory(srcFile, destFile)
            } else {
                FileUtils.copyFile(srcFile, destFile)
            }
        }
    }

    /**
     * 获取文件 MD5 信息摘要。
     * @param file 目标文件。
     * @return 文件 MD5 信息。
     */
    static String generateMD5(File file) {
        return generateFile(file, 'MD5')
    }

    /**
     * 获取文件 SHA-256 信息摘要。
     * @param file 目标文件。
     * @return 文件 SHA-256 信息。
     */
    static String generateSHA256(File file) {
        return generateFile(file, 'SHA-256')
    }

    /**
     * 获取文件信息摘要。
     * <p>
     * copy from <a href=https://gist.github.com/mcquinne/2655782>https://gist.github.com/mcquinne/2655782</a> .
     * </p>
     * @param file 目标文件。
     * @param algorithm 信息摘要算法名称。
     * @return 文件置顶的摘要算法的信息摘要。
     */
    static String generateFile(File file, String algorithm) {
        def digest = MessageDigest.getInstance(algorithm)
        file.eachByte(4096) { byte[] buffer, length ->
            digest.update(buffer, 0, (int) length)
        }
        return digest.digest().encodeHex() as String
    }

    /**
     * 根据配置文件获取 Properties 对象，默认使用 utf-8 编码读取文件。
     * @param path 配置文件路径。
     * @return 具有配置文件信息的 Properties 对象。
     */
    static Properties loadProperties(String path) {
        File file = null
        if (path) {
            file = new File(path)
        }
        return loadProperties(file, 'utf-8')
    }

    /**
     * 根据配置文件获取 Properties 对象。
     * @param file 配置文件。
     * @param charset 文件编码。
     * @return 具有配置文件信息的 Properties 对象。
     */
    static Properties loadProperties(File file, String charset) {
        Properties props = new Properties()

        if (file?.exists()) {
            def reader = file.newReader(charset);
            props.load(reader)
            reader.close()
        }
        return props
    }

    /**
     * 将 Properties 对象中的信息保存至文件，文件使用 utf-8 编码。
     * @param pros Properties 对象。
     * @param path 目标文件路径。
     * @param charset 文件编码。
     */
    static void saveProperties(Properties pros, String path) {
        if (path) {
            saveProperties(pros, new File(path), 'utf-8')
        }
    }

    /**
     * 将 Properties 对象中的信息保存至文件。
     * @param pros Properties 对象。
     * @param file 目标文件。
     * @param charset 文件编码。
     */
    static void saveProperties(Properties pros, File file, String charset) {
        def writer = file.newWriter(charset);
        pros.store(writer, null)
        writer.close()
    }
}
