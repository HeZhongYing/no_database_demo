package com.hezy.interceptor;

import com.hezy.dal.redis.UserRedisRepository;
import com.hezy.pojo.UserDO;
import com.hezy.service.impl.ExcelFileHandlerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * 停机拦截器
 * 作用：在项目停止时，将数据写入到外部文件中
 *
 * @author hezy
 * @version 1.0.0
 * @create 2026/2/4 21:36
 */
@Component
@Slf4j
public class FinishedInterceptor implements ApplicationListener<ContextClosedEvent> {

    @Value("${app.data.file-path:data/users.xlsx}")
    private String userDataFilePath;

    @Resource
    private ExcelFileHandlerServiceImpl excelFileHandlerService;

    @Resource
    private UserRedisRepository userRedisRepository;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("项目停止, 正在往外部文件中写数据");

        // 保存用户数据
        saveUserData();

        log.info("数据写入完成");
    }

    private void saveUserData() {
        try {
            // 从 Redis 获取所有用户数据
            List<UserDO> userList = userRedisRepository.getAll();
            if (userList.isEmpty()) {
                log.info("Redis 中没有用户数据，跳过保存");
                return;
            }

            // 确保目录存在
            File file = new File(userDataFilePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (created) {
                    log.info("创建数据目录: {}", parentDir.getAbsolutePath());
                }
            }

            // 保存到 Excel 文件
            excelFileHandlerService.saveData(userList, userDataFilePath, UserDO.class);
            log.info("用户数据保存成功，共 {} 条", userList.size());
        } catch (Exception e) {
            log.error("保存用户数据失败", e);
        }
    }
}
