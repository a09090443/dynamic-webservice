package com.dynamicwebservice.service;

import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.dto.JarFileResponseDTO;
import com.dynamicwebservice.dto.MockResponseRequestDTO;
import com.dynamicwebservice.dto.MockResponseResponseDTO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.List;

public interface DynamicWebService {
    List<EndpointDTO> getEndpoints() throws SQLException;
    EndpointDTO getEndpoint(String id);
    String getResponseContent(MockResponseRequestDTO request);
    List<MockResponseResponseDTO> getResponseList(MockResponseRequestDTO request);
    void saveWebService(EndpointDTO endpointDTO) throws FileNotFoundException;
    void updateWebService(EndpointDTO endpointDTO) throws FileNotFoundException;
    void enabledWebService(String publishUrl) throws MalformedURLException, ClassNotFoundException, FileNotFoundException;
    void disabledWebService(String publicUrl, Boolean isDeleted) throws Exception;
    void disabledJarFile(String publishUrl) throws Exception;
    void removeWebService(String publishUrl) throws Exception;
    void saveMockResponse(MockResponseRequestDTO request);
    void updateMockResponse(MockResponseRequestDTO request);
    void updateMockResponse(String oriPublishUrl, String newPublishUrl);
    void deleteMockResponse(String id);
    void switchMockResponse(String id, Boolean status);
    JarFileResponseDTO uploadJarFile(InputStream inputStream) throws IOException;
}
