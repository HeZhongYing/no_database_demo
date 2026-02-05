package com.hezy.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

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

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // todo
        log.info("项目启动, 正在从外部文件中加载数据");
    }
}
