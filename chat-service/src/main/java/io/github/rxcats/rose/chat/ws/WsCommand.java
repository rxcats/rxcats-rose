package io.github.rxcats.rose.chat.ws;

import java.lang.reflect.Method;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WsCommand extends HystrixCommand<Object> {

    private Method method;
    private Object clazz;
    private Object[] args;

    public WsCommand(Object clazz, Method method, Object[] args, HystrixThreadGroup threadGroup) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(clazz.getClass().getSimpleName()))
            .andCommandKey(HystrixCommandKey.Factory.asKey(method.getClass().getSimpleName()))
            .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(threadGroup.getGroupName()))
            .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                .withMaximumSize(threadGroup.getThreadSize()).withMaxQueueSize(threadGroup.getQueueSize()))
            .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(threadGroup.getTimeout()))
        );
        this.method = method;
        this.clazz = clazz;
        this.args = args;
    }

    @Override
    protected Object run() throws Exception {
        return method.invoke(clazz, args);
    }

    @Override
    protected Object getFallback() {
        log.warn("Unable execute command");
        return null;
    }

}
