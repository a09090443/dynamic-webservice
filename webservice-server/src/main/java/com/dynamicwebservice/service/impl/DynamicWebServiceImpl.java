package com.dynamicwebservice.service.impl;

import com.dynamicwebservice.dao.MockResponseDao;
import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.dto.EndpointResponse;
import com.dynamicwebservice.dto.JarFileResponse;
import com.dynamicwebservice.dto.MockResponseRequest;
import com.dynamicwebservice.dto.MockResponseResponse;
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
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import java.util.Optional;
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
        MockResponseEntity mockResponseEntity = mockResponseRepository.findByPublishUrlAndMethodAndConditionAndIsActive(request.getPublishUrl(), request.getMethod(), request.getCondition(), Boolean.TRUE);
        return Optional.ofNullable(mockResponseEntity).map(MockResponseEntity::getResponseContent).orElse("");
    }

    @Override
    public List<MockResponseResponse> getResponseList(MockResponseRequest request) {
        List<MockResponseEntity> mockResponseEntity = mockResponseRepository.findByPublishUrl(request.getPublishUrl());
        return mockResponseEntity.stream().map(mockResponse -> {
            MockResponseResponse response = new MockResponseResponse();
            response.setPublishUrl(mockResponse.getPublishUrl());
            response.setMethod(mockResponse.getMethod());
            response.setCondition(mockResponse.getCondition());
            response.setResponseContent(mockResponse.getResponseContent());
            response.setIsActive(mockResponse.getIsActive());
            return response;
        }).toList();
    }

    @Override
    public void saveWebService(EndpointDTO endpointDTO) throws FileNotFoundException {

        EndpointEntity endpointEntity = new EndpointEntity();
        endpointEntity.setPublishUrl(endpointDTO.getPublishUrl());
        endpointEntity.setClassPath(endpointDTO.getClassPath());
        endpointEntity.setBeanName(endpointDTO.getBeanName());
        endpointEntity.setIsActive(Boolean.FALSE);
        endpointEntity.setJarFileId(endpointDTO.getJarFileId());

        JarFileEntity jarFileEntity = jarFileRepository.findById(endpointDTO.getJarFileId()).orElseThrow(() -> new FileNotFoundException("找不到對應的 Jar 檔案"));

        jarFileEntity.setStatus(JarFileStatus.ACTIVE);
        try {
            jarFileRepository.save(jarFileEntity);

            endpointRepository.save(endpointEntity);
        } catch (EntityExistsException e) {
            // 處理實體已經存在的異常
            throw new EntityExistsException("實體已經存在：" + e.getMessage());
        } catch (PersistenceException e) {
            // 處理其他持久化異常
            throw new PersistenceException("持久化操作失敗：" + e.getMessage());
        }

        endpointDTO.setJarFileName(jarFileEntity.getName());
    }

    @Override
    public void addMockResponse(MockResponseRequest request) {
        MockResponseEntity mockResponseEntity = new MockResponseEntity();
        mockResponseEntity.setPublishUrl(request.getPublishUrl());
        mockResponseEntity.setMethod(request.getMethod());
        mockResponseEntity.setCondition(request.getCondition());
        mockResponseEntity.setResponseContent(request.getResponseContent());
        mockResponseEntity.setIsActive(Boolean.FALSE);
        mockResponseRepository.save(mockResponseEntity);
    }

    @Override
    public JarFileResponse uploadJarFile(InputStream inputStream) throws IOException {

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

        JarFileResponse jarFileResponse = new JarFileResponse();
        jarFileResponse.setJarFileId(jarFileEntity.getId());
        jarFileResponse.setJarFileName(jarFileEntity.getName());
        return jarFileResponse;
    }

    @Override
    public void enabledWebService(String publishUrl) throws MalformedURLException, ClassNotFoundException, FileNotFoundException {
        EndpointEntity endpointEntity = endpointRepository.findById(publishUrl).orElseThrow(() -> new FileNotFoundException("找不到對應的 Web Service"));
        JarFileEntity jarFileEntity = jarFileRepository.findById(endpointEntity.getJarFileId()).orElseThrow(() -> new FileNotFoundException("找不到對應的 Jar 檔案"));
        WebServiceHandler registerWebService = new WebServiceHandler();
        EndpointDTO endpointDTO = new EndpointDTO();
        BeanUtils.copyProperties(endpointEntity, endpointDTO);
        registerWebService.registerWebService(endpointDTO, context, jarFileEntity.getName());
        endpointEntity.setIsActive(Boolean.TRUE);
        try {
            endpointRepository.save(endpointEntity);
        } catch (EntityExistsException e) {
            // 處理實體已經存在的異常
            throw new EntityExistsException("實體已經存在：" + e.getMessage());
        } catch (OptimisticLockException e) {
            // 處理樂觀鎖定失敗的異常
            throw new OptimisticLockException("樂觀鎖定失敗：" + e.getMessage());
        } catch (PersistenceException e) {
            // 處理其他持久化異常
            throw new PersistenceException("持久化操作失敗：" + e.getMessage());
        }

    }

    @Override
    public void disabledWebService(String publicUrl, Boolean isDeleted) throws FileNotFoundException {
        EndpointEntity endpointEntity = endpointRepository.findById(publicUrl).orElseThrow(() -> new FileNotFoundException("找不到對應的 Web Service"));
        JarFileEntity jarFileEntity = jarFileRepository.findById(endpointEntity.getJarFileId()).orElseThrow(() -> new FileNotFoundException("找不到對應的 Jar 檔案"));
        WebServiceHandler registerWebService = new WebServiceHandler();
        try {
            registerWebService.removeWebService(publicUrl, bus, context, jarFileEntity.getName());
            endpointEntity.setIsActive(Boolean.FALSE);
            jarFileEntity.setStatus(JarFileStatus.INACTIVE);
            jarFileRepository.save(jarFileEntity);
            if (isDeleted) {
                endpointRepository.delete(endpointEntity);
            } else {
                endpointRepository.save(endpointEntity);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeWebService(String publishUrl) throws Exception {
        this.disabledWebService(publishUrl, true);
    }

}
