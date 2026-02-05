package com.hezy.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

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

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // todo
        log.info("项目停止, 正在往外部文件中写数据");
    }
}
