package com.dynamicwebservice.controller;

import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.dto.EndpointResponse;
import com.dynamicwebservice.dto.JarFileResponse;
import com.dynamicwebservice.dto.MockResponseRequest;
import com.dynamicwebservice.dto.MockResponseResponse;
import com.dynamicwebservice.dto.WebServiceRequest;
import com.dynamicwebservice.service.DynamicWebService;
import com.zipe.annotation.ResponseResultBody;
import com.zipe.dto.Result;
import com.zipe.enums.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

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
    public Result<List<EndpointResponse>> getEndpoints() throws SQLException {
        return Result.success(dynamicWebService.getEndpoints());
    }

    @PostMapping("/getResponseContent")
    public Result<String> getResponseContent(@RequestBody MockResponseRequest request) {
        if (StringUtils.isBlank(request.getPublishUrl())) {
            return Result.failure(ResultStatus.BAD_REQUEST, "Publish URL is required");
        } else if (StringUtils.isBlank(request.getMethod())) {
            return Result.failure(ResultStatus.BAD_REQUEST, "Method is required");
        } else if (StringUtils.isBlank(request.getCondition())) {
            return Result.failure(ResultStatus.BAD_REQUEST, "Condition is required");
        }
        String content = dynamicWebService.getResponseContent(request);
        return Result.success(content);
    }

    @PostMapping("/getResponseList")
    public Result<List<MockResponseResponse>> getResponseList(@RequestBody MockResponseRequest request) {
        if (StringUtils.isBlank(request.getPublishUrl())) {
            return Result.failure(ResultStatus.BAD_REQUEST);
        }
        List<MockResponseResponse> mockResponseResponseList = dynamicWebService.getResponseList(request);
        return Result.success(mockResponseResponseList);
    }

    @PostMapping("/registerWebService")
    public Result<EndpointResponse> registerWebService(@RequestBody WebServiceRequest request) throws MalformedURLException, ClassNotFoundException, FileNotFoundException {
        log.info("Register web service: {}", request);
        EndpointDTO endpointDTO = new EndpointDTO();
        BeanUtils.copyProperties(request, endpointDTO);

        try {
            dynamicWebService.saveWebService(endpointDTO);
        } catch (Exception e) {
            log.error("Register web service failed", e);
            return Result.failure(ResultStatus.BAD_REQUEST);
        }

        EndpointResponse response = new EndpointResponse();
        BeanUtils.copyProperties(endpointDTO, response);
        return Result.success(response);
    }

    @GetMapping("/removeWebService")
    public Result<String> removeWebService(@RequestBody WebServiceRequest request) throws Exception {
        dynamicWebService.disabledWebService(request.getPublishUrl(), true);
        return Result.success(StringUtils.EMPTY);
    }

    @GetMapping("/switchWebService")
    public ResponseEntity<String> switchWebService(@RequestParam String publishUrl, @RequestParam Boolean isActive) {
        if (StringUtils.isNotBlank(publishUrl) && isActive != null) {
            try {
                if (isActive) {
                    dynamicWebService.enabledWebService(publishUrl);
                } else {
                    dynamicWebService.disabledWebService(publishUrl, false);
                }
            } catch (Exception e) {
                log.error("Switch endpoint failure, publishUrl:{}, isActive:{}", publishUrl, isActive, e);
            }
        }
        return ResponseEntity.ok().body("Success");
    }

    @PostMapping("/saveMockResponse")
    public Result<String> saveMockResponse(@RequestBody MockResponseRequest request) {
        dynamicWebService.saveMockResponse(request);
        return Result.success("Success");
    }

    @PostMapping("/uploadJarFile")
    public Result<JarFileResponse> uploadJarFile(@RequestParam("file") MultipartFile file) {
        try {
            // 檢查檔案是否為空或不是以 .jar 結尾
            if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".jar")) {
                log.error("Upload jar file failed: file is empty or not a jar file");
                return Result.failure(ResultStatus.BAD_REQUEST);
            }

            // 執行實際的檔案上傳操作，這裡假設使用 dynamicWebService 來處理上傳
            JarFileResponse jarFileResponse = dynamicWebService.uploadJarFile(file.getInputStream());

            // 返回成功上傳的訊息和新檔案編號
            return Result.success(jarFileResponse);
        } catch (IOException e) {
            // 如果發生 IO 錯誤，返回伺服器內部錯誤訊息
            log.error("Upload jar file failed", e);
            return Result.failure(ResultStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
