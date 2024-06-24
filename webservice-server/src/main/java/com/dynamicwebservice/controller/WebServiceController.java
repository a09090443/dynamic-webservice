package com.dynamicwebservice.controller;

import com.dynamicwebservice.dto.EndpointResponse;
import com.dynamicwebservice.dto.MockResponseRequest;
import com.dynamicwebservice.dto.WebServiceRequest;
import com.dynamicwebservice.service.DynamicWebService;
import com.zipe.annotation.ResponseResultBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    public ResponseEntity<String> updateWebService(@RequestBody WebServiceRequest request) throws MalformedURLException, ClassNotFoundException {
        dynamicWebService.updateWebService(request);
        return ResponseEntity.ok().body("Success");
    }

    @PostMapping("/addMockResponse")
    public ResponseEntity<String> addMockResponse(@RequestBody MockResponseRequest request) {
        dynamicWebService.addMockResponse(request);
        return ResponseEntity.ok().body("Success");
    }

    @PostMapping("/uploadJarFile")
    public ResponseEntity<String> uploadJarFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".jar")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
            }

            String newFileName = dynamicWebService.uploadJarFile(file.getInputStream());
            return ResponseEntity.ok().body("File uploaded successfully: " + newFileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }
}
