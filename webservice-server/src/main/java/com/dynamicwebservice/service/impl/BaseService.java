package com.dynamicwebservice.service.impl;

import com.dynamicwebservice.entity.JarFileEntity;
import com.dynamicwebservice.exception.WebserviceException;
import com.dynamicwebservice.repository.JarFileRepository;
import com.dynamicwebservice.util.DynamicClassLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

public abstract class BaseService {
    protected ApplicationContext context;

    protected JarFileRepository jarFileRepository;

    protected DynamicClassLoader dynamicClassLoader;

    protected String jarFileDir;

    protected JarFileEntity getJarFile(String jarFileId) {
        return jarFileRepository.findById(jarFileId).orElseThrow(() -> new WebserviceException("找不到對應的 Jar 檔案"));
    }

    protected String getJarFilePath(String fileName) {
        return "file:" + jarFileDir + fileName;
    }

    @Autowired
    public void setJarFileRepository(JarFileRepository jarFileRepository) {
        this.jarFileRepository = jarFileRepository;
    }

    @Autowired
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Autowired
    public void setDynamicClassLoader(DynamicClassLoader dynamicClassLoader) {
        this.dynamicClassLoader = dynamicClassLoader;
    }

    @Value("${jar.file.dir}")
    public void setJarFileDir(String jarFileDir) {
        this.jarFileDir = jarFileDir;
    }
}
