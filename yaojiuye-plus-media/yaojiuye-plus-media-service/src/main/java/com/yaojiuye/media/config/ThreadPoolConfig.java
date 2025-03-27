package com.yaojiuye.media.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Value("${task.pool.core-size}")
    private int corePoolSize;

    @Value("${task.pool.max-size}")
    private int maxPoolSize;

    @Value("${task.pool.queue-capacity}")
    private int queueCapacity;

    @Value("${task.pool.keep-alive-seconds}")
    private int keepAliveSeconds;

    @Value("${task.pool.thread-name-prefix}")
    private String threadNamePrefix;

    @Value("${task.pool.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Bean(name = "ClearChunkFilesTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(corePoolSize);
        // 最大线程数
        executor.setMaxPoolSize(maxPoolSize);
        // 队列容量
        executor.setQueueCapacity(queueCapacity);
        // 线程空闲时间
        executor.setKeepAliveSeconds(keepAliveSeconds);
        // 线程名前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务完成再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待终止的超时时间
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);

        executor.initialize();
        return executor;
    }
}