package com.dynamicwebservice.service;

import com.dynamicwebservice.dto.EndpointDTO;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

public interface DynamicWebService {
    List<EndpointDTO> getEndpoints() throws SQLException;
    EndpointDTO getEndpoint(String id);
    void saveWebService(EndpointDTO endpointDTO) throws FileNotFoundException;
    void updateWebService(EndpointDTO endpointDTO) throws FileNotFoundException;
    void enabledWebService(String publishUri) throws Exception;
    void disabledWebService(String publicUri, Boolean isDeleted) throws Exception;
    void disabledJarFile(String publishUri) throws Exception;
    void removeWebService(String publishUri) throws Exception;
}
