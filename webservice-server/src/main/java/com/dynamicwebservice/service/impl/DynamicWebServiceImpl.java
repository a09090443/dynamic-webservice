package com.dynamicwebservice.service.impl;

import com.dynamicwebservice.dao.MockResponseDao;
import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.dto.JarFileResponse;
import com.dynamicwebservice.dto.MockResponseRequest;
import com.dynamicwebservice.dto.MockResponseResponse;
import com.dynamicwebservice.entity.EndpointEntity;
import com.dynamicwebservice.entity.JarFileEntity;
import com.dynamicwebservice.entity.MockResponseEntity;
import com.dynamicwebservice.enums.JarFileStatus;
import com.dynamicwebservice.jdbc.EndPointJDBC;
import com.dynamicwebservice.model.WebServiceModel;
import com.dynamicwebservice.repository.EndpointRepository;
import com.dynamicwebservice.repository.JarFileRepository;
import com.dynamicwebservice.repository.MockResponseRepository;
import com.dynamicwebservice.service.DynamicWebService;
import com.dynamicwebservice.util.WebServiceHandler;
import com.zipe.enums.ResourceEnum;
import com.zipe.jdbc.criteria.Conditions;
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

    private final EndPointJDBC endPointJDBC;

    private final String jarFileDir;

    DynamicWebServiceImpl(ApplicationContext context,
                          Bus bus,
                          MockResponseRepository mockResponseRepository,
                          MockResponseDao mockResponseDao,
                          EndpointRepository endpointRepository,
                          JarFileRepository jarFileRepository,
                          EndPointJDBC endPointJDBC,
                          @Value("${jar.file.dir}") String jarFileDir) {
        this.context = context;
        this.bus = bus;
        this.mockResponseRepository = mockResponseRepository;
        this.mockResponseDao = mockResponseDao;
        this.endpointRepository = endpointRepository;
        this.jarFileRepository = jarFileRepository;
        this.endPointJDBC = endPointJDBC;
        this.jarFileDir = jarFileDir;
    }

    @Override
    public List<EndpointDTO> getEndpoints() {

        ResourceEnum resource = ResourceEnum.SQL.getResource(EndPointJDBC.SQL_SELECT_ENDPOINT_RELATED_JAR_FILE);
        List<WebServiceModel> webServiceModelList = endPointJDBC.queryForList(resource, new Conditions(), new HashMap<>(), WebServiceModel.class);

        return webServiceModelList.stream().map(endpoint -> {
            EndpointDTO dto = new EndpointDTO();
            dto.setId(endpoint.getId());
            dto.setPublishUrl(endpoint.getPublishUrl());
            dto.setClassPath(endpoint.getClassPath());
            dto.setBeanName(endpoint.getBeanName());
            dto.setIsActive(endpoint.getIsActive());
            dto.setJarFileId(endpoint.getJarFileId());
            dto.setJarFileName(endpoint.getJarFileName());
            return dto;
        }).toList();
    }

    @Override
    public EndpointDTO getEndpoint(String id) {
        EndpointEntity endpointEntity = endpointRepository.findByUuId(id);
        if (endpointEntity != null) {
            EndpointDTO dto = new EndpointDTO();
            BeanUtils.copyProperties(endpointEntity, dto);
            return dto;
        }
        return null;
    }

    @Override
    public String getResponseContent(MockResponseRequest request) {
        MockResponseEntity mockResponseEntity = mockResponseRepository.findByIdPublishUrlAndIdMethodAndIdConditionAndIsActive(request.getPublishUrl(), request.getMethod(), request.getCondition(), Boolean.TRUE);
        return Optional.ofNullable(mockResponseEntity).map(MockResponseEntity::getResponseContent).orElse("");
    }

    @Override
    public List<MockResponseResponse> getResponseList(MockResponseRequest request) {
        List<MockResponseEntity> mockResponseEntity = mockResponseRepository.findByIdPublishUrl(request.getPublishUrl());
        return mockResponseEntity.stream().map(mockResponse -> {
            MockResponseResponse response = new MockResponseResponse();
            response.setPublishUrl(mockResponse.getId().getPublishUrl());
            response.setMethod(mockResponse.getId().getMethod());
            response.setCondition(mockResponse.getId().getCondition());
            response.setResponseContent(mockResponse.getResponseContent());
            response.setIsActive(mockResponse.getIsActive());
            return response;
        }).toList();
    }

    @Override
    public void saveWebService(EndpointDTO endpointDTO) throws FileNotFoundException {

        EndpointEntity endpointEntity = new EndpointEntity();
        endpointEntity.setUuId(UUID.randomUUID().toString());
        endpointEntity.setPublishUrl(endpointDTO.getPublishUrl());
        endpointEntity.setClassPath(endpointDTO.getClassPath());
        endpointEntity.setBeanName(endpointDTO.getBeanName());
        endpointEntity.setIsActive(Boolean.FALSE);
        endpointEntity.setJarFileId(endpointDTO.getJarFileId());
        endpointDTO.setId(endpointEntity.getUuId());
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
    public void updateWebService(EndpointDTO endpointDTO) throws FileNotFoundException {
        EndpointEntity endpointEntity = endpointRepository.findByUuId(endpointDTO.getId());
        saveWebService(endpointDTO);
        if (!endpointDTO.getPublishUrl().equals(endpointEntity.getPublishUrl())) {
            endpointRepository.delete(endpointEntity);
        }
    }

    @Override
    public void saveMockResponse(MockResponseRequest request) {
        MockResponseEntity mockResponseEntity = new MockResponseEntity();
        mockResponseEntity.getId().setPublishUrl(request.getPublishUrl());
        mockResponseEntity.getId().setMethod(request.getMethod());
        mockResponseEntity.getId().setCondition(request.getCondition());
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
    public void disabledJarFile(String publishUrl) throws Exception {
        ResourceEnum resource = ResourceEnum.SQL.getResource(EndPointJDBC.SQL_SELECT_ENDPOINT_RELATED_JAR_FILE);
        Conditions conditions = new Conditions();
        conditions.equal("e.PUBLISH_URL", publishUrl);
        List<WebServiceModel> webServiceModelList = endPointJDBC.queryForList(resource, new Conditions(), new HashMap<>(), WebServiceModel.class);
        webServiceModelList.forEach(endpoint -> {
            try {
                JarFileEntity jarFileEntity = jarFileRepository.findById(endpoint.getJarFileId()).orElseThrow(() -> new FileNotFoundException("找不到對應的 Jar 檔案"));
                jarFileEntity.setStatus(JarFileStatus.INACTIVE);
                jarFileRepository.save(jarFileEntity);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            }
        });
    }

    @Override
    public void removeWebService(String publishUrl) throws Exception {
        this.disabledWebService(publishUrl, true);
    }

}
