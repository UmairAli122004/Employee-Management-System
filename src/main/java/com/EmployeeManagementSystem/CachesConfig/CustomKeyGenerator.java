package com.EmployeeManagementSystem.CachesConfig;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Component("customKeyGenerator")
public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {

        StringBuilder key = new StringBuilder(method.getName()).append("::");

        for (Object param : params) {

            if(param == null) continue;

            //Handle Pageable safely
            if (param instanceof Pageable pageable) {
                key.append("page=").append(pageable.getPageNumber())
                        .append("-size=").append(pageable.getPageSize())
                        .append("-sort=").append(formatSort(pageable.getSort()));
            }

            //Handle List safely (IMPORTANT FIX)
            else if (param instanceof List<?> list) {
                String listKey = list.stream()
                        .map(Object::toString) // must override toString in DTO
                        .sorted() // ensures same order
                        .collect(Collectors.joining(","));
                key.append(listKey);
            }

            //Handle primitives & strings
            else {
                key.append(param.toString());
            }
            key.append("|");
        }
        return key.toString();
    }


    private String formatSort(Sort sort) {
        return sort.stream()
                .map(order -> order.getProperty() + ":" + order.getDirection())
                .sorted()
                .collect(Collectors.joining(","));
    }
}