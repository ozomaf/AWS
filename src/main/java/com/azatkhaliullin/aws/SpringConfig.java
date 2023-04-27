package com.azatkhaliullin.aws;

import com.azatkhaliullin.aws.domain.AmazonPolly;
import com.azatkhaliullin.aws.domain.AmazonTranslate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class SpringConfig {

    @Bean
    public AmazonTranslate amazonTranslate(ThreadPoolExecutor translateThreadPoolExecutor) {
        return new AmazonTranslate(translateThreadPoolExecutor);
    }

    @Bean
    public ThreadPoolExecutor translateThreadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

    @Bean
    public AmazonPolly amazonPolly(ThreadPoolExecutor pollyThreadPoolExecutor) {
        return new AmazonPolly(pollyThreadPoolExecutor);
    }

    @Bean
    public ThreadPoolExecutor pollyThreadPoolExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    }

}