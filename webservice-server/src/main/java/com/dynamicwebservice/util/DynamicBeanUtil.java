package com.dynamicwebservice.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;

public class DynamicBeanUtil {
    private final DefaultListableBeanFactory beanFactory;

    public DynamicBeanUtil(ApplicationContext context) {
        this.beanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
    }

    public <T> void registerBean(String beanName, Class<T> beanClass) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    public <T> T getBean(String beanName, Class<T> beanClass) {
        return beanFactory.getBean(beanName, beanClass);
    }

    public boolean containsBean(String beanName) {
        return beanFactory.containsBean(beanName);
    }
}
