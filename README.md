# classClean

一个用来分析项目中没有引用到的类的小工具。它根据字节码文件的常量池解析所用到的其他类并以此迭代出一条引用路径。

## 使用方式

您可以自行编译成jar包。或者直接使用其中的Main。其中args命令如下：\<待扫描的文件夹\> \<自行设定的root文件夹/文件\> <br>
由于改工具是根据.class文件的字节码来进行扫描的，所以以上良文路径都应该指向.class文件而不是.java文件。

## 例子

一个普通的项目目录结构如下：<br>
src<br>
  -main<br>
    -java<br>
      -com<br>
        -text<br>
          -controller<br>
          -service<br>
          -model <br>
          ... <br>
target<br>
  -classses<br>
    -com<br>
      -text<br>
        -controller<br>
        -service<br>
        -model<br>
        ...<br>

如果我们需要进行分析，则输入命令为 /{classPath}/target/classes/com /{classPath}/target/classes/com/controller<br>
这样，他就会根据controller文件下的所有Controller.class进行分析并以此为bootstrap Class创建调用链。并分析com文件夹下的所有文件有那些没有被调用到。
