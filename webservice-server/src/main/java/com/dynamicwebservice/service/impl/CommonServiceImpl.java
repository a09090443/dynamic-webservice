package com.dynamicwebservice.service.impl;

import com.dynamicwebservice.dto.JarFileResponseDTO;
import com.dynamicwebservice.dto.MockResponseRequestDTO;
import com.dynamicwebservice.dto.MockResponseResponseDTO;
import com.dynamicwebservice.entity.JarFileEntity;
import com.dynamicwebservice.entity.MockResponseEntity;
import com.dynamicwebservice.enums.JarFileStatus;
import com.dynamicwebservice.exception.WebserviceException;
import com.dynamicwebservice.jdbc.MockResponseJDBC;
import com.dynamicwebservice.repository.JarFileRepository;
import com.dynamicwebservice.repository.MockResponseRepository;
import com.dynamicwebservice.service.CommonService;
import com.zipe.enums.ResourceEnum;
import com.zipe.util.time.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommonServiceImpl implements CommonService {

    private final String jarFileDir;

    private final JarFileRepository jarFileRepository;

    private final MockResponseRepository mockResponseRepository;

    private final MockResponseJDBC mockResponseJDBC;

    private final ResourceLoader resourceLoader;

    public CommonServiceImpl(@Value("${jar.file.dir}") String jarFileDir,
                             JarFileRepository jarFileRepository,
                             MockResponseRepository mockResponseRepository, MockResponseJDBC mockResponseJDBC, ResourceLoader resourceLoader) {
        this.jarFileDir = jarFileDir;
        this.jarFileRepository = jarFileRepository;
        this.mockResponseRepository = mockResponseRepository;
        this.mockResponseJDBC = mockResponseJDBC;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public JarFileResponseDTO uploadJarFile(InputStream inputStream) throws IOException {
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

        JarFileResponseDTO jarFileResponse = new JarFileResponseDTO();
        jarFileResponse.setJarFileId(jarFileEntity.getId());
        jarFileResponse.setJarFileName(jarFileEntity.getName());
        return jarFileResponse;
    }

    @Override
    public String getResponseContent(MockResponseRequestDTO request) {

        if (StringUtils.isBlank(request.getPublishUri())) {
            throw new WebserviceException("Publish URL is required");
        } else if (StringUtils.isBlank(request.getMethod())) {
            throw new WebserviceException("Method is required");
        } else if (StringUtils.isBlank(request.getCondition())) {
            throw new WebserviceException("Condition is required");
        }
        MockResponseEntity mockResponseEntity = mockResponseRepository.findByIdPublishUriAndIdMethodAndIdConditionAndIsActive(request.getPublishUri(), request.getMethod(), request.getCondition(), Boolean.TRUE);
        return Optional.ofNullable(mockResponseEntity).map(MockResponseEntity::getResponseContent).orElse("");
    }

    @Override
    public List<MockResponseResponseDTO> getResponseList(MockResponseRequestDTO request) {
        if (StringUtils.isBlank(request.getPublishUri())) {
            throw new WebserviceException("Publish URL is required");
        }
        List<MockResponseEntity> mockResponseEntity = mockResponseRepository.findByIdPublishUri(request.getPublishUri());
        return mockResponseEntity.stream().map(mockResponse -> {
            MockResponseResponseDTO response = new MockResponseResponseDTO();
            response.setId(mockResponse.getUuId());
            response.setPublishUri(mockResponse.getId().getPublishUri());
            response.setMethod(mockResponse.getId().getMethod());
            response.setCondition(mockResponse.getId().getCondition());
            response.setResponseContent(mockResponse.getResponseContent());
            response.setIsActive(mockResponse.getIsActive());
            return response;
        }).toList();
    }

    @Override
    public void saveMockResponse(MockResponseRequestDTO request) {
        MockResponseEntity mockResponseEntity = new MockResponseEntity();
        mockResponseEntity.setUuId(UUID.randomUUID().toString());
        mockResponseEntity.getId().setPublishUri(request.getPublishUri());
        mockResponseEntity.getId().setMethod(request.getMethod());
        mockResponseEntity.getId().setCondition(request.getCondition());
        mockResponseEntity.setResponseContent(request.getResponseContent());
        mockResponseEntity.setIsActive(Boolean.FALSE);
        mockResponseRepository.save(mockResponseEntity);
        request.setId(mockResponseEntity.getUuId());
    }

    @Override
    public void updateMockResponse(MockResponseRequestDTO request) {
        ResourceEnum resource = ResourceEnum.SQL.getResource(MockResponseJDBC.SQL_UPDATE_RESPONSE);
        String sql = readFileFromJar(resource);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("publishUri", request.getPublishUri());
        paramMap.put("method", request.getMethod());
        paramMap.put("condition", request.getCondition());
        paramMap.put("responseContent", request.getResponseContent());
        paramMap.put("id", request.getId());
        paramMap.put("updatedAt", DateTimeUtils.getDateNow());
        try {
            mockResponseJDBC.update(sql, paramMap);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("publishUrl:{}", request.getPublishUri());
            log.error("method:{}", request.getMethod());
            log.error("condition:{}", request.getCondition());
            log.error("responseContent:{}", request.getResponseContent());
            throw new WebserviceException("更新 Mock Response 失敗");
        }
    }

    @Override
    public void updateMockResponse(String oriPublishUri, String newPublishUri) {
        ResourceEnum resource = ResourceEnum.SQL.getResource(MockResponseJDBC.SQL_UPDATE_PUBLISH_URI_FOR_RESPONSE);
        String sql = readFileFromJar(resource);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("oriPublishUri", oriPublishUri);
        paramMap.put("newPublishUri", newPublishUri);
        paramMap.put("updatedAt", DateTimeUtils.getDateNow());
        try {
            mockResponseJDBC.update(sql, paramMap);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("oriPublishUri:{}", oriPublishUri);
            log.error("newPublishUri:{}", newPublishUri);
            log.error("IncorrectResultSizeDataAccessException:{}", e.getMessage(), e);
            throw new WebserviceException("更新 Mock Response 失敗");
        }
    }

    @Override
    public void deleteMockResponse(String id) {
        ResourceEnum resource = ResourceEnum.SQL.getResource(MockResponseJDBC.SQL_DEL_RESPONSE);
        String sql = readFileFromJar(resource);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        try {
            mockResponseJDBC.update(sql, paramMap);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("id:{}", id);
            log.error("IncorrectResultSizeDataAccessException:{}", e.getMessage(), e);
            throw new WebserviceException("刪除 Mock Response 失敗");
        }
    }

    @Override
    public void switchMockResponse(String id, Boolean status) {
        MockResponseEntity mockResponseEntity = mockResponseRepository.findByUuId(id);
        mockResponseEntity.setIsActive(status);
        try {
            mockResponseRepository.save(mockResponseEntity);
        } catch (Exception e) {
            throw new WebserviceException("切換 Mock Response 狀態失敗");
        }
    }

    private String readFileFromJar(ResourceEnum resource) {
        StringBuilder path = new StringBuilder();
        path.append(resource.dir());
        path.append(resource.file());
        path.append(resource.extension());
        try {
            // 使用 classpath: 前綴來指定文件在 JAR 內的路徑
            Resource fileResource = resourceLoader.getResource("classpath:" + path.toString());

            // 檢查資源是否存在
            if (!fileResource.exists()) {
                throw new WebserviceException("File not found: " + path.toString());
            }

            // 讀取文件內容
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fileResource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new WebserviceException("Error reading file from JAR: " + path.toString(), e);
        }
    }
}
