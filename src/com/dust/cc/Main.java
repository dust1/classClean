package com.dust.cc;


import java.io.File;

public class Main {

    /**
     * java -jar cclean <allDirs> <bootStrapDir>
     * example:
     * java -jar cclean /usr/local/jar/myJavaProject/target/classes/myJavaProject
     *                  /usr/local/jar/myJavaProject/target/classes/myJavaProject/src/main/java/com/dust/controller
     */
    public static void main(String[] args) {
        CleanTask cleanTask = new CleanTask();
        int i = cleanTask.run(args);
        System.exit(i);
    }
}
