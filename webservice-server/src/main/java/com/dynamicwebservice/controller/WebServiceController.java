package com.dynamicwebservice.controller;

import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.dto.EndpointResponseDTO;
import com.dynamicwebservice.dto.JarFileResponseDTO;
import com.dynamicwebservice.dto.MockResponseRequestDTO;
import com.dynamicwebservice.dto.MockResponseResponseDTO;
import com.dynamicwebservice.dto.WebServiceRequestDTO;
import com.dynamicwebservice.exception.WebserviceException;
import com.dynamicwebservice.service.DynamicWebService;
import com.zipe.annotation.ResponseResultBody;
import com.zipe.dto.Result;
import com.zipe.enums.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RestController
@ResponseResultBody
@RequestMapping(value = "/ws", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:4200") // 允許來自 http://localhost:4200 的請求
public class WebServiceController {

    private final DynamicWebService dynamicWebService;

    WebServiceController(DynamicWebService dynamicWebService) {
        this.dynamicWebService = dynamicWebService;
    }

    @GetMapping("/getEndpoints")
    public Result<List<EndpointResponseDTO>> getEndpoints() throws SQLException {
        List<EndpointDTO> endpoints = dynamicWebService.getEndpoints();
        List<EndpointResponseDTO> endpointResponseList = endpoints.stream().map(endpointDTO -> {
            EndpointResponseDTO response = new EndpointResponseDTO();
            BeanUtils.copyProperties(endpointDTO, response);
            return response;
        }).toList();
        return Result.success(endpointResponseList);
    }

    @PostMapping("/getResponseContent")
    public Result<String> getResponseContent(@RequestBody MockResponseRequestDTO request) {
        String content = dynamicWebService.getResponseContent(request);
        return Result.success(content);
    }

    @PostMapping("/getResponseList")
    public Result<List<MockResponseResponseDTO>> getResponseList(@RequestBody MockResponseRequestDTO request) {
        List<MockResponseResponseDTO> mockResponseResponseList = dynamicWebService.getResponseList(request);
        return Result.success(mockResponseResponseList);
    }

    @PostMapping("/saveWebService")
    public Result<EndpointResponseDTO> saveWebService(@RequestBody WebServiceRequestDTO request) {
        log.info("Save web service: {}", request);

        if (StringUtils.isBlank(request.getPublishUrl())) {
            log.error("PublishUrl is blank");
        }
        if (StringUtils.isBlank(request.getBeanName())) {
            log.error("BeanName is blank");
        }
        if (StringUtils.isBlank(request.getClassPath())) {
            log.error("ClassPath is blank");
        }

        if (StringUtils.isBlank(request.getPublishUrl()) ||
                StringUtils.isBlank(request.getBeanName()) ||
                StringUtils.isBlank(request.getClassPath())) {
            return Result.failure(ResultStatus.BAD_REQUEST);
        }

        EndpointDTO endpointDTO = new EndpointDTO();
        BeanUtils.copyProperties(request, endpointDTO);

        try {
            dynamicWebService.saveWebService(endpointDTO);
        } catch (Exception e) {
            log.error("Register web service failed", e);
            return Result.failure(ResultStatus.BAD_REQUEST);
        }

        EndpointResponseDTO response = new EndpointResponseDTO();
        BeanUtils.copyProperties(endpointDTO, response);
        return Result.success(response);
    }

    @PostMapping("/updateWebService")
    public Result<EndpointResponseDTO> updateWebService(@RequestBody WebServiceRequestDTO request) {
        log.info("Update web service: {}", request);
        EndpointDTO endpointDTO;
        EndpointDTO newEndpointDTO = new EndpointDTO();

        try {
            endpointDTO = Optional.ofNullable(
                    dynamicWebService.getEndpoint(request.getId())).orElseThrow(() -> new WebserviceException("Endpoint not found"));
            dynamicWebService.disabledWebService(endpointDTO.getPublishUrl(), false);

            BeanUtils.copyProperties(request, newEndpointDTO);
            newEndpointDTO.setIsActive(false);
            dynamicWebService.updateWebService(newEndpointDTO);

            if (!endpointDTO.getPublishUrl().equals(newEndpointDTO.getPublishUrl())) {
                dynamicWebService.removeWebService(endpointDTO.getPublishUrl());
                dynamicWebService.updateMockResponse(endpointDTO.getPublishUrl(), newEndpointDTO.getPublishUrl());
            }

        } catch (Exception e) {
            log.error("Update web service failed:{}", e.getMessage(), e);
            return Result.failure(ResultStatus.BAD_REQUEST);
        }

        EndpointResponseDTO response = new EndpointResponseDTO();
        BeanUtils.copyProperties(newEndpointDTO, response);
        return Result.success(response);
    }

    @DeleteMapping("/removeWebService")
    public Result<String> removeWebService(@RequestBody String[] publishUrls) throws Exception {
        for (String publishUrl : publishUrls) {
            dynamicWebService.removeWebService(publishUrl);
        }
        return Result.success(StringUtils.EMPTY);
    }

    @GetMapping("/switchWebService")
    public Result<String> switchWebService(@RequestParam String publishUrl, @RequestParam Boolean isActive) throws Exception {
        if (StringUtils.isNotBlank(publishUrl) && isActive != null) {
            if (isActive) {
                dynamicWebService.enabledWebService(publishUrl);
            } else {
                dynamicWebService.disabledWebService(publishUrl, false);
            }
        }
        return Result.success(StringUtils.EMPTY);
    }

    @PostMapping("/saveMockResponse")
    public Result<MockResponseResponseDTO> saveMockResponse(@RequestBody MockResponseRequestDTO request) {
        log.info("Save mock response: {}", request);

        if (StringUtils.isBlank(request.getPublishUrl())) {
            log.error("PublishUrl is blank");
        }
        if (StringUtils.isBlank(request.getMethod())) {
            log.error("Method is blank");
        }
        if (StringUtils.isBlank(request.getCondition())) {
            log.error("Condition is blank");
        }
        if (StringUtils.isBlank(request.getPublishUrl()) ||
                StringUtils.isBlank(request.getMethod()) ||
                StringUtils.isBlank(request.getCondition())) {
            return Result.failure(ResultStatus.BAD_REQUEST);
        }

        dynamicWebService.saveMockResponse(request);
        MockResponseResponseDTO mockResponseResponse = new MockResponseResponseDTO();
        BeanUtils.copyProperties(request, mockResponseResponse);

        return Result.success(mockResponseResponse);
    }

    @PostMapping("/updateResponse")
    public Result<MockResponseResponseDTO> updateResponse(@RequestBody MockResponseRequestDTO request) {
        log.info("Update response: {}", request);
        try {
            dynamicWebService.updateMockResponse(request);
        } catch (Exception e) {
            log.error("Register web service failed:{}", e.getMessage(), e);
            return Result.failure(ResultStatus.BAD_REQUEST);
        }

        MockResponseResponseDTO response = new MockResponseResponseDTO();
        BeanUtils.copyProperties(request, response);
        return Result.success(response);
    }

    @DeleteMapping("/deleteResponse")
    public Result<String> deleteResponse(@RequestBody String[] ids) {
        for (String id : ids) {
            dynamicWebService.deleteMockResponse(id);
        }
        return Result.success(StringUtils.EMPTY);
    }

    @GetMapping("/switchResponse")
    public Result<String> switchResponse(@RequestParam String id, @RequestParam Boolean isActive) {
        if (StringUtils.isNotBlank(id) && isActive != null) {
            try {
                dynamicWebService.switchMockResponse(id, isActive);
            } catch (Exception e) {
                log.error("Switch endpoint failure, id:{}, isActive:{}", id, isActive, e);
            }
        }
        return Result.success(StringUtils.EMPTY);
    }

    @PostMapping("/uploadJarFile")
    public Result<JarFileResponseDTO> uploadJarFile(@RequestParam("file") MultipartFile file) {
        try {
            // 檢查檔案是否為空或不是以 .jar 結尾
            if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".jar")) {
                log.error("Upload jar file failed: file is empty or not a jar file");
                return Result.failure(ResultStatus.BAD_REQUEST);
            }

            // 執行實際的檔案上傳操作，這裡假設使用 dynamicWebService 來處理上傳
            JarFileResponseDTO jarFileResponse = dynamicWebService.uploadJarFile(file.getInputStream());

            // 返回成功上傳的訊息和新檔案編號
            return Result.success(jarFileResponse);
        } catch (IOException e) {
            // 如果發生 IO 錯誤，返回伺服器內部錯誤訊息
            log.error("Upload jar file failed", e);
            return Result.failure(ResultStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
