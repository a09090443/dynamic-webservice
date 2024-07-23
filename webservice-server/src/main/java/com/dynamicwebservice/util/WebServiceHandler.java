package com.dynamicwebservice.util;

import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.exception.WebserviceException;
import com.zipe.util.string.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.URL;

@Slf4j
public class WebServiceHandler {
    public void registerWebService(DynamicClassLoader classLoader, EndpointDTO endpointDTO, ApplicationContext context, String fileName) throws IOException, ClassNotFoundException {

        String jarPath = "file:" + context.getEnvironment().getProperty("jar.file.dir") + fileName;
        DynamicBeanUtil dynamicBeanUtil = new DynamicBeanUtil(context);
        try {
            classLoader.addURL(new URL(jarPath));
            Class<?> loadedClass = classLoader.loadClass(endpointDTO.getClassPath());
            this.setBeanName(context, endpointDTO.getBeanName(), loadedClass);
            EndpointImpl endpoint;
            endpoint = new EndpointImpl(context.getBean(Bus.class), dynamicBeanUtil.getBean(endpointDTO.getBeanName(), loadedClass));
            endpoint.publish(StringConstant.SLASH + endpointDTO.getPublishUri());
            log.info("Web Service 註冊服務:{}, 對應 URI:{}", endpointDTO.getBeanName(), endpointDTO.getPublishUri());
        } catch (Exception e) {
            log.error("Web Service 註冊服務:{}, 失敗", endpointDTO.getBeanName(), e);
            throw new WebserviceException("Web Service 註冊服務失敗");
        }
    }

    public void removeWebService(DynamicClassLoader classLoader, String publicUrl, Bus bus, ApplicationContext context, String jarName) {
        ServerRegistry serverRegistry = bus.getExtension(ServerRegistry.class);

        serverRegistry.getServers().stream()
                .filter(server -> server.getEndpoint().getEndpointInfo().getAddress().endsWith(publicUrl))
                .findFirst()
                .ifPresent(server -> {
                    server.stop();
                    server.destroy();
                    serverRegistry.getServers().remove(server);
                });
        String jarPath = "file:" + context.getEnvironment().getProperty("jar.file.dir") + jarName;
        try {
            classLoader.unloadJarFile(new URL(jarPath));
        } catch (Exception e) {
            log.error("Web Service 移除服務:{}, 失敗", publicUrl, e);
            throw new WebserviceException("移除服務失敗");
        }
    }

    private void setBeanName(ApplicationContext context, String beanName, Class<?> clazz) {
        DynamicBeanUtil dynamicBeanUtil = new DynamicBeanUtil(context);
        dynamicBeanUtil.registerBean(beanName, clazz);
    }
}
