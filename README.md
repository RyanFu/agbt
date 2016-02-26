# Android Gradle Build Tool

所有的 Android 项目打包时，都会遇到以下重复性的问题：

- 更改 apk 的版本号以及版本名称
- 复制 apk 文件，发送给测试或者运维
- 复制 mapping 文件夹，用于反射出混淆前的代码
- 渠道包
- 发邮件通知测试或者运维有新的版本出现
- ...

这些重复性的劳动，无趣却又不得不做。本项目，就是为了解决这些问题而产生，自动化的解决上述问题。

本插件包含以下功能：

- 版本信息自动升级
- 收集 apk 、mapping 、AndroidManifest 、dex 文件
- 通过复制目标文件至 ftp 服务器目录，实现提供 ftp 下载的功能
- 打渠道包（[美团式渠道包](http://tech.meituan.com/mt-apk-packaging.html)）
- 邮件通知

## 使用方式

- 在 Project 目录中的 `build.gradle` 文件中加入编译依赖

```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "com.github.yanglw:agbt:0.2"
    }
}
```

- 在 Mudole 目录中的 `build.gradle` 文件中，引入插件。

```
apply plugin: 'agbt'
```

完成以上两步，即可使用本插件。

最新版本：

[![bintray](https://img.shields.io/badge/2016.02.26-com.github.yanglw:agbt:0.2.0-brightgreen.svg)](https://bintray.com/yanglw/maven/agbt/view) 

- 执行 Gradle 命令

```
gradle collect
```

`collect` 命令即可用来执行插件任务。也可以针对不同的维度进行进行 `collect` 。本插件支持 android 插件中的 buildTypes 和 productFlavors 的维度以及二者的组合维度。

例如，只进行 debug 任务，可以执行 `gradle collectDebug` 命令。

本插件的任务集为 android 插件中 `assemble` 的任务集的一个扩展，形成了一个 `collect` 任务集。

## 功能配置

### 自动升级版本信息

为了实现版本信息的自动升级功能，我们需要存储上一次的版本信息。本插件使用 [Properties](https://docs.oracle.com/javase/7/docs/api/java/util/Properties.html) 进行版本信息的存储。所以，你需要指定一个 Properties 文件的存储位置。

具体使用方式如下：

```
apply plugin: 'agbt'

tool {
    ver {
        verFilePath
        onSetVer {Properties versionProps, def version, String buildTypeName, List<String> flavorNames ->
        }

        onSaveVer {Properties versionProps, List<Object> list ->
        }
    }
}
```

`verFilePath` 为 Properties 文件的存储位置，需指向一个文件（如果该文件不存在，则会自动创建）。**如果没有设置该字段，则表示不使用自动更改版本信息的功能**。

`onSetVer` 为设置版本信息的方法。插件将读取 Properties 文件中的信息，作为参数传递至闭包。同时，也将传递当前 variant 的信息（为了方便使用，提取了当前 variant 的 buildTypes 的名称以及 productFlavors 名称列表至 buildTypesName， flavorNames）。下面详细解说，`onSerVer` 方法的闭包所传递的 4 个参数的含义。

- 第一个参数，类型： Properties ，指定的配置文件对应的 Properties 对象。
- 第二个参数，类型： [VersionExtension.Version](src/main/groovy/com/github/yanglw/agbt/tool/version/extension/VersionExtension.groovy#L65) ，通过设置该对象的 versionCode 和 versionName 字段实现控制更改版本号。Version 信息如下：
    - 字段名称： versionName ；类型： String ；含义： 版本名称。
    - 字段名称： versionCode ；类型： int ；含义： 版本号。
- 第三个参数，类型： String ，当前 variant 的 buildTypes 名称。
- 第四个参数，类型： List&#60;String&#62;，当前 variant 的 productFlavors 名称列表。

该闭包无需返回任何对象，只需对 version 对象进行操作即可。通过设置 version 对象的 versionName 字段和 versionCode 字段实现更改版本信息的功能。如果修改后的 Version 无意义（**versionName 为空字符串或者 versionCode 小于 1**），不再修改版本信息。

`onSaveVer` 为存储本次的版本信息，在下次变更版本时使用。同样的，会将 Properties 文件中的信息，作为参数传递至闭包。同时，也将传递所有使用到自动更新版本信息的 variant 信息的列表。下面详细解说，`onSaveVer` 方法的闭包所传递的 2 个参数的含义。

- 第一个参数，类型：Properties ，指定的配置文件对应的 Properties 对象。
- 第二个参数，类型：List&#60;[SimpleVariant](src/main/groovy/com/github/yanglw/agbt/variant/SimpleVariant.groovy)&#62; ，执行了 assemble task 同时修改了版本信息的 variant 列表。SimpleVariant 信息如下：
    - 字段名称： buildType ；类型： String ；含义： variant 的 buildTypes 名称。
    - 字段名称： flavors ；类型： List&#60;String&#62; ；含义： variant 的 productFlavors 名称列表。

最终，插件会将 Properties 中的信息存储至存储文件，你只需在 `onSaveVer` 的闭包中，修改 Properties 中的键值对即可。

应用举例：

```
apply plugin: 'com.android.application'
apply plugin: 'agbt'

android {
    buildTypes {
        release {
        }
        debug {
        }
    }

    productFlavors {
        google {
        }
        amazon {
        }
    }
}

tool {
    ver {
        verFilePath 'e:/outputs/1.txt'
        onSetVer {versionProps, version, buildType, flavorNames ->
            String hVerName = versionProps['hVerName'] ?: '1.0.0'
            String lVerName = versionProps['lVerName'] ?: '0'
            String versionName = "${hVerName}.${lVerName}"
            int versionCode = versionProps['verCode'] ? versionProps['verCode'].toInteger() : 1

            version.versionCode = versionCode

            if (buildType == 'release') {
                version.versionName = hVerName
            } else {
                version.versionName = "${hVerName}.${lVerName}"
            }

            if (flavorNames.contains('google')) {
                version.versionName += '_google'
            } else {
                version.versionName += '_amazon'
            }
        }

        onSaveVer {versionProps, list ->
            String hVerName = versionProps['hVerName'] ?: '1.0.0'
            String lVerName = versionProps['lVerName'] ?: '0'
            int versionCode = versionProps['verCode'] ? versionProps['verCode'].toInteger() : 1
            
            versionProps['hVerName'] = hVerName
            versionProps['lVerName'] = String.valueOf(lVerName.toInteger() + 1)
            versionProps['verCode'] = String.valueOf(versionCode + 1)
        }
    }
}
```

本例实现了，`release` 版本名称为三位数；`debug` 版本名称为四位数，且第四位自动增加；`release` 和 `debug` 版本号均自动增加的 3 个功能。

### 收集文件

```
apply plugin: 'agbt'

tool {
    bee {
        defaultConfig {
            repRoot
            appName
            outputFolderMatcher
            outputFileMatcher
            flavorsBuilder
            timeFormat
            defaultCollectorEnable
        }
        configs {
            xxx {
                repRoot
                appName
                outputFolderMatcher
                outputFileMatcher
                flavorsBuilder
                timeFormat
            }
        }
    }
}
```

收集文件的功能，由 `bee` 字段实现，`bee` 包含两个字段，`defaultConfig` 和 `configs` 。`defaultConfig` 为默认配置，如果在 `configs` 中没有设置某个字段，则使用 `defaultConfig` 中该字段的值。`configs` 为用户自定义的文件收集功能，用户可以自行添加元素（方式类似于 productFlavors） 实现 apk 上传等功能。

字段详细说明：

- repRoot 类型：String ，含义：收集文件所存放的位置，一般为一个包含众多项目的仓库起始目录。
- appName 类型：String ，含义：项目名称（Android Studio 默认的项目名称为 app ，但是很多情况下，项目的名称并非 app ，可以在这里设置项目名称）。
- outputFolderMatcher 类型：String ，含义：当前项目指定 variant 生成物存放目录路径内容，借助 [SimpleTemplateEngine]() 实现的字符标记替换，达到动态设置路径的功能。目前，该字段支持一下标记：
    - appName ：App 模块名称
    - projectName ：项目名称
    - rootProjectName ：根项目名称
    - applicationId ： applicationId
    - buildType ： buildType
    - flavors ： flavors（支持 flavorDimensions）
    - versionName ：版本名称
    - versionCode ：版本号
    - time ：编译时间
    - channel ：当前渠道名称（仅支持[ChannelCollector](src/main/groovy/com/github/yanglw/agbt/tool/collect/task/collector/ChannelCollector.groovy)）
- outputFileMatcher 类型：String ，含义：当前项目生成物名称内容（不包含后缀名和后缀名前的点），借助 [SimpleTemplateEngine](http://www.groovy-lang.org/api/groovy/text/SimpleTemplateEngine.html) 实现的字符标记替换，达到动态设置路径的功能。目前，该字段支持一下标记：
    - appName ：App 模块名称
    - projectName ：项目名称
    - rootProjectName ：根项目名称
    - applicationId ： applicationId
    - buildType ： buildType
    - flavors ： flavors（支持 flavorDimensions）
    - versionName ：版本名称
    - versionCode ：版本号
    - time ：编译时间
    - channel ：当前渠道名称（仅支持[ChannelCollector](src/main/groovy/com/github/yanglw/agbt/tool/collect/task/collector/ChannelCollector.groovy)）
- flavorsBuilder 类型：Closure ，含义：用于生成 outputFileMatcher 中 flavors 的内容。将会传递一个类型为 List&#60;String&#62; 的参数至该闭包，该参数代表着当前 variant 的 productFlavors 名称列表。
- timeFormat 类型：String ，含义：用于生成 outputFileMatcher 中 time 格式化内容。
- defaultCollectorEnable 类型：boolean ，含义：表示使用使用默认的文件收集方式（收集 apk 、mapping 、AndroidManifest 、dex 文件到对应的目录，具体实现参见[FileCopyCollector.groovy](src/main/groovy/com/github/yanglw/agbt/tool/collect/task/collector/FileCopyCollector.groovy)）。本字段只在 `defaultConfig` 中有意义。
- collector 类型：Closure ，含义：用户自定义的收集动作。该闭包，将会被传递一下参数：
    - 第一个参数，当前的 variant
    - 第二个参数，类型：File ，需要进行处理的文件。
    - 第三个参数，类型：int ，表示第二个参数的类型。
        - 0 ：apk 或者 aar 文件。
        - 1 ：AndroidManifest 文件。
        - 2 ：java class 经 proguard 后输出的 jar 文件。
        - 3 ：proguard 输出 mapping 文件夹。
        - 4 ：dex 文件。
    - 第四个参数，类型：Map ，表示当前 variant 信息集合，用于存储一些有意义的信息，通过邮件发出。默认的其中有一个 key 为 `"file_info_variant"` ，value 为当前 variant 的元素。以下为插件已经实现的收集动作会添加的元素内容： 
        - [FileCopyCollector.groovy](src/main/groovy/com/github/yanglw/agbt/tool/collect/task/collector/FileCopyCollector.groovy)
            - key ："group_file_info" ，标记 Map 为 variant 信息集合，value 无意义。
            - key ："file_info_output" ，当前 variant 的 apk 或者 aar 文件所存放的路径。
            - key ："file_info_manifest" ，当前 variant 的最终 AndroidManifest 文件所存放的路径。
            - key ："file_info_mapping" ，当前 variant proguard 输出 mapping 文件夹所存放的路径。
            - key ："file_info_jar" ，当前 variant 的 java class 经 proguard 后输出的 jar 文件所存放的路径。
            - key ："group_file_info" ，当前 variant 的 dex 文件所存放的路径。
        - [FileUploadCollector.groovy](src/main/groovy/com/github/yanglw/agbt/tool/collect/task/collector/FileUploadCollector.groovy)
            - key ："file_info_download_url" ，当前 variant 的 apk 或者 aar 文件的下载地址。

关于收集动作，本插件除了 `defaultCollectorEnable` 对应的 [FileCopyCollector.groovy](src/main/groovy/com/github/yanglw/agbt/tool/collect/task/collector/FileCopyCollector.groovy) ，还有 [FileUploadCollector.groovy](src/main/groovy/com/github/yanglw/agbt/tool/collect/task/collector/FileUploadCollector.groovy) 和 [ChannelCollector.groovy](src/main/groovy/com/github/yanglw/agbt/tool/collect/task/collector/ChannelCollector.groovy) 。

FileUploadCollector 通过复制文件至下载服务器的仓库目录实现提供下载功能。例如，在服务器中，有一个 ftp 服务，默认的 ftp 目录为 `e:/ftp/` ，ftp 地址为 `ftp://server.com/apk/`，FileUploadCollector 通过将 apk 文件复制到 `e:/ftp/app/release/xxx.apk` 提供 `ftp://server.com/apk/app/release/xxx.apk` 的下载功能。

ChannelCollector 提供[美团式渠道包](http://tech.meituan.com/mt-apk-packaging.html)生成功能，关于渠道配置，需要传递进来一个渠道列表文件，该文件有以下要求：
- 一行只有一个渠道号。
- 每一行均需要含有 `-` 字符。
- `-` 字符前的内容为写入 META_INF 中的渠道文件的名称，字符后的内容为 apk 文件名称追加的内容。
- 以 # 开头的行将会被忽略。

例如，在这么一个渠道列表文件中：

```
channel_channel1-channel1
#channel_channel2-channel2
```

第一行为有效的数据，第二行会被自动忽略。


应用举例：

```
apply plugin: 'com.android.application'
apply plugin: 'agbt'

android {
    buildTypes {
        release {
        }
        debug {
        }
    }

    productFlavors {
        google {
        }
        amazon {
        }
    }
}

tool {
    bee {
        defaultConfig {
            repRoot 'e:/outputs/'
            appName 'test'
            outputFolderMatcher '${time}-${versionCode}-${versionName}/${flavors}-${buildType}'
            outputFileMatcher '${appName}-${buildType}-${flavors}-${versionName}-${versionCode}'
            flavorsBuilder {it.join('-')}
            timeFormat 'yyyyMMdd'
            defaultCollectorEnable false
        }
        configs {
            fileCopyCollect {
                collector = getFileCopyCollector()
            }
            uploadApk {
                // 设置 ftp 的仓库目录。
                repRoot = 'e:/ftp/'
                // 通过 getFileUploadCollector(String) 获取 FileUploadCollector 实例。该方法的参数含义为 ftp 地址的前缀。
                collector = getFileUploadCollector('ftp://10.128.4.93/')
            }
            channel {
                // 通过 getChannelCollector(String) 获取 ChannelCollector 实例。该方法的参数含义渠道文件的路径。
                collector = getChannelCollector({'e:/outputs/channel.txt'})
            }
            myCollect {
                // 自定义收集动作，这里没有实际的文件复制等操作，只是输出了当前自定义的 config 的 variant 输出目录，以及目标文件的名称。
                collector = {def variant, File file, int type, Map info ->
                    println getVariantOutputFolder(variant)
                    println getOutputFileName(variant)
                }
                outputFolderMatcher = '${time}-${versionName}-${versionCode}/${flavors}-${buildType}/myCollect'
            }
        }
    }
}
```

### 信息输出

```
apply plugin: 'agbt'

tool {
    bee {
        writer {
            mail {
                mailProsPath
                charset
                host
                port
                auth
                user
                password
                to
                cc
                bcc
                personal
                subjectMather
                appName
                timeFormat
            }

            vcs {
                lastCodeSavePath
                userName
                password
            }

            output

            infoBuilder
        }
    }
}
```

**只有执行了 collect 系列命令信息输出功能才会启动。**

默认的，本插件使用邮件的方式，进行编译信息的输出，可以在 `mail` 字段中设置邮件发送服务器的信息。`vcs` 用于获取当前项目的版本控制的信息。`output` 用于输出信息。当你使用默认的邮件输出的方式输出信息是，可以 `infoBuilder` 设置邮件内容。以下是详细的说明：

`mail` 是邮件发送服务器的信息载体，用户通过在此设置发送服务器的信息才能成功发送邮件。

- mailProsPath ，类型：String ，含义：邮件发送服务器配置文件的路径。为了方便的让多个项目使用同一个邮件服务器配置，插件支持使用本地配置文件的方式配置信息。本地配置信息支持一下字段：
    - "mail.host" 服务器地址
    - "mail.port" 服务器端口号
    - "mail.auth" 标记服务器是否需要认证
    - "mail.user" 账号名称
    - "mail.password" 账号密码
    - "mail.to" 收件人列表
    - "mail.cc" 抄送列表
    - "mail.bcc" 秘密抄送列表
- charset ，类型：String ，含义：邮件标题和邮件内容的编码。
- host ，类型：String ，含义：服务器地址，如果用户设置了 mailProsPath ，将会替换掉 `"mail.host"` 的内容。
- port ，类型：String ，含义：服务器端口号，如果用户设置了 mailProsPath ，将会替换掉 `"mail.port"` 的内容。
- auth ，类型：boolean ，含义：标记服务器是否需要认证，如果用户设置了 mailProsPath ，将会替换掉 `"mail.auth"` 的内容。
- user ，类型：String ，含义：账号名称，如果用户设置了 mailProsPath ，将会替换掉 `"mail.user"` 的内容。
- password ，类型：String ，含义：账号密码，如果用户设置了 mailProsPath ，将会替换掉 `"mail.password"` 的内容。
- to ，类型：String ，含义：收件人列表，如果用户设置了 mailProsPath ，将会替换掉 `"mail.to"` 的内容。
- cc ，类型：String ，含义：抄送列表，如果用户设置了 mailProsPath ，将会替换掉 `"mail.cc"` 的内容。
- bcc ，类型：String ，含义：秘密抄送列表，如果用户设置了 mailProsPath ，将会替换掉 `"mail.bcc"` 的内容。
- personal ，类型：String ，含义：发件人的名称。
- subjectMather ，类型：String ，含义：邮件标题内容，借助 [SimpleTemplateEngine](http://www.groovy-lang.org/api/groovy/text/SimpleTemplateEngine.html) 实现的字符标记替换，达到动态设置路径的功能。目前，该字段支持一下标记：
    - appName ：App 模块名称
    - projectName ：项目名称
    - rootProjectName ：根项目名称
    - time ：编译时间
- appName ，类型：String ，含义：App 模块名称
- timeFormat ，类型：String ，含义：编译时间的格式化字符串

`vcs` 表示输出项目的版本控制信息，默认的，将会输出当前项目的远程主机地址，最后一次的 commit id ，以及本次编译到上一次编译期间的提交日志。

- lastCodeSavePath ，类型：String ，含义：指定存储本次 commit id 的文件的路径。
- userName ，类型：String ，含义：当前版本控制的用户名。
- password ，类型：String ，含义：当前版本控制的用户名对应的密码。

对于 `svn` 来说，需要提供 `userName` 和 `password` 字段，否则无法获取提交日志。对于 `git` ，仅需要设置 `lastCodeSavePath` 即可。如果没有设置 `lastCodeSavePath` 则表示不输出版本控制信息。`vcs` 暂时支持 `svn` 和 `git` 。

`vcs` 会创建一个包含项目版本控制信息的 Map 添加至信息列表，该 Map 中包含以下字段：
 
- key ："group_vcs_info" ，标记 Map 为项目版本控制信息集合，value 为版本控制的名称。
- key ："vcs_info_commit_code" ，最后一次提交的 Commit Id 。
- key ："vcs_info_remote_url" ，远程仓库的地址 。
- key ："vcs_info_log" ，更改日志 。

`output` 是用来输出信息列表的，它的参数为 Closure ，Closure 的参数为 List&#60;Map&#62; ，参数含义为包含所有的信息列表。默认的，如果用户没有设置新的 output ，则使用默认的通过邮件的方式发送项目信息的输出方式（需要 `mail` 的配置信息有效）。

`infoBuilder` 当你使用默认的 output 时，可以通过 infoBuilder 进行设置邮件内容，如果用户没有设置 `infoBuilder` ，则使用默认的 `infoBuilder` 。 `infoBuilder ` 的参数为 Closure ，Closure 的参数为 List&#60;Map&#62; ，参数含义为包含所有的信息列表。`infoBuilder` 需要返回一个 Multipart 对象或者一个 String 对象。本插件提供一系列的添加文本和图片的方法，以及将添加的文字和图片转化为 Multipart 对象的方法。首先，通过 addText(String) 、addImage(File, String) 、addImagePart(File, String, String) 、addQRCodeImage(String) 、addKeyValueTextWithLF(String, String) 添加文本、图片、二维码等内容，最终通过 getMultiPart() 获取包含已添加的文本、图片、二维码等内容的 MultiPart 对象。

- addText(String) 添加文本
    - 参数表示文本内容，该文本内容可以是 HTML 文本。
- addImage(File, String) 添加图片
    - 第一个参数表示图片文件
    - 第二个参数表示文件类型，例如，png、jpg 等
- addImagePart(File, String, String) 添加图片 
    - 第一个参数表示图片文件
    - 第二个参数表示该图片在邮件中的索引 id
    - 第三个参数表示文件类型，例如，png、 jgp 等
- addQRCodeImage(String) 添加二维码图片
    - 参数表示二维码实际的文本内容
- addKeyValueTextWithLF(String, String) 添加键值对信息的文本内容
    - 第一个参数表示键名
    - 第二个参数表示键值

应用举例：

```
apply plugin: 'com.android.application'
apply plugin: 'agbt'

android {
    buildTypes {
        release {
        }
        debug {
        }
    }

    productFlavors {
        google {
        }
        amazon {
        }
    }
}

tool {
    bee {
        defaultConfig {
            repRoot 'e:/outputs/'
            defaultCollectorEnable true
        }
        writer {
            mail {
                host 'smtp.126.com'
                user '@126.com'
                password ''
                personal '打包服务器'
                to '@126.com'
            }
            infoBuilder {List<Map> allInfo ->
                getDefaultInfoBuilder().call(allInfo)
                addText("<p>测试</p>")
                addImage(new File('e:/387546.jpg'), 'jpg')
                addQRCodeImage('添加二维码')
                addKeyValueTextWithLF('hello :', 'world')
                getMultiPart()
            }
        }
    }
}
```

## 感谢

本项目参考了 [gradle-packer-plugin](https://github.com/mcxiaoke/gradle-packer-plugin) 很多的思路以及实现方式，在此谢谢作者 @mcxiaoke 。

## License
This plugin is available under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

(c) All rights reserved yanglw