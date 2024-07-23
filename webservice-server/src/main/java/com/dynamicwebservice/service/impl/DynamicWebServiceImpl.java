package com.dynamicwebservice.service.impl;

import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.entity.EndpointEntity;
import com.dynamicwebservice.entity.JarFileEntity;
import com.dynamicwebservice.enums.JarFileStatus;
import com.dynamicwebservice.exception.WebserviceException;
import com.dynamicwebservice.jdbc.EndPointJDBC;
import com.dynamicwebservice.model.WebServiceModel;
import com.dynamicwebservice.repository.EndpointRepository;
import com.dynamicwebservice.service.DynamicWebService;
import com.dynamicwebservice.util.WebServiceHandler;
import com.zipe.enums.ResourceEnum;
import com.zipe.jdbc.criteria.Conditions;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DynamicWebServiceImpl extends BaseService implements DynamicWebService {

    private final Bus bus;

    private final EndpointRepository endpointRepository;

    private final EndPointJDBC endPointJDBC;

    DynamicWebServiceImpl(Bus bus,
                          EndpointRepository endpointRepository,
                          EndPointJDBC endPointJDBC) {
        this.bus = bus;
        this.endpointRepository = endpointRepository;
        this.endPointJDBC = endPointJDBC;
    }

    @Override
    public List<EndpointDTO> getEndpoints() {

        ResourceEnum resource = ResourceEnum.SQL.getResource(EndPointJDBC.SQL_SELECT_ENDPOINT_RELATED_JAR_FILE);
        List<WebServiceModel> webServiceModelList = endPointJDBC.queryForList(resource, new Conditions(), WebServiceModel.class);

        return webServiceModelList.stream().map(endpoint -> {
            EndpointDTO dto = new EndpointDTO();
            dto.setId(endpoint.getId());
            dto.setPublishUri(endpoint.getPublishUri());
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
    public void saveWebService(EndpointDTO endpointDTO) {

        EndpointEntity endpointEntity = new EndpointEntity();
        endpointEntity.setUuId(UUID.randomUUID().toString());
        endpointEntity.setPublishUri(endpointDTO.getPublishUri());
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
    public void updateWebService(EndpointDTO endpointDTO) {
        saveWebService(endpointDTO);
    }

    @Override
    public void enabledWebService(String publishUri) {
        EndpointEntity endpointEntity = endpointRepository.findById(publishUri).orElseThrow(() -> new WebserviceException("找不到對應的 Web Service"));
        JarFileEntity jarFileEntity = getJarFile(endpointEntity.getJarFileId());
        WebServiceHandler registerWebService = new WebServiceHandler();
        EndpointDTO endpointDTO = new EndpointDTO();

        try {
            BeanUtils.copyProperties(endpointEntity, endpointDTO);
            registerWebService.registerWebService(dynamicClassLoader, endpointDTO, context, jarFileEntity.getName());
            endpointEntity.setIsActive(Boolean.TRUE);
            endpointRepository.save(endpointEntity);
        } catch (RuntimeException | IOException | ClassNotFoundException e) {
            log.error("Web Service 註冊服務:{}, 失敗", endpointEntity.getBeanName(), e);
            throw new WebserviceException("啟動 Webservice 失敗");
        }

    }

    @Override
    public void disabledWebService(String publicUri, Boolean isDeleted) {
        EndpointEntity endpointEntity = endpointRepository.findById(publicUri).orElseThrow(() -> new WebserviceException("找不到對應的 Web Service"));
        JarFileEntity jarFileEntity = getJarFile(endpointEntity.getJarFileId());
        WebServiceHandler registerWebService = new WebServiceHandler();
        try {
            registerWebService.removeWebService(dynamicClassLoader, publicUri, bus, context, jarFileEntity.getName());
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
    public void disabledJarFile(String publishUrl) {
        ResourceEnum resource = ResourceEnum.SQL.getResource(EndPointJDBC.SQL_SELECT_ENDPOINT_RELATED_JAR_FILE);
        Conditions conditions = new Conditions();
        conditions.equal("e.PUBLISH_URL", publishUrl);
        List<WebServiceModel> webServiceModelList = endPointJDBC.queryForList(resource, new Conditions(), WebServiceModel.class);
        webServiceModelList.forEach(endpoint -> {
            try {
                JarFileEntity jarFileEntity = getJarFile(endpoint.getJarFileId());
                jarFileEntity.setStatus(JarFileStatus.INACTIVE);
                jarFileRepository.save(jarFileEntity);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new WebserviceException("關閉 Jar 檔案失敗");
            }
        });
    }

    @Override
    public void removeWebService(String publishUri) {
        try {
            this.disabledWebService(publishUri, true);
        } catch (Exception e) {
            log.error("移除 Web Service 失敗:{}", e.getMessage(), e);
            throw new WebserviceException("移除 Web Service 失敗");
        }
    }
}
