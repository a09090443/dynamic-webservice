package com.dynamicwebservice.service;

import com.dynamicwebservice.dto.ControllerDTO;

import java.util.List;

public interface DynamicControllerService {

    void saveController(ControllerDTO controllerDTO);

    List<ControllerDTO> getControllers();

    ControllerDTO getController(String id);

    void updateController(ControllerDTO controllerDTO);

    void enabledController(String publishUri);

    void disabledController(String publishUri, Boolean isDeleted);

    void removeController(String publishUri);

    void startUpControllerProcess(String publishUri, String classPath, String jarPath);
}
