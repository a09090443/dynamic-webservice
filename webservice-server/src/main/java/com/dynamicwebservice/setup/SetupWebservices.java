package com.dynamicwebservice.setup;

import com.dynamicwebservice.entity.EndpointEntity;
import com.dynamicwebservice.entity.JarFileEntity;
import com.dynamicwebservice.repository.EndpointRepository;
import com.dynamicwebservice.repository.JarFileRepository;
import com.dynamicwebservice.util.WebServiceHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class SetupWebservices implements SmartLifecycle {

    private final EndpointRepository endpintRepository;

    private final ApplicationContext context;

    private final JarFileRepository jarFileRepository;

    public SetupWebservices(EndpointRepository endpintRepository, ApplicationContext context, JarFileRepository jarFileRepository) {
        this.endpintRepository = endpintRepository;
        this.context = context;
        this.jarFileRepository = jarFileRepository;
    }

    @Override
    public void start() {
        List<EndpointEntity> endpointEntities = endpintRepository.findAllByIsActive(true);
        WebServiceHandler registerWebService = new WebServiceHandler();
        AtomicReference<JarFileEntity> jarFileEntity = new AtomicReference<>();
                Optional.ofNullable(endpointEntities).ifPresent(entities -> entities.forEach(entity -> {
            try {
                jarFileEntity.set(jarFileRepository.findById(entity.getJarFileId()).orElseThrow(() -> new FileNotFoundException("找不到對應的 Jar 檔案")));

                registerWebService.registerWebService(entity, context, jarFileEntity.get().getName());
            } catch (Exception e) {
                log.error("Web Service 註冊服務:{}, 失敗", entity.getBeanName(), e);
            }
        }));
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
