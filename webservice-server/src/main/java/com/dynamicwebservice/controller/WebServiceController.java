package com.dynamicwebservice.controller;

import com.dynamicwebservice.dto.EndpointResponse;
import com.dynamicwebservice.dto.MockResponseRequest;
import com.dynamicwebservice.dto.WebServiceRequest;
import com.dynamicwebservice.service.DynamicWebService;
import com.zipe.annotation.ResponseResultBody;
import com.zipe.dto.Result;
import com.zipe.enums.ResultStatus;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<List<EndpointResponse>> getEndpoints() throws SQLException {
        return ResponseEntity.ok().body(dynamicWebService.getEndpoints());
    }

    @PostMapping("/getResponseList")
    public ResponseEntity<String> getResponseList(@RequestBody MockResponseRequest request) {
        String content = dynamicWebService.getResponseContent(request);
        return ResponseEntity.ok().body(content);
    }

    @PostMapping("/registerWebService")
    public ResponseEntity<String> registerWebService(@RequestBody WebServiceRequest request) throws MalformedURLException, ClassNotFoundException, FileNotFoundException {
        dynamicWebService.registerWebService(request);
        return ResponseEntity.ok().body("Success");
    }

    @PostMapping("/removeWebService")
    public ResponseEntity<String> removeWebService(@RequestBody WebServiceRequest request) {
        dynamicWebService.removeWebService(request);
        return ResponseEntity.ok().body("Success");
    }

    public ResponseEntity<String> updateWebService(@RequestBody WebServiceRequest request) {
        dynamicWebService.updateWebService(request);
        return ResponseEntity.ok().body("Success");
    }

    @PostMapping("/addMockResponse")
    public ResponseEntity<String> addMockResponse(@RequestBody MockResponseRequest request) {
        dynamicWebService.addMockResponse(request);
        return ResponseEntity.ok().body("Success");
    }

    @PostMapping("/uploadJarFile")
    public Result<String> uploadJarFile(@RequestParam("file") MultipartFile file) {
        try {
            // 檢查檔案是否為空或不是以 .jar 結尾
            if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".jar")) {
                return Result.failure(ResultStatus.BAD_REQUEST, "Please select a .jar file to upload.");
            }

            // 執行實際的檔案上傳操作，這裡假設使用 dynamicWebService 來處理上傳
            String newFileName = dynamicWebService.uploadJarFile(file.getInputStream());

            // 返回成功上傳的訊息和新檔案名稱
            return Result.success(newFileName);
        } catch (IOException e) {
            // 如果發生 IO 錯誤，返回伺服器內部錯誤訊息
            return Result.failure(ResultStatus.INTERNAL_SERVER_ERROR, "Failed to upload file: " + e.getMessage());
        }
    }
}
