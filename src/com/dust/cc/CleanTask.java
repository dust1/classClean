package com.dust.cc;

import javax.tools.DiagnosticListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 清理任务
 */
public class CleanTask {

    /**
     * 获取系统的换行
     */
    private static final String nl = System.getProperty("line.separator");

    /**
     * 截取路径的层级
     */
    private static final int TAG = 2;


    private PrintWriter log;
    private DiagnosticListener<? super ClassObject> diagnosticListener;
    private Locale locale;
    private String dirs;
    private String bootstrapDirs;


    /** 返回码
     */
    static final int
            EXIT_OK = 0,        // Compilation completed with no errors.
            EXIT_ERROR = 1,     // Completed but reported errors.
            EXIT_CMDERR = 2,    // Bad command-line arguments
            EXIT_SYSERR = 3,    // System error or resource exhaustion.
            EXIT_ABNORMAL = 4;  // Compiler terminated abnormally


    public CleanTask() {
        //日志输出使用System.out
        this.log = getPrintWriterForStream(System.out);
        //根类路径
        this.locale = Locale.getDefault();

    }

    public int run(String[] args) {
        try {
            handleOptions(args);
            return run();
        } catch (IOException e) {
            showFileError("root", e);
        } finally {
            log.flush();
        }
        return EXIT_SYSERR;
    }

    private int run() throws IOException {
        if (dirs == null || bootstrapDirs == null
            || "".equals(dirs) || "".equals(bootstrapDirs)) {
            return EXIT_ERROR;
        }

        FileManager fileManager = FileManager.create(dirs, bootstrapDirs);

        //controller.TextController -> FileInfo$1234
        Map<String, List<FileInfo>> allFiles = fileManager.getAllFiles().stream()
                .map(f -> {
                    String[] paths = f.getPath().split(String.valueOf(File.separatorChar));
                    Map<String[], File> maps = new HashMap<>(1);
                    maps.put(paths, f);
                    return maps;
                })
                .filter(f -> {
                    Iterator<Map.Entry<String[], File>> iterator = f.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String[], File> entry = iterator.next();
                        String[] keys = entry.getKey();
                        return keys.length >= TAG;
                    }
                    return false;
                })
                .map(f -> {
                    for (Map.Entry<String[], File> entry : f.entrySet()) {
                        String[] keys = entry.getKey();
                        String str = subEnd(keys);
                        return new FileInfo(str, entry.getValue());
                    }
                    return null;
                })
                .collect(Collectors.groupingBy(FileInfo::getClazz));

        List<ClassObject> bootClass = ClassObject.create(fileManager.getBootStrapFile());
        List<String> list = bootClass
                .stream()
                .map(ClassObject::getClassInfoByConstantPool)
                .map(CleanTask::dealWithClass)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        loopClass(allFiles, list);

        Iterator<Map.Entry<String, List<FileInfo>>> iterator = allFiles.entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, List<FileInfo>> entry = iterator.next();
            sb.append("Class File:")
                .append(entry.getKey())
                .append(" Not Used!!");
            printLines(sb.toString());
            sb.delete(0, sb.length());
        }
        return EXIT_OK;
    }

    /**
     * 循环迭代，将从class开始，所有与class相关的class以及其引用的子类从所有文件索引中移除。
     * 最终留下来的就是没有引用到的class文件
     */
    private void loopClass(Map<String, List<FileInfo>> allFiles, List<String> list) {
        for (String str : list) {
            List<FileInfo> fileInfos = allFiles.get(str);
            if (fileInfos != null && fileInfos.size() > 0) {
                FileInfo fileInfo = fileInfos.get(0);
                File file = fileInfo.getFile();
                List<String> clazz = ClassObject.create(file).getClassInfoByConstantPool();
                List<String> clazzPath = dealWithClass(clazz);

                allFiles.remove(str);
                loopClass(allFiles, clazzPath);
            }
        }
    }

    /**
     * 将Class常量池中的Class的格式由com/dust/Controller转化为dust.Controller
     */
    private static List<String> dealWithClass(List<String> list) {
        return list.stream().map(s -> s.split("/"))
                .filter(a -> a.length >= TAG)
                .map(CleanTask::subEnd)
                .collect(Collectors.toList());
    }

    /**
     * 将String[]转变成com.dust.Controller格式
     */
    private static String subEnd(String[] a) {
        StringBuilder sb = new StringBuilder();
        for (int i = a.length - TAG; i < a.length; i++) {
            sb.append(a[i]);
            if (i != a.length - 1) {
                sb.append(".");
            }
        }
        String str = sb.toString();
        if (str.endsWith(".class")) {
            str = str.substring(0, str.length() - 6);
        }
        return str;
    }

    /**
     * 文件类信息
     */
    static class FileInfo {
        private String clazz;

        private File file;

        public FileInfo(String clazz, File file) {
            this.clazz = clazz;
            this.file = file;
        }

        public String getClazz() {
            return clazz;
        }

        public void setClazz(String clazz) {
            this.clazz = clazz;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }

    private void handleOptions(String[] args) {
        if (args.length != 2) {
            showHelp();
            return;
        }
        dirs = args[0];
        bootstrapDirs = args[1];
    }


    private PrintWriter getPrintWriterForStream(OutputStream out) {
        return new PrintWriter(out == null ? System.err : out, true);
    }


    private String getMessage(String key, Object... args) {
        return getMessage(locale, key, args);
    }

    private String getMessage(Locale locale, String key,
                              Object... args) {
        if (locale == null) {
            locale = Locale.getDefault();
        }

        //本地化，根据执行机器的Local来判断生成的message
        ResourceBundle bundle = null;
        try {
            bundle = ResourceBundle.getBundle("com.dust.cc.resource.cclean", locale);
        } catch (MissingResourceException e) {
            throw new InternalError("can not found classClean resource bundle for local " + locale);
        }

        try {
            return MessageFormat.format(bundle.getString(key), args);
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showHelp() {
        printLines(getMessage("main.usage", "cclean"));
    }

    private void showFileError(String fileName, Exception e) {
        printLines(getMessage("file.error", fileName));
        printLines(getMessage("file.error.info", e.getMessage()));
    }

    private void showResult(List<String> result) {

    }

    private void printLines(String msg) {
        log.println(msg.replace("\n", nl));
    }



}
