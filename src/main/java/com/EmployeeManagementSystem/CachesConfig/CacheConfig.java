package com.EmployeeManagementSystem.CachesConfig;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching(proxyTargetClass = true)
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "employeeById",
                "employeeList",
                "employeeSearchPage",
                "employeeSearchList",
                "employeePage",
                "employeeFilter",
                "employeeSummary",
                "employeeAuth"
        );

        cacheManager.setAllowNullValues(true);
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .initialCapacity(100)
                        .maximumSize(10_000)
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                    //    .expireAfterAccess(5, TimeUnit.MINUTES) //Entry expires if not accessed for 5 minutes
                        .recordStats()
        );
        return cacheManager;
    }
}