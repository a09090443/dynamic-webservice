package com.dynamicwebservice.service;

import com.dynamicwebservice.dto.EndpointResponse;
import com.dynamicwebservice.dto.MockResponseRequest;
import com.dynamicwebservice.dto.WebServiceRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

public interface DynamicWebService {
    List<EndpointResponse> getEndpoints();
    String getResponseContent(MockResponseRequest request);
    void registerWebService(WebServiceRequest request) throws MalformedURLException, ClassNotFoundException, FileNotFoundException;
    void removeWebService(WebServiceRequest request);
    void updateWebService(WebServiceRequest request);
    void addMockResponse(MockResponseRequest request);
    String uploadJarFile(InputStream inputStream) throws IOException;
}
