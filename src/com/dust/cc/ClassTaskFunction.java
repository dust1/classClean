package com.dust.cc;

import java.util.List;

/**
 * 分析单个class文件的任务对象
 */
public interface ClassTaskFunction {

    /**
     * 执行CLass文件分析，并返回常量池中的类
     */
    List<String> run();

}
