package com.dynamicwebservice.controller;

import com.dynamicwebservice.dto.ControllerDTO;
import com.dynamicwebservice.dto.ControllerResponseDTO;
import com.dynamicwebservice.dto.EndpointDTO;
import com.dynamicwebservice.dto.EndpointResponseDTO;
import com.dynamicwebservice.service.DynamicControllerService;
import com.zipe.annotation.ResponseResultBody;
import com.zipe.dto.Result;
import com.zipe.enums.ResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@ResponseResultBody
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:4200") // 允許來自 http://localhost:4200 的請求
public class DynamicLoadController {

    private final DynamicControllerService dynamicControllerService;

    public DynamicLoadController(DynamicControllerService dynamicControllerService) {
        this.dynamicControllerService = dynamicControllerService;
    }

    @PostMapping("/registerController")
    public Result<String> registerController(@RequestBody ControllerDTO controllerDTO) {
        try {
            dynamicControllerService.register(controllerDTO);
            return Result.success("");
        } catch (Exception e) {
            return Result.failure(ResultStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getControllers")
    public Result<List<ControllerResponseDTO>> getControllers() {
        List<ControllerDTO> controllers = dynamicControllerService.getControllers();
        List<ControllerResponseDTO> endpointResponseList = controllers.stream().map(controllerDTO -> {
            ControllerResponseDTO controllerResponseDTO = new ControllerResponseDTO();
            BeanUtils.copyProperties(controllerDTO, controllerResponseDTO);
            return controllerResponseDTO;
        }).toList();
        return Result.success(endpointResponseList);
    }

}
