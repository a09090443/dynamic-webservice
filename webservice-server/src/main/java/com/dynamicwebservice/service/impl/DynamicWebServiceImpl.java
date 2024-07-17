package com.dynamicwebservice.service.impl;

import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.dto.MockResponseRequestDTO;
import com.dynamicwebservice.dto.MockResponseResponseDTO;
import com.dynamicwebservice.entity.EndpointEntity;
import com.dynamicwebservice.entity.JarFileEntity;
import com.dynamicwebservice.entity.MockResponseEntity;
import com.dynamicwebservice.enums.JarFileStatus;
import com.dynamicwebservice.exception.WebserviceException;
import com.dynamicwebservice.jdbc.EndPointJDBC;
import com.dynamicwebservice.jdbc.MockResponseJDBC;
import com.dynamicwebservice.model.WebServiceModel;
import com.dynamicwebservice.repository.EndpointRepository;
import com.dynamicwebservice.repository.JarFileRepository;
import com.dynamicwebservice.repository.MockResponseRepository;
import com.dynamicwebservice.service.DynamicWebService;
import com.dynamicwebservice.util.WebServiceHandler;
import com.zipe.enums.ResourceEnum;
import com.zipe.jdbc.criteria.Conditions;
import com.zipe.util.time.DateTimeUtils;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.Bus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DynamicWebServiceImpl extends CommonService implements DynamicWebService {

    private final ApplicationContext context;

    private final Bus bus;

    private final MockResponseRepository mockResponseRepository;

    private final EndpointRepository endpointRepository;

    private final JarFileRepository jarFileRepository;

    private final EndPointJDBC endPointJDBC;

    private final MockResponseJDBC mockResponseJDBC;

    private final String jarFileDir;

    DynamicWebServiceImpl(ApplicationContext context,
                          Bus bus,
                          MockResponseRepository mockResponseRepository,
                          EndpointRepository endpointRepository,
                          JarFileRepository jarFileRepository,
                          EndPointJDBC endPointJDBC, MockResponseJDBC mockResponseJDBC,
                          @Value("${jar.file.dir}") String jarFileDir) {
        this.context = context;
        this.bus = bus;
        this.mockResponseRepository = mockResponseRepository;
        this.endpointRepository = endpointRepository;
        this.jarFileRepository = jarFileRepository;
        this.endPointJDBC = endPointJDBC;
        this.mockResponseJDBC = mockResponseJDBC;
        this.jarFileDir = jarFileDir;
    }

    @Override
    public List<EndpointDTO> getEndpoints() {

        ResourceEnum resource = ResourceEnum.SQL.getResource(EndPointJDBC.SQL_SELECT_ENDPOINT_RELATED_JAR_FILE);
        List<WebServiceModel> webServiceModelList = endPointJDBC.queryForList(resource, new Conditions(), WebServiceModel.class);

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
    public String getResponseContent(MockResponseRequestDTO request) {

        if (StringUtils.isBlank(request.getPublishUrl())) {
            throw new WebserviceException("Publish URL is required");
        } else if (StringUtils.isBlank(request.getMethod())) {
            throw new WebserviceException("Method is required");
        } else if (StringUtils.isBlank(request.getCondition())) {
            throw new WebserviceException("Condition is required");
        }
        MockResponseEntity mockResponseEntity = mockResponseRepository.findByIdPublishUrlAndIdMethodAndIdConditionAndIsActive(request.getPublishUrl(), request.getMethod(), request.getCondition(), Boolean.TRUE);
        return Optional.ofNullable(mockResponseEntity).map(MockResponseEntity::getResponseContent).orElse("");
    }

    @Override
    public List<MockResponseResponseDTO> getResponseList(MockResponseRequestDTO request) {
        if (StringUtils.isBlank(request.getPublishUrl())) {
            throw new WebserviceException("Publish URL is required");
        }
        List<MockResponseEntity> mockResponseEntity = mockResponseRepository.findByIdPublishUrl(request.getPublishUrl());
        return mockResponseEntity.stream().map(mockResponse -> {
            MockResponseResponseDTO response = new MockResponseResponseDTO();
            response.setId(mockResponse.getUuId());
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
        JarFileEntity jarFileEntity = getJarFile(endpointDTO.getJarFileId());

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
        saveWebService(endpointDTO);
    }

    @Override
    public void saveMockResponse(MockResponseRequestDTO request) {
        MockResponseEntity mockResponseEntity = new MockResponseEntity();
        mockResponseEntity.setUuId(UUID.randomUUID().toString());
        mockResponseEntity.getId().setPublishUrl(request.getPublishUrl());
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
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("publishUrl", request.getPublishUrl());
        paramMap.put("method", request.getMethod());
        paramMap.put("condition", request.getCondition());
        paramMap.put("responseContent", request.getResponseContent());
        paramMap.put("id", request.getId());
        paramMap.put("updatedAt", DateTimeUtils.getDateNow());
        try {
            mockResponseJDBC.update(resource, paramMap);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("publishUrl:{}", request.getPublishUrl());
            log.error("method:{}", request.getMethod());
            log.error("condition:{}", request.getCondition());
            log.error("IncorrectResultSizeDataAccessException:{}", e.getMessage(), e);
            throw new WebserviceException("更新 Mock Response 失敗");
        }
    }

    @Override
    public void updateMockResponse(String oriPublishUrl, String newPublishUrl) {
        ResourceEnum resource = ResourceEnum.SQL.getResource(MockResponseJDBC.SQL_UPDATE_PUBLISH_URL_FOR_RESPONSE);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("oriPublishUrl", oriPublishUrl);
        paramMap.put("newPublishUrl", newPublishUrl);
        paramMap.put("updatedAt", DateTimeUtils.getDateNow());
        try {
            mockResponseJDBC.update(resource, paramMap);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("oriPublishUrl:{}", oriPublishUrl);
            log.error("newPublishUrl:{}", newPublishUrl);
            log.error("IncorrectResultSizeDataAccessException:{}", e.getMessage(), e);
            throw new WebserviceException("更新 Mock Response 失敗");
        }
    }

    @Override
    public void deleteMockResponse(String id) {
        ResourceEnum resource = ResourceEnum.SQL.getResource(MockResponseJDBC.SQL_DEL_RESPONSE);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        try {
            mockResponseJDBC.update(resource, paramMap);
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

    @Override
    public void enabledWebService(String publishUrl) throws FileNotFoundException {
        EndpointEntity endpointEntity = endpointRepository.findById(publishUrl).orElseThrow(() -> new FileNotFoundException("找不到對應的 Web Service"));
        JarFileEntity jarFileEntity = getJarFile(endpointEntity.getJarFileId());
        WebServiceHandler registerWebService = new WebServiceHandler();
        EndpointDTO endpointDTO = new EndpointDTO();

        try {
            BeanUtils.copyProperties(endpointEntity, endpointDTO);
            registerWebService.registerWebService(endpointDTO, context, jarFileEntity.getName());
            endpointEntity.setIsActive(Boolean.TRUE);
            endpointRepository.save(endpointEntity);
        } catch (RuntimeException | IOException | ClassNotFoundException e) {
            log.error("Web Service 註冊服務:{}, 失敗", endpointEntity.getBeanName(), e);
            throw new WebserviceException("啟動 Webservice 失敗");
        }

    }

    @Override
    public void disabledWebService(String publicUrl, Boolean isDeleted) throws FileNotFoundException {
        EndpointEntity endpointEntity = endpointRepository.findById(publicUrl).orElseThrow(() -> new FileNotFoundException("找不到對應的 Web Service"));
        JarFileEntity jarFileEntity = getJarFile(endpointEntity.getJarFileId());
        WebServiceHandler registerWebService = new WebServiceHandler();
        try {
            registerWebService.removeWebService(publicUrl, bus, context, jarFileEntity.getName());
            endpointEntity.setIsActive(Boolean.FALSE);
            jarFileEntity.setStatus(JarFileStatus.INACTIVE);
            jarFileRepository.save(jarFileEntity);
            if (Boolean.TRUE.equals(isDeleted)) {
                endpointRepository.delete(endpointEntity);
            } else {
                endpointRepository.save(endpointEntity);
            }
        } catch (Exception e) {
            log.error("Web Service 關閉服務:{}, 失敗", endpointEntity.getBeanName(), e);
            throw new WebserviceException("關閉 Webservice 失敗");
        }
    }

    @Override
    public void disabledJarFile(String publishUrl) throws Exception {
        ResourceEnum resource = ResourceEnum.SQL.getResource(EndPointJDBC.SQL_SELECT_ENDPOINT_RELATED_JAR_FILE);
        Conditions conditions = new Conditions();
        conditions.equal("e.PUBLISH_URL", publishUrl);
        List<WebServiceModel> webServiceModelList = endPointJDBC.queryForList(resource, new Conditions(), WebServiceModel.class);
        webServiceModelList.forEach(endpoint -> {
            try {
                JarFileEntity jarFileEntity = getJarFile(endpoint.getJarFileId());
                jarFileEntity.setStatus(JarFileStatus.INACTIVE);
                jarFileRepository.save(jarFileEntity);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
                throw new WebserviceException("關閉 Jar 檔案失敗");
            }
        });
    }

    @Override
    public void removeWebService(String publishUrl) throws Exception {

        try {
            this.disabledWebService(publishUrl, true);
        } catch (FileNotFoundException e) {
            log.error("移除 Web Service 失敗:{}", e.getMessage(), e);
            throw new WebserviceException("移除 Web Service 失敗");
        }
    }

    private JarFileEntity getJarFile(String jarFileId) throws FileNotFoundException {
        return jarFileRepository.findById(jarFileId).orElseThrow(() -> new FileNotFoundException("找不到對應的 Jar 檔案"));
    }
}
