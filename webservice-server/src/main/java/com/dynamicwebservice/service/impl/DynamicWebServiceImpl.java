package com.dynamicwebservice.service.impl;

import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.entity.EndpointEntity;
import com.dynamicwebservice.entity.JarFileEntity;
import com.dynamicwebservice.enums.JarFileStatus;
import com.dynamicwebservice.exception.WebserviceException;
import com.dynamicwebservice.jdbc.EndPointJDBC;
import com.dynamicwebservice.model.WebServiceModel;
import com.dynamicwebservice.repository.EndpointRepository;
import com.dynamicwebservice.repository.JarFileRepository;
import com.dynamicwebservice.service.DynamicWebService;
import com.dynamicwebservice.util.WebServiceHandler;
import com.zipe.enums.ResourceEnum;
import com.zipe.jdbc.criteria.Conditions;
import com.zipe.util.file.FileUtil;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.Bus;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DynamicWebServiceImpl implements DynamicWebService {

    private final ApplicationContext context;

    private final Bus bus;

    private final EndpointRepository endpointRepository;

    private final JarFileRepository jarFileRepository;

    private final EndPointJDBC endPointJDBC;


    DynamicWebServiceImpl(ApplicationContext context,
                          Bus bus,
                          EndpointRepository endpointRepository,
                          JarFileRepository jarFileRepository,
                          EndPointJDBC endPointJDBC) {
        this.context = context;
        this.bus = bus;
        this.endpointRepository = endpointRepository;
        this.jarFileRepository = jarFileRepository;
        this.endPointJDBC = endPointJDBC;
    }

    @Override
    public List<EndpointDTO> getEndpoints() {

        ResourceEnum resource = ResourceEnum.SQL.getResource(EndPointJDBC.SQL_SELECT_ENDPOINT_RELATED_JAR_FILE);
        String sql = readFileFromJar(resource);
        List<WebServiceModel> webServiceModelList = endPointJDBC.queryForList(sql, new Conditions(), new HashMap<>(), WebServiceModel.class);

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
    public void saveWebService(EndpointDTO endpointDTO) throws FileNotFoundException {

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
    public void updateWebService(EndpointDTO endpointDTO) throws FileNotFoundException {
        saveWebService(endpointDTO);
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
    public void disabledJarFile(String publishUrl) {
        ResourceEnum resource = ResourceEnum.SQL.getResource(EndPointJDBC.SQL_SELECT_ENDPOINT_RELATED_JAR_FILE);
        String sql = readFileFromJar(resource);
        Conditions conditions = new Conditions();
        conditions.equal("e.PUBLISH_URL", publishUrl);
        List<WebServiceModel> webServiceModelList = endPointJDBC.queryForList(sql, new Conditions(), new HashMap<>(), WebServiceModel.class);
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

    private String readFileFromJar(ResourceEnum resource) {
        StringBuilder path = new StringBuilder();
        path.append(resource.dir());
        path.append(resource.file());
        path.append(resource.extension());
        try {
            File jarFile = FileUtil.getFileFromClasspath(path.toString());
            FileInputStream fis = new FileInputStream(jarFile);
            // 檢查資源是否存在
            if (!jarFile.exists()) {
                throw new WebserviceException("File not found: " + jarFile.getAbsoluteFile().getAbsolutePath());
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fis, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new WebserviceException("Error reading file from JAR: " + path.toString(), e);
        }
    }

    private JarFileEntity getJarFile(String jarFileId) throws FileNotFoundException {
        return jarFileRepository.findById(jarFileId).orElseThrow(() -> new FileNotFoundException("找不到對應的 Jar 檔案"));
    }
}
