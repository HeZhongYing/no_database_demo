package com.hezy.service;

import java.util.List;

/**
 * 文件处理服务
 * 作用：1）加载数据；2）持久化数据
 *
 * @author hezhongying
 * @version 1.0.0
 * @create 2026/2/8 18:38
 */
public interface FileHandlerService {

    /**
     * 加载数据
     * @param filePath 文件路径
     * @param clazz 数据类型
     * @return 数据列表
     */
    <T> List<T> loadData(String filePath, Class<T> clazz);

    /**
     * 持久化数据
     * @param data 数据列表
     * @param filePath 文件路径
     * @param clazz 数据类型
     */
    <T> void saveData(List<T> data, String filePath, Class<T> clazz);
}
