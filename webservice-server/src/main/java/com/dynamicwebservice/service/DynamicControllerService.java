package com.dynamicwebservice.service;

import com.dynamicwebservice.dto.ControllerDTO;

import java.util.List;

public interface DynamicControllerService {

    void register(ControllerDTO controllerDTO);
    List<ControllerDTO> getControllers();
}
