package com.dust.cc;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 文件目录管理
 */
public class FileManager {

    private List<File> bootFile;

    private File dirFile;

    public static FileManager create(String dirs, String bootstrapDirs) {
        FileManager fileManager = new FileManager();
        fileManager.initPath(dirs, bootstrapDirs);
        return fileManager;
    }

    private FileManager() {
        this.bootFile = new ArrayList<>();
    }

    private void initPath(String dirs, String bootstrapDirs) {
        dirFile = new File(dirs);
        if (!dirFile.isDirectory()) {
            throw new IllegalArgumentException("<dirs> is not a directory!!");
        }
        File boot = new File(bootstrapDirs);
        if (boot.isFile()) {
            bootFile.add(boot);
        } else {
            bootFile = showFiles(boot);
        }
    }

    /**
     * 展示该目录下的所有文件
     */
    private List<File> showFiles(File file) {
        if (file.isFile()) {
            return Collections.singletonList(file);
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            List<File> result = new ArrayList<>();
            assert files != null;
            for (File f : files) {
                result.addAll(showFiles(f));
            }
            return result;
        }
        return Collections.emptyList();
    }

    public List<File> getBootStrapFile() {
        return bootFile;
    }

    public List<File> getAllFiles() {
        return showFiles(dirFile);
    }

    /**
     * 获取文件列表中不再这个集合中的元素
     */
    public List<String> notIncluded(Set<String> strings) {

        return null;
    }

}
