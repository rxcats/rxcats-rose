package io.github.rxcats.rose.chat.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableScheduling
@EnableAsync
@Configuration
public class ExecutorConfig {

    @Bean
    public Executor threadPoolTaskExecutor() {
        var pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(5);
        pool.setMaxPoolSize(20);
        pool.setQueueCapacity(50);
        pool.setThreadNamePrefix("executor-");
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        pool.initialize();
        return pool;
    }

    @Bean
    public Executor scheduler() {
        var pool = new ThreadPoolTaskScheduler();
        pool.setPoolSize(2);
        pool.setThreadNamePrefix("scheduler-");
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        pool.initialize();
        return pool;
    }

}
