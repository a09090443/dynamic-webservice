package com.dynamicwebservice.service;

import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.dto.EndpointResponse;
import com.dynamicwebservice.dto.JarFileResponse;
import com.dynamicwebservice.dto.MockResponseRequest;
import com.dynamicwebservice.dto.WebServiceRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

public interface DynamicWebService {
    List<EndpointResponse> getEndpoints() throws SQLException;
    String getResponseContent(MockResponseRequest request);
    void saveWebService(EndpointDTO endpointDTO) throws FileNotFoundException;
    void enabledWebService(String publishUrl) throws MalformedURLException, ClassNotFoundException, FileNotFoundException;
    void disabledWebService(String publicUrl, Boolean isDeleted) throws Exception;
    void removeWebService(String publishUrl) throws Exception;
    void addMockResponse(MockResponseRequest request);
    JarFileResponse uploadJarFile(InputStream inputStream) throws IOException;
}
