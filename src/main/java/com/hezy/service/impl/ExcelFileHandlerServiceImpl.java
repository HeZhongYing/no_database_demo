package com.hezy.service.impl;

import com.alibaba.excel.EasyExcel;
import com.hezy.service.AbstractFileHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Excel 文件处理服务实现类
 *
 * @author hezhongying
 * @version 1.0.0
 * @create 2026/2/8 18:43
 */
@Service
@Slf4j
public class ExcelFileHandlerServiceImpl extends AbstractFileHandlerService {

    @Override
    public <T> List<T> loadData(String filePath, Class<T> clazz) {
        try {
            log.info("开始读取 Excel 文件: {}", filePath);
            List<T> dataList = EasyExcel.read(filePath).head(clazz).sheet().doReadSync();
            log.info("Excel 文件读取完成，共 {} 条数据", dataList.size());
            return dataList;
        } catch (Exception e) {
            log.error("读取 Excel 文件失败: {}", filePath, e);
            return Collections.emptyList();
        }
    }

    @Override
    public <T> void saveData(List<T> data, String filePath, Class<T> clazz) {
        try {
            log.info("开始写入 Excel 文件: {}", filePath);
            EasyExcel.write(filePath, clazz).sheet().doWrite(data);
            log.info("Excel 文件写入完成，共 {} 条数据", data.size());
        } catch (Exception e) {
            log.error("写入 Excel 文件失败: {}", filePath, e);
        }
    }
}
