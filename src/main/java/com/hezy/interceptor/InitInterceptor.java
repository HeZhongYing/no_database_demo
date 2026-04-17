package com.hezy.interceptor;

import com.hezy.dal.redis.UserRedisRepository;
import com.hezy.pojo.UserDO;
import com.hezy.service.impl.ExcelFileHandlerServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * 项目初始化
 * 作用：在项目启动时，将数据加载到本地缓存中
 *
 * @author hezy
 * @version 1.0.0
 * @create 2026/2/4 21:33
 */
@Component
@Slf4j
public class InitInterceptor implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${app.data.file-path:data/users.xlsx}")
    private String userDataFilePath;

    @Resource
    private ExcelFileHandlerServiceImpl excelFileHandlerService;

    @Resource
    private UserRedisRepository userRedisRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("项目启动, 正在从外部文件中加载数据");

        // 加载用户数据
        loadUserData();

        log.info("数据加载完成");
    }

    private void loadUserData() {
        try {
            File file = new File(userDataFilePath);
            if (!file.exists()) {
                log.warn("用户数据文件不存在: {}，跳过加载用户数据", userDataFilePath);
                return;
            }

            List<UserDO> userList = excelFileHandlerService.loadData(userDataFilePath, UserDO.class);
            if (userList.isEmpty()) {
                log.info("用户数据文件为空，跳过加载");
                return;
            }

            // 先清空 Redis 中的旧数据
            userRedisRepository.clearAll();

            // 保存新数据到 Redis
            userRedisRepository.saveAll(userList);
            log.info("用户数据加载成功，共 {} 条", userList.size());
        } catch (Exception e) {
            log.error("加载用户数据失败", e);
        }
    }
}
