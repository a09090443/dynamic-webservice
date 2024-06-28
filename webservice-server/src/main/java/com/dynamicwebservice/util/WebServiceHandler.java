package com.dynamicwebservice.util;

import com.dynamicwebservice.dto.EndpointDTO;
import com.zipe.util.classloader.CustomClassLoader;
import com.zipe.util.string.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.ApplicationContext;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class WebServiceHandler {
    public void registerWebService(EndpointDTO endpointDTO, ApplicationContext context, String fileName) throws MalformedURLException, ClassNotFoundException {

        String jarPath = "file:" + context.getEnvironment().getProperty("jar.file.dir") + fileName;
        CustomClassLoader loader = new CustomClassLoader(new URL[]{new URL(jarPath)}, this.getClass().getClassLoader());
        DynamicBeanUtil dynamicBeanUtil = new DynamicBeanUtil(context);
        EndpointImpl endpoint;
        try {
            Class<?> loadedClass = loader.loadClass(endpointDTO.getClassPath());
            this.setBeanName(context, endpointDTO.getBeanName(), loadedClass);

            endpoint = new EndpointImpl(context.getBean(Bus.class), dynamicBeanUtil.getBean(endpointDTO.getBeanName(), loadedClass));
            endpoint.publish(StringConstant.SLASH + endpointDTO.getPublishUrl());
            log.info("Web Service 註冊服務:{}, 對應 URI:{}", endpointDTO.getBeanName(), endpointDTO.getPublishUrl());
        } catch (Exception e) {
            log.error("Web Service 註冊服務:{}, 失敗", endpointDTO.getBeanName(), e);
            throw e;
        }
    }

    public void removeWebService(String publicUrl, Bus bus, ApplicationContext context, String jarName) throws Exception {
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
        CustomClassLoader loader;
        try {
            loader = new CustomClassLoader(new URL[]{new URL(jarPath)}, this.getClass().getClassLoader());
            loader.unloadJarFile(new URL(jarPath));

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } finally {
            loader = null; // Set the class loader to null
        }

    }

    private void setBeanName(ApplicationContext context, String beanName, Class<?> clazz) {
        DynamicBeanUtil dynamicBeanUtil = new DynamicBeanUtil(context);
        dynamicBeanUtil.registerBean(beanName, clazz);
    }
}
