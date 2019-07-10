package com.dust.cc;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 单个class文件的包装类
 */
public class ClassObject {

    private ClassStream classStream;

    private int magic;

    private int mionr_version;

    private int major_version;

    private ConstantPool pool;

    public static ClassObject create(File file) {
        return new ClassObject(file);
    }

    public static List<ClassObject> create(List<File> list) {
        List<ClassObject> result = new ArrayList<>(list.size());
        for (File file : list) {
            result.add(create(file));
        }
        return result;
    }

    private ClassObject(File file) {
        try {
            openStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开流
     */
    private void openStream(File file) throws IOException {
        this.classStream = ClassStream.create(file);
        initParameters();
    }

    /**
     * 初始化相关参数
     */
    private void initParameters() throws IOException {
        magic = classStream.readInt();       //长度是4字节
        mionr_version = classStream.readUnsignedShort();     //长度是2字节
        major_version = classStream.readUnsignedShort();     //转10进制
        pool = new ConstantPool(classStream);
    }

    /**
     * 从常量池中获取所有Class_info类型
     * 返回例子：
     * java/util/ArrayList
     * Test
     * java/lang/Object
     */
    public List<String> getClassInfoByConstantPool() {
        return pool.getCPList(ConstantPool.CONSTANT_Class).stream()
                .map(c -> (ConstantPool.CONSTANT_Class_info)c)
                .map(ConstantPool.CONSTANT_Class_info::getBaseName)
                .collect(Collectors.toList());
    }

    public void close() {
        try {
            classStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getMagic() {
        return magic;
    }

    public int getMionr_version() {
        return mionr_version;
    }

    public int getMajor_version() {
        return major_version;
    }

}
