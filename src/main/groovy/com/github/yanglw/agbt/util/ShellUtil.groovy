package com.github.yanglw.agbt.util

/**
 * <p>
 * Process 工具类。
 * </p>
 * Created by yanglw on 2015-12-22.
 */
class ShellUtil {
    /** process 输出内容 Model 。*/
    static class ShellValue {
        /** 输出内容。 */
        String text
        /** 程序退出值。 */
        int exitValue
    }

    /**
     * 执行一个 process 。
     * @param command 命令集。
     * @return process 输出内容。
     */
    static ShellValue execute(String... command) {
        execute(null, command)
    }

    /**
     * 执行一个 process 。
     * @param command 命令集。
     * @return process 输出内容。
     */
    static ShellValue execute(List<String> command) {
        execute(null, command)
    }

    /**
     * 在指定的目录下执行一个 process 。
     * @param directory 指定的目录。
     * @param command 命令集。
     * @return process 输出内容。
     */
    static ShellValue execute(File directory, List<String> command) {
        execute(directory, command as String[])
    }

    /**
     * 在指定的目录下执行一个 process 。
     * @param directory 指定的目录。
     * @param command 命令集。
     * @return process 输出内容。
     */
    static ShellValue execute(File directory, String... command) {
        ProcessBuilder builder = new ProcessBuilder(command)
        def process = builder.directory(directory)
                             .redirectErrorStream(true)
                             .start()

        ShellValue value = new ShellValue()
        value.text = process.inputStream.text
        process.destroy()
        value.exitValue = process.exitValue()
        return value
    }
}
