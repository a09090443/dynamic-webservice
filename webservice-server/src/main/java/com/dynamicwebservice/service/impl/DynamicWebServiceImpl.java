package com.dynamicwebservice.service.impl;

import com.dynamicwebservice.dao.MockResponseDao;
import com.dynamicwebservice.dto.EndpointResponse;
import com.dynamicwebservice.dto.MockResponseRequest;
import com.dynamicwebservice.dto.WebServiceRequest;
import com.dynamicwebservice.entity.EndpointEntity;
import com.dynamicwebservice.entity.JarFileEntity;
import com.dynamicwebservice.entity.MockResponseEntity;
import com.dynamicwebservice.enums.JarFileStatus;
import com.dynamicwebservice.jdbc.JarFileJDBC;
import com.dynamicwebservice.model.WebServiceModel;
import com.dynamicwebservice.repository.EndpointRepository;
import com.dynamicwebservice.repository.JarFileRepository;
import com.dynamicwebservice.repository.MockResponseRepository;
import com.dynamicwebservice.service.DynamicWebService;
import com.dynamicwebservice.util.WebServiceHandler;
import com.zipe.enums.ResourceEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DynamicWebServiceImpl implements DynamicWebService {

    private final ApplicationContext context;

    private final Bus bus;

    private final MockResponseRepository mockResponseRepository;

    private final MockResponseDao mockResponseDao;

    private final EndpointRepository endpointRepository;

    private final JarFileRepository jarFileRepository;

    private final JarFileJDBC jarFileJDBC;

    private final String jarFileDir;

    DynamicWebServiceImpl(ApplicationContext context,
                          Bus bus,
                          MockResponseRepository mockResponseRepository,
                          MockResponseDao mockResponseDao,
                          EndpointRepository endpointRepository,
                          JarFileRepository jarFileRepository, JarFileJDBC jarFileJDBC,
                          @Value("${jar.file.dir}") String jarFileDir) {
        this.context = context;
        this.bus = bus;
        this.mockResponseRepository = mockResponseRepository;
        this.mockResponseDao = mockResponseDao;
        this.endpointRepository = endpointRepository;
        this.jarFileRepository = jarFileRepository;
        this.jarFileJDBC = jarFileJDBC;
        this.jarFileDir = jarFileDir;
    }

    @Override
    public List<EndpointResponse> getEndpoints() {

        ResourceEnum resource = ResourceEnum.SQL.getResource(JarFileJDBC.SQL_SELECT_ENDPOINT_RELATED_JAR_FILE);
        List<WebServiceModel> webServiceModelList = jarFileJDBC.queryForList(resource, new HashMap<>(), WebServiceModel.class);

        return webServiceModelList.stream().map(endpoint -> {
            EndpointResponse response = new EndpointResponse();
            response.setPublishUrl(endpoint.getPublishUrl());
            response.setClassPath(endpoint.getClassPath());
            response.setBeanName(endpoint.getBeanName());
            response.setIsActive(endpoint.getIsActive());
            response.setJarFileId(endpoint.getJarFileId());
            response.setJarFileName(endpoint.getJarFileName());
            response.setFileStatus(endpoint.getFileStatus());
            return response;
        }).toList();
    }

    @Override
    public String getResponseContent(MockResponseRequest request) {
        try {
            return mockResponseDao.findByPrimaryKey(request.getPublishUrl(), request.getMethod(), request.getCondition());
        } catch (IncorrectResultSizeDataAccessException e) {
            log.warn("查無對應的 Mock Response 資料");
            return "";
        }
    }

    @Override
    public void registerWebService(WebServiceRequest request) throws MalformedURLException, ClassNotFoundException, FileNotFoundException {
        WebServiceHandler registerWebService = new WebServiceHandler();
        try {

            EndpointEntity endpointEntity = new EndpointEntity();
            endpointEntity.setPublishUrl(request.getPublishUrl());
            endpointEntity.setClassPath(request.getClassPath());
            endpointEntity.setBeanName(request.getBeanName());
            endpointEntity.setIsActive(Boolean.TRUE);

            JarFileEntity jarFileEntity = jarFileRepository.findById(request.getFileId()).orElseThrow(() -> new FileNotFoundException("找不到對應的 Jar 檔案"));
            endpointEntity.setJarFileId(jarFileEntity.getId());

            registerWebService.registerWebService(endpointEntity, context, jarFileEntity.getName());

            jarFileEntity.setStatus(JarFileStatus.ACTIVE);
            jarFileRepository.save(jarFileEntity);

            endpointRepository.save(endpointEntity);
        } catch (Exception e) {
            log.error("註冊 Web Service 失敗", e);
            throw e;
        }
    }

    @Override
    public void removeWebService(WebServiceRequest request) {
        WebServiceHandler registerWebService = new WebServiceHandler();
        try {

            EndpointEntity endpointEntity = endpointRepository.findById(request.getPublishUrl()).orElseThrow(() -> new FileNotFoundException("找不到對應的 Web Service"));

            JarFileEntity jarFileEntity = jarFileRepository.findById(endpointEntity.getJarFileId()).orElseThrow(() -> new FileNotFoundException("找不到對應的 Jar 檔案"));
            registerWebService.removeWebService(request.getPublishUrl(), bus, context, jarFileEntity.getName());

            jarFileEntity.setStatus(JarFileStatus.INACTIVE);
            jarFileRepository.save(jarFileEntity);

            endpointRepository.delete(endpointEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateWebService(WebServiceRequest request) {

    }

    @Override
    public void addMockResponse(MockResponseRequest request) {
        MockResponseEntity mockResponseEntity = new MockResponseEntity();
        mockResponseEntity.setPublishUrl(request.getPublishUrl());
        mockResponseEntity.setMethod(request.getMethod());
        mockResponseEntity.setCondition(request.getCondition());
        mockResponseEntity.setResponseContent(request.getResponseContent());
        mockResponseEntity.setIsActive(Boolean.TRUE);
        mockResponseRepository.save(mockResponseEntity);
    }

    @Override
    public String uploadJarFile(InputStream inputStream) throws IOException {

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
        return jarFileEntity.getId();
    }

}
