package com.dynamicwebservice.service.impl;


import com.dynamicwebservice.dto.JarFileResponseDTO;
import com.dynamicwebservice.entity.JarFileEntity;
import com.dynamicwebservice.enums.JarFileStatus;
import com.dynamicwebservice.exception.ControllerException;
import com.dynamicwebservice.repository.JarFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
public abstract class CommonService {

    protected String jarFileDir;

    private JarFileRepository jarFileRepository;


    public JarFileResponseDTO uploadJarFile(InputStream inputStream) {
        JarFileResponseDTO jarFileResponse;

        try {
            // 確保上傳目錄存在
            Path uploadPath = Paths.get(jarFileDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String newFileName = UUID.randomUUID() + ".jar";
            // 儲存檔案到本地檔案系統
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            JarFileEntity jarFileEntity = new JarFileEntity();
            jarFileEntity.setName(newFileName);
            jarFileEntity.setStatus(JarFileStatus.INACTIVE);
            jarFileEntity = jarFileRepository.save(jarFileEntity);

            jarFileResponse = new JarFileResponseDTO();
            jarFileResponse.setJarFileId(jarFileEntity.getId());
            jarFileResponse.setJarFileName(jarFileEntity.getName());
        } catch (IOException e) {
            log.error("Upload jar file failed: {}", e.getMessage());
            throw new ControllerException("Upload jar file failed: " + e.getMessage());
        }
        return jarFileResponse;
    }

    @Autowired
    public void setJarFileRepository(JarFileRepository jarFileRepository) {
        this.jarFileRepository = jarFileRepository;
    }

    @Value("${jar.file.dir}")
    public void setJarFileDir(String jarFileDir) {
        this.jarFileDir = jarFileDir;
    }
}
