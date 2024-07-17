package com.dynamicwebservice.service;

import com.dynamicwebservice.dto.ControllerDTO;
import com.dynamicwebservice.dto.JarFileResponseDTO;

import java.io.InputStream;

public interface DynamicControllerService {

    void register(ControllerDTO controllerDTO);

    JarFileResponseDTO uploadJarFile(InputStream inputStream);

}
